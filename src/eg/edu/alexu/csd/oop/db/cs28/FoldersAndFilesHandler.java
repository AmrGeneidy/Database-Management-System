package eg.edu.alexu.csd.oop.db.cs28;

import java.io.File;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eg.edu.alexu.csd.oop.db.cs28.Parser.returnType;

public class FoldersAndFilesHandler {
	public void createDatabase(String path) {
		File dir = new File(path);
		dir.mkdirs();
	}

	public void deleteDatabase(String path) {
		File dir = new File(path);
		File[] listFiles = dir.listFiles();
		for (File file : listFiles) {
			file.delete();
		}
		dir.delete();
	}

	public void deleteTable(String path) {
		new File(path + ".xml").delete();
		new File(path + ".dtd").delete();
	}

	public boolean createTable(String path,String name, String[] colName, String[] colType) throws Exception {
		File x = new File(path + ".xml");
		if (x.exists()) {
			return false;
		}
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.newDocument();
		Element rootElement = doc.createElement(name.toLowerCase());
		doc.appendChild(rootElement);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(x);
		transformer.transform(source, result);
		@SuppressWarnings("resource")
		PrintWriter writer = new PrintWriter(path + ".dtd");
		writer.print("<!ELEMENT row (");
		for (int i = 0; i < colName.length; i++) {
			if (i < colName.length - 1) {
				writer.print(colName[i] + ",");
			} else {
				writer.println(colName[i] + ")>");
			}
		}
		for (int i = 0; i < colName.length; i++) {
			writer.println("<!ELEMENT " + colName[i] + " " + colType[i] + ">");
		}
		writer.close();
		return true;
	}
}
