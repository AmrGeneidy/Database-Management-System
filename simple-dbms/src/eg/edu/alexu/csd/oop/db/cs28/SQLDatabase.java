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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class SQLDatabase implements Database {

	private String currentDatabase;

	@Override
	public String createDatabase(String databaseName, boolean dropIfExists) {
		databaseName = databaseName.toLowerCase();
		File db = new File(databaseName);
		if (db.exists()) {
			if (dropIfExists) {
				try {
					executeStructureQuery("DROP DATABASE " + databaseName);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else
				currentDatabase = databaseName;
		} else {
			currentDatabase = databaseName;
			try {
				executeStructureQuery("CREATE DATABASE " + databaseName);
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
		if ((boolean) map.get(returnType.ISDATABASE) && (boolean) map.get(returnType.ISCREATE)) {
			File dbDir = new File(((String) map.get(returnType.NAME)).toLowerCase());
			dbDir.mkdirs();
			currentDatabase = ((String) map.get(returnType.NAME)).toLowerCase();
			return true;
		} else if ((boolean) map.get(returnType.ISDATABASE) && !(boolean) map.get(returnType.ISCREATE)) {
			File dbDir = new File(((String) map.get(returnType.NAME)).toLowerCase());
			File[] listFiles = dbDir.listFiles();
			for (File file : listFiles) {
				file.delete();
			}
			return true;
		} else if (currentDatabase == null) {
			throw new SQLException();
		} else if (!(boolean) map.get(returnType.ISDATABASE) && !(boolean) map.get(returnType.ISCREATE)) {
			new File(currentDatabase + System.getProperty("file.separator")
					+ ((String) map.get(returnType.NAME)).toLowerCase() + ".xml").delete();
			new File(currentDatabase + System.getProperty("file.separator")
					+ ((String) map.get(returnType.NAME)).toLowerCase() + ".dtd").delete();
		} else {
			try {
				File x = new File(currentDatabase + System.getProperty("file.separator")
						+ ((String) map.get(returnType.NAME)).toLowerCase() + ".xml");
				if (x.exists()) {
					return false;
				}
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.newDocument();
				 Element rootElement = doc.createElement(((String) map.get(returnType.NAME)).toLowerCase());
		         doc.appendChild(rootElement);
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(x);
				transformer.transform(source, result);
				@SuppressWarnings("resource")
				PrintWriter writer = new PrintWriter(currentDatabase + System.getProperty("file.separator")
						+ ((String) map.get(returnType.NAME)).toLowerCase() + ".dtd");
				writer.print("<!ELEMENT row (");
				String[] colName = (String[]) map.get(returnType.COLNAME);
				for (int i = 0; i < colName.length; i++) {
					if (i < colName.length - 1) {
						writer.print(colName[i] + ",");
					} else {
						writer.println(colName[i] + ")>");
					}
				}
				String[] coltype = (String[]) map.get(returnType.COLTYPE);
				for (int i = 0; i < colName.length; i++) {
					writer.println("<!ELEMENT " + colName[i] + " " + coltype[i] + ">");
				}
				writer.close();
			} catch (FileNotFoundException e) {
				return false;
			} catch (IOException e) {
				return false;
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}
		return false;
	}


    @Override
 public Object[][] executeQuery(String query) throws SQLException {
    	
    	Parser parser = new Parser();
    	parser.executeQuery(query);
    	HashMap<returnType, Object> data = parser.map;
    	int numOfColumns = 4;
    	String [] columnsArray = (String[]) data.get(returnType.COLNAME);
    	String[] conditionOperands = (String[]) data.get(returnType.CONDITIONOPERANDS);
    	String conditionOprtator = (String) data.get(returnType.CONDITIONOPERATOR);
    	int value = 0;
    	int actualRows = 0;
		int actualcolumns = 0;
    	byte[] checkColumn = new byte[numOfColumns];
    	
    	Object[][] selected = null;
    	try {
			File input = new File("demo.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(input);
			doc.getDocumentElement().normalize();
			// rows
			NodeList rows = doc.getElementsByTagName("student");

			byte[] checkRow = new byte[rows.getLength()];

			// database name
			System.out.println("Database Name: " + doc.getDocumentElement().getNodeName());

			
			

			if (columnsArray == null && conditionOperands == null) {
				//no condition
					for (int i = 0; i < checkRow.length; i++) {
						checkRow[i] = 1;
						actualRows++;
				}
					for (int i = 0; i < checkColumn.length; i++) {
						checkColumn[i] = 1;
						actualcolumns++;
				}
					
			}
			else if(columnsArray == null && conditionOperands != null) {
				String firstComparator = conditionOperands[0];
				String secondComparator = conditionOperands[1];
				
				boolean isNum = true;
				 for (char c : secondComparator.toCharArray())
				    {
				        if (!Character.isDigit(c)) {
				        	isNum = false;
				        }
				    }
				 if(isNum) {
					 value = Integer.parseInt(secondComparator);
				 }
				
				for (int i = 0; i < checkRow.length; i++) {
					Node p = rows.item(i);
					if (p.getNodeType() == Node.ELEMENT_NODE) {
						Element e = (Element) p;
						NodeList columns = e.getChildNodes();
						for (int j = 1; j < columns.getLength(); j += 2) {
							Node content = columns.item(j);
							if (content.getNodeType() == Node.ELEMENT_NODE) {
								Element n = (Element) content;
								
								if (n.getTagName().equals(firstComparator)) {
									// conditions
									switch (conditionOprtator) {
									case "=":
										if (n.getTextContent().equals(conditionOperands[1])) {
											checkRow[i] = 1;
											actualRows++;
										}
										break;
									case ">":
										int sample = Integer.parseInt(n.getTextContent());
										if(sample > value) {
											checkRow[i] = 1;
											actualRows++;
										}
										break;
									case "<":
										sample = Integer.parseInt(n.getTextContent());
										if(sample < value) {
											checkRow[i] = 1;
											actualRows++;
										}
										break;
									case "<>":
										sample = Integer.parseInt(n.getTextContent());
										if(sample != value) {
											checkRow[i] = 1;
											actualRows++;
										}
										break;
									case ">=":
										sample = Integer.parseInt(n.getTextContent());
										if(sample >= value) {
											checkRow[i] = 1;
											actualRows++;
										}
										break;
									case "<=":
										sample = Integer.parseInt(n.getTextContent());
										if(sample <= value) {
											checkRow[i] = 1;
											actualRows++;
										}
										break;
									default:
										System.out.println("Not Found");
										
									}
								}
							}
						}
					}
				}
			}
			else if(columnsArray != null && conditionOperands == null) {
				for (int i = 0; i < checkRow.length; i++) {
					checkRow[i] = 1;
					actualRows++;
			}
				for (int j = 0; j < rows.getLength(); j++) {
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
								
								for(int v = 0; v < columnsArray.length; v++) {
									String s = n.getTagName();
									if(s.equals(columnsArray[v])) {
										if(checkColumn[v] == 0) {
											actualcolumns++;
										}
										checkColumn[v] = 1;
										break;
									}
								}
							}
						}
					}
				}
			}
			
			selected = new Object[actualRows][actualcolumns];
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
							if (checkRow[j] == 1 && checkColumn[k] == 1) {
								if(!n.getTextContent().equals("")) {
									selected[c][k] = n.getTextContent();
								}
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
				for (int j = 0; j < actualcolumns; j++) {
					System.out.print(selected[i][j] + "	");
				}

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
        return selected;
    }

	@Override
	public int executeUpdateQuery(String query) throws SQLException {
		int rowsCount = 0;
		Parser parser = new Parser();
		if (!parser.executeUpdateQuery(query)) {
			throw new SQLException();
		}
		HashMap<returnType, Object> map = parser.map;
		if ((boolean)map.get(returnType.ISINSERT)) {
			rowsCount = ModifyTable.insert(currentDatabase,map);
		}else if ((boolean)map.get(returnType.ISUPDATE)) {
			rowsCount = ModifyTable.update(currentDatabase,map);
		}else if ((boolean)map.get(returnType.ISDELETE)) {
			rowsCount = ModifyTable.delete(currentDatabase,map);
		}
	
		return rowsCount;
	}
}
