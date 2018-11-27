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
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import eg.edu.alexu.csd.oop.db.cs28.Parser.returnType;

public class ModifyTable {

	public static int insert(String workSpacePath, HashMap<returnType, Object> map) {
		//TODO method doesn't terminate when process fail 
		boolean processFailed = false;
		String tableName = ((String) map.get(returnType.NAME)).toLowerCase();
		Object[] colNames;
		Object[] colValues = (Object[]) map.get(returnType.COLVALUES);

		Item[] record = readDTD(
				 workSpacePath + System.getProperty("file.separator") + tableName + ".dtd");
		// couldn't read DTD file
		if (record.length == 0) {
			processFailed = true;
		}

		// All cols case
		if (((Object[])map.get(returnType.COLNAME)).length==0) {
			if (record.length != colValues.length) {
				processFailed = true;
			} else {
				for (int i = 0; i < record.length; i++) {
					// check if Value is Integer or not TODO test this line
					if (record[i].getDataType().equals("int") && !(Integer.valueOf(Integer.parseInt((String)(colValues[i]))) instanceof Integer)) {
						processFailed = true;
					} else {
						record[i].setValue((String) colValues[i]);
					}
				}
			}
			// Some cols case
		} else {
			colNames = (Object[]) map.get(returnType.COLNAME);
			if (colNames.length != colValues.length) {
				processFailed = true;
			} else {
				// TODO not efficient
				for (int colCount = 0; colCount < colNames.length; colCount++) {
					for (int i = 0; i < record.length; i++) {
						if (((String)colNames[colCount]).equals(record[i].getColName())) {
							// check if Value is Integer or not TODO test this line (copied from above)
							if (record[i].getDataType().equals("int") && !(Integer.valueOf(Integer.parseInt((String)colValues[colCount])) instanceof Integer)) {
								processFailed = true;
							} else {
								record[i].setValue((String) colValues[colCount]);
							}
						}
					}
				}
			}
		}
		//Finally Data now in Record
		
		
		//terminate before editing the table
		if (processFailed) {
			return 0;
		}
		
		// open table file.xml and edit without deleting it
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(workSpacePath + System.getProperty("file.separator") + tableName + ".xml"));
			Element root = doc.getDocumentElement();
			// Check if file empty or not
			if (root == null) {
				root = doc.createElement(tableName);
			}
			Element xmlRecord = doc.createElement("record");
			
			for (Item item : record) {
				Element tag = doc.createElement(item.getColName());
				Text value = doc.createTextNode(item.getValue());
				tag.appendChild(value);
				xmlRecord.appendChild(tag);
			}
			root.appendChild(xmlRecord);
			doc.appendChild(root);
			
	         TransformerFactory transformerFactory = TransformerFactory.newInstance();
	         Transformer transformer = transformerFactory.newTransformer();
	         DOMSource source = new DOMSource(doc);
	         StreamResult result = new StreamResult(new File(workSpacePath + System.getProperty("file.separator") + tableName + ".xml"));
	         transformer.transform(source, result);
	         
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException | IOException e) {
			// TODO table not found
			processFailed = true;
			e.printStackTrace();
		} catch (TransformerException e) {
			//TODO error while writing into file
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (processFailed) {
			return 0;
		}
		
		return 1;
	}

	public static int update(String workSpacePath, HashMap<returnType, Object> map) {

		return 0;
	}

	public static int delete(String workSpacePath, HashMap<returnType, Object> map) {

		return 0;
	}

	private static Item[] readDTD(String path) {
		BufferedReader reader = null;
		Pattern pattern = Pattern.compile("<!ELEMENT (\\S+) (\\S+)>");
		Matcher matcher = null;
		ArrayList<Item> column = new ArrayList<Item>();
		try {
			reader = new BufferedReader(new FileReader(path));
			reader.readLine();
			String thisLine = reader.readLine();
			while (thisLine != null) {
				matcher = pattern.matcher(thisLine);
				matcher.matches();
				column.add(new Item(matcher.group(1), matcher.group(2)));
				thisLine = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			// TODO DTD not found return null
			e.printStackTrace();
			return null;
		}

		return  column.toArray(new Item[column.size()]);
	}
}
