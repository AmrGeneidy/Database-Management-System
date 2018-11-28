package eg.edu.alexu.csd.oop.db.cs28;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

//Cache cleared when we change the table we work on OR close the program 
public class Table {

	private static Table singleTable;

	private static String tableFullPathXML;
	private static String tableFullPathDTD;
	private static Record[] tableData;
	private static String[] colsNames;
	private static String[] colsDataTypes;

	private Table() {
	}

	// clear Cache and get new instance
	public static Table loadNewTable(String xmlPath) {
		if (singleTable != null) {
			saveDataInXML();
		}
		singleTable = new Table();
		tableFullPathXML = xmlPath;
		tableFullPathDTD = getDTDPath();
		loadData();
		return singleTable;
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
			// TODO DTD not found return null
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
			tableData = new Record[nList.getLength()];
			Item[] record = new Item[colsNames.length];
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					for(int j = 0; j < colsNames.length; j++) {
						Element eElement = (Element) nNode;
						String value = eElement.getElementsByTagName(colsNames[j]).item(0).getTextContent();
						Item x = new Item(colsNames[j], colsDataTypes[j], value);
						record[j] = x;
					}
					tableData[i] = new Record(record);
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// save the data from table(cache) in xml file
	public static void saveDataInXML() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(tableFullPathXML));
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();
			Element xmlRecord;
			for (Record record : tableData) {
				xmlRecord = doc.createElement("record");
				for (int i = 0; i < record.length(); i++) {
					Element tag = doc.createElement(record.getItem(i).getColName());
					Text value = doc.createTextNode(record.getItem(i).getValue());
					tag.appendChild(value);
					xmlRecord.appendChild(tag);
				}
				root.appendChild(xmlRecord);
			}
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

	private static String getDTDPath() {
		String ans = tableFullPathXML.substring(0, tableFullPathXML.length() - 3) + "dtd";
		return ans;
	}
}
