package eg.edu.alexu.csd.oop.db.cs28;

import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.cs28.Parser.returnType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Arrays;
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
					executeStructureQuery("CREATE DATABASE " + databaseName);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else
				currentDatabase = databaseName;
		} else {
			try {
				executeStructureQuery("CREATE DATABASE " + databaseName);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return db.getAbsolutePath();
	}

	@Override
	public boolean executeStructureQuery(String query) throws SQLException {
		Parser parser = new Parser();
		if (!parser.executeStructureQuery(query)) {
			throw new SQLException("Invalid Query!!");
		}
		HashMap<returnType, Object> map = parser.map;
		FoldersAndFilesHandler handler = new FoldersAndFilesHandler();
		if ((boolean) map.get(returnType.ISDATABASE) && (boolean) map.get(returnType.ISCREATE)) {
			String path = ((String) map.get(returnType.NAME)).toLowerCase();
			handler.createDatabase(path);
			currentDatabase = path;
			return true;
		} else if ((boolean) map.get(returnType.ISDATABASE) && !(boolean) map.get(returnType.ISCREATE)) {
			String path = ((String) map.get(returnType.NAME)).toLowerCase();
			handler.deleteDatabase(path);
			return true;
		} else if (currentDatabase == null) {
			throw new SQLException("No Database is selected !");
		} else if (!(boolean) map.get(returnType.ISDATABASE) && !(boolean) map.get(returnType.ISCREATE)) {
			String path = currentDatabase + System.getProperty("file.separator")
					+ ((String) map.get(returnType.NAME)).toLowerCase();
			handler.deleteTable(path);
			return true;
		} else {
			String path = currentDatabase + System.getProperty("file.separator")
					+ ((String) map.get(returnType.NAME)).toLowerCase();

			try {
				return handler.createTable(path, (String) map.get(returnType.NAME),
						(String[]) map.get(returnType.COLNAME), (String[]) map.get(returnType.COLTYPE));
			} catch (Exception e) {
				return false;
			}
		}
	}

	@Override
	public Object[][] executeQuery(String query) throws SQLException {

		Parser parser = new Parser();
		parser.executeQuery(query);
		HashMap<returnType, Object> data = parser.map;

		String[] columnsArray = (String[]) data.get(returnType.COLNAME);
		String[] conditionOperands = (String[]) data.get(returnType.CONDITIONOPERANDS);
		String conditionOprtator = (String) data.get(returnType.CONDITIONOPERATOR);
		int value = 0;
		int actualRows = 0;
		int actualcolumns = 0;
		byte[] checkRow;

		Object[][] selected = null;
		try {
			File x = new File(currentDatabase + System.getProperty("file.separator")
					+ ((String) data.get(returnType.NAME)).toLowerCase() + ".xml");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(x);
			doc.getDocumentElement().normalize();
			// rows
			NodeList rows = doc.getElementsByTagName("record");

			Item[] col = ModifyTable.readDTD(currentDatabase + System.getProperty("file.separator")
					+ ((String) data.get(returnType.NAME)).toLowerCase() + ".dtd");

			checkRow = new byte[rows.getLength()];
			int numOfColumns = col.length;
			// database name
			System.out.println("Database Name: " + doc.getDocumentElement().getNodeName());

			byte[] checkColumn = new byte[numOfColumns];

			if (columnsArray == null && conditionOperands == null) {
				// no condition
				for (int i = 0; i < checkRow.length; i++) {
					checkRow[i] = 1;
					actualRows++;
				}
				for (int i = 0; i < checkColumn.length; i++) {
					checkColumn[i] = 1;
					actualcolumns++;
				}

			} else if (conditionOperands != null) {
				if (columnsArray == null) {
					for (int i = 0; i < checkColumn.length; i++) {
						checkColumn[i] = 1;
						actualcolumns++;
					}
				}

				String firstComparator = conditionOperands[0];
				String secondComparator = conditionOperands[1];

				boolean isNum = true;
				for (char c : secondComparator.toCharArray()) {
					if (!Character.isDigit(c)) {
						isNum = false;
					}
				}
				if (isNum) {
					value = Integer.parseInt(secondComparator);
				}

				for (int i = 0; i < checkRow.length; i++) {
					Node p = rows.item(i);
					if (p.getNodeType() == Node.ELEMENT_NODE) {
						Element e = (Element) p;
						NodeList columns = e.getChildNodes();
						for (int j = 0, k = 0; j < columns.getLength(); j++, k++) {
							Node content = columns.item(j);
							if (content.getNodeType() == Node.ELEMENT_NODE) {
								Element n = (Element) content;
								if (!(columnsArray == null)) {
									for (int v = 0; v < columnsArray.length; v++) {
										String s = n.getTagName();
										if (s.equalsIgnoreCase(columnsArray[v])) {
											if (checkColumn[k] == 0) {
												actualcolumns++;
											}
											checkColumn[k] = 1;
											break;
										}
									}
								}

								if (n.getTagName().equalsIgnoreCase(firstComparator)) {
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
										if (sample > value) {
											checkRow[i] = 1;
											actualRows++;
										}
										break;
									case "<":
										sample = Integer.parseInt(n.getTextContent());
										if (sample < value) {
											checkRow[i] = 1;
											actualRows++;
										}
										break;
									case "<>":
										sample = Integer.parseInt(n.getTextContent());
										if (sample != value) {
											checkRow[i] = 1;
											actualRows++;
										}
										break;
									case ">=":
										sample = Integer.parseInt(n.getTextContent());
										if (sample >= value) {
											checkRow[i] = 1;
											actualRows++;
										}
										break;
									case "<=":
										sample = Integer.parseInt(n.getTextContent());
										if (sample <= value) {
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
			} else if (columnsArray != null && conditionOperands == null) {
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

								for (int v = 0; v < columnsArray.length; v++) {
									String s = n.getTagName();
									if (s.equalsIgnoreCase(columnsArray[v])) {
										if (checkColumn[k] == 0) {
											actualcolumns++;
										}
										checkColumn[k] = 1;
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
					for (int i = 0, k = 0, g = 0; i < columns.getLength(); i++, k++) {
						// cell as a node
						Node content = columns.item(i);
						if (content.getNodeType() == Node.ELEMENT_NODE) {
							// n is a cell in the row
							Element n = (Element) content;
							if (checkRow[j] == 1 && checkColumn[k] == 1) {
								if (!n.getTextContent().equals("")) {
									boolean isNum = true;
									for (char c1 : n.getTextContent().toCharArray()) {
										if (!Character.isDigit(c1)) {
											isNum = false;
										}
									}
									if (isNum) {
										value = Integer.parseInt(n.getTextContent());
										selected[c][g] = value;
									} else {
										selected[c][g] = n.getTextContent();
									}

									g++;
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
		String xmlPath = currentDatabase + System.getProperty("file.separator")
				+ ((String) map.get(returnType.NAME)).toLowerCase() + ".xml";
		Table table = Table.loadNewTable(xmlPath);
		if ((boolean) map.get(returnType.ISINSERT)) {
			rowsCount = table.insert(map);

		} else if ((boolean) map.get(returnType.ISUPDATE)) {
			rowsCount = table.update(map);

		} else if ((boolean) map.get(returnType.ISDELETE)) {
			rowsCount = table.delete(map);
		}

		return rowsCount;
	}
}
