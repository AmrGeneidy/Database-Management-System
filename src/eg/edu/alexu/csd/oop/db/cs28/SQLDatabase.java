package eg.edu.alexu.csd.oop.db.cs28;

import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.cs28.Parser.returnType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SQLDatabase implements Database {
	private String currentDatabase;
    @Override
    public String createDatabase(String databaseName, boolean dropIfExists) {
    	databaseName = databaseName.toLowerCase(); 
    	File db = new File(databaseName);
    	if (db.exists()) {
			if (dropIfExists) {
				try {
					executeStructureQuery("DROP DATABASE "+databaseName);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			else 
				currentDatabase = databaseName;
		}
    	else {
    		currentDatabase = databaseName;
    		try {
				executeStructureQuery("CREATE DATABASE "+databaseName);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        return db.getAbsolutePath();
    }

    @Override
    public boolean executeStructureQuery(String query) throws SQLException {
    	Parser parser = new Parser();
    	if (!parser.executeStructureQuery(query)) {
			throw new SQLException();
		}
    	HashMap<returnType, Object> map = parser.map;
    	if ((boolean)map.get(returnType.ISDATABASE)&&(boolean)map.get(returnType.ISCREATE)) {
			File dbDir = new File(((String) map.get(returnType.NAME)).toLowerCase());
			dbDir.mkdirs();
			currentDatabase = ((String) map.get(returnType.NAME)).toLowerCase();
			return true;
		}else if ((boolean)map.get(returnType.ISDATABASE)&&!(boolean)map.get(returnType.ISCREATE)) {
			File dbDir = new File(((String) map.get(returnType.NAME)).toLowerCase());
			File[] listFiles = dbDir.listFiles();
			for(File file : listFiles){
				file.delete();
			}
			return true;
		}else if (currentDatabase==null) {
			throw new SQLException();
		}else if(!(boolean)map.get(returnType.ISDATABASE)&&!(boolean)map.get(returnType.ISCREATE)) {
			new File(currentDatabase+ System.getProperty("file.separator")+((String) map.get(returnType.NAME)).toLowerCase()+".xml").delete();
			new File(currentDatabase+ System.getProperty("file.separator")+((String) map.get(returnType.NAME)).toLowerCase()+".dtd").delete();
		}else {
			try {
				File x = new File(currentDatabase+ System.getProperty("file.separator")+((String) map.get(returnType.NAME)).toLowerCase()+".xml");
				if (x.exists()) {
					return false;
				}
				x.createNewFile();
			} catch (IOException e) {
				return false;
			}
			try {
				new File(currentDatabase+ System.getProperty("file.separator")+((String) map.get(returnType.NAME)).toLowerCase()+".dtd").createNewFile();
				@SuppressWarnings("resource")
				PrintWriter writer= new PrintWriter(currentDatabase+ System.getProperty("file.separator")+((String) map.get(returnType.NAME)).toLowerCase()+".dtd");
				writer.print("<!ELEMENT row (");
				String [] colName = (String[]) map.get(returnType.COLNAME);
				for (int i = 0; i < colName.length; i++) {
					if (i < colName.length - 1) {
						writer.print(colName[i]+",");
					} else {
						writer.println(colName[i]+")>");
					}
				}
				String [] coltype = (String[]) map.get(returnType.COLTYPE);
				for (int i = 0; i < colName.length; i++) {
					writer.println("<!ELEMENT "+colName[i]+" "+coltype[i]+">");
				}
				writer.close();
			} catch (FileNotFoundException e) {
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return true;
		}
        return false;
    }

    @Override
    public Object[][] executeQuery(String query) throws SQLException {
    	try {

			File input = new File("demo.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(input);
			doc.getDocumentElement().normalize();
			// database name
			// rows
			NodeList rows = doc.getElementsByTagName("student");

			Scanner s = new Scanner(System.in);
			String in = s.nextLine();

			int numOfColumns = 4;

			byte[] check = new byte[rows.getLength()];

			// database name
			System.out.println("Database Name: " + doc.getDocumentElement().getNodeName());

			String comparator = "firstname";
			int actualRows = 0;

			if (in.equals("*")) {
				for (int i = 0; i < check.length; i++) {
					check[i] = 1;
					actualRows++;
				}
			} else {
				for (int i = 0; i < check.length; i++) {
					Node p = rows.item(i);
					if (p.getNodeType() == Node.ELEMENT_NODE) {
						Element e = (Element) p;
						NodeList columns = e.getChildNodes();
						for (int j = 1; j < columns.getLength(); j += 2) {
							Node content = columns.item(j);
							if (content.getNodeType() == Node.ELEMENT_NODE) {
								Element n = (Element) content;
								if (n.getTagName().equals(comparator)) {
									// conditions
									switch (in) {
									case "==":
										if (n.getTextContent().equals("dinkar")) {
											check[i] = 1;
											actualRows++;
										}
										break;
									case ">":
										break;
									case "<":
										break;
									case "<>":
										break;
									case "!=":
										break;
									case ">=":
										break;
									case "<=":
										break;
									case "=":
										break;

									}
								}
							}
						}
					}
				}
			}

			Object[][] selected = new Object[actualRows][numOfColumns];

			int c = 0;
			boolean flag = false;
			for (int j = 0; j < rows.getLength(); j++) {
				flag = false;
				// get the first row as a node
				Node p = rows.item(j);
				if (p.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) p;
					// id of the first node
					String id = e.getAttribute("id");
					// the first row cells
					NodeList columns = e.getChildNodes();
					for (int i = 1, k = 0; i < columns.getLength(); i += 2, k++) {
						// cell as a node
						Node content = columns.item(i);
						if (content.getNodeType() == Node.ELEMENT_NODE) {
							// n is a cell in the row
							Element n = (Element) content;
							if (check[j] == 1) {
								selected[c][k] = n.getTextContent();
								flag = true;
							}
						}
					}
					if (flag) {
						c++;
					}

				}
			}

			for (int i = 0; i < selected.length; i++) {
				for (int j = 0; j < numOfColumns; j++) {
					System.out.print(selected[i][j] + "	");
				}
				System.out.println();

			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return new Object[0][];
    }

    @Override
    public int executeUpdateQuery(String query) throws SQLException {
        return 0;
    }
}
