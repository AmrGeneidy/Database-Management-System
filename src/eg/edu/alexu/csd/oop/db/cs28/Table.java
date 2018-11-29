package eg.edu.alexu.csd.oop.db.cs28;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import eg.edu.alexu.csd.oop.db.cs28.Parser.returnType;

public class Table {

	private static Table singleTable;

	private static String tableFullPathXML;
	private static String tableFullPathDTD;
	private static ArrayList<Record> tableData;
	private static String[] colsNames;
	private static String[] colsDataTypes;

	private Table() {
	}

	// clear Cache and get new instance
	public static Table loadNewTable(String xmlPath) {
		if (singleTable != null) {
			saveDataInXML();
		}
		if (tableFullPathXML != null && xmlPath.equalsIgnoreCase(tableFullPathXML)) {
			return singleTable;
		}
		singleTable = new Table();
		tableFullPathXML = xmlPath;
		tableFullPathDTD = getDTDPath();
		loadData();
		return singleTable;
	}

	// setters & getters
	public ArrayList<Record> getTableData() {
		return tableData;
	}

	public void setTableData(ArrayList<Record> tableData) {
		Table.tableData = tableData;
	}

	public String[] getColsNames() {
		return colsNames;
	}

	public String[] getColsDataTypes() {
		return colsDataTypes;
	}

	// MUST be called before finishing any method
	public void save() {
		saveDataInXML();
	}

	// TODO convert to HashMap
	public int insert(String[] colNames, String[] values) {
		return ModifyTable.insert(singleTable, colNames, values);
	}

	public int update(HashMap<returnType, Object> map) {
		return ModifyTable.update(singleTable, map);
	}
	
	public int delete(HashMap<returnType, Object> map) {
		return ModifyTable.delete(singleTable, map);
	}

	// save the data from table(cache) in xml file
	private static void saveDataInXML() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			File file = new File(tableFullPathXML);
			Document doc = builder.parse(file);
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();

			while (root.hasChildNodes()) {
				root.removeChild(root.getFirstChild());
			}

			Element xmlRecord;
			for (Record record : tableData) {
				xmlRecord = doc.createElement("record");
				for (int i = 0; i < record.length(); i++) {
					Element tag = doc.createElement(colsNames[i]);
					Text value = doc.createTextNode(record.getItem(i));
					tag.appendChild(value);
					xmlRecord.appendChild(tag);
				}
				root.appendChild(xmlRecord);
			}
			file.delete();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(tableFullPathXML));
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException | IOException e) {
			// TODO table not found
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO error while writing into file
			e.printStackTrace();
		}

	}

	// fill colsNames & colsDataTypes
	private static void readDTD() {
		BufferedReader reader = null;
		Pattern pattern = Pattern.compile("<!ELEMENT (\\S+) (\\S+)>");
		Matcher matcher = null;
		ArrayList<String> names = new ArrayList<>();
		ArrayList<String> dataTypes = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(tableFullPathDTD));
			reader.readLine();
			String thisLine = reader.readLine();
			while (thisLine != null) {
				matcher = pattern.matcher(thisLine);
				matcher.matches();
				names.add(matcher.group(1));
				dataTypes.add(matcher.group(2));
				thisLine = reader.readLine();
			}
			colsNames = names.toArray(new String[names.size()]);
			colsDataTypes = dataTypes.toArray(new String[dataTypes.size()]);
			reader.close();
		} catch (IOException e) {
			// TODO DTD not found
			System.out.println("DTD file not found");
			e.printStackTrace();
		}
	}

	private static void loadData() {
		readDTD();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(tableFullPathXML));
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("record");
			tableData = new ArrayList<Record>();
			String[] record = new String[colsNames.length];
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					for (int j = 0; j < colsNames.length; j++) {
						Element eElement = (Element) nNode;
						String value = eElement.getElementsByTagName(colsNames[j]).item(0).getTextContent();
						record[j] = value;
					}
					tableData.add(i, new Record(record));
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getDTDPath() {
		String ans = tableFullPathXML.substring(0, tableFullPathXML.length() - 3) + "dtd";
		return ans;
	}
}
