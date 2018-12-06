package eg.edu.alexu.csd.oop.db.cs28;

import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.cs28.Parser.returnType;
import eg.edu.alexu.csd.oop.db.cs28.Record;
import eg.edu.alexu.csd.oop.db.cs28.Table;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.management.RuntimeErrorException;


public class SQLDatabase implements Database {

	private String currentDatabase;
	
	public String getCurrentDataBase() {
		return currentDatabase;
	}

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
		
	Object[][] selected = null;
	
	Parser parser = new Parser();
	parser.executeQuery(query);
	HashMap<returnType, Object> data = parser.map;
	Table table = null;
	try {
		table = Table.loadNewTable(currentDatabase + System.getProperty("file.separator")
		+ ((String) data.get(returnType.NAME)).toLowerCase() + ".xml");
		
	}
	catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("There is no data recorded!");
		
	}
	
	
	String[] columnsArray = (String[]) data.get(returnType.COLNAME);
	String[] colNames = table.getColsNames();
	String[] colTypes = table.getColsDataTypes();
	String[] conditionOperands = (String[]) data.get(returnType.CONDITIONOPERANDS);
	String conditionOprtator = (String) data.get(returnType.CONDITIONOPERATOR);
	ArrayList<Record> contents = table.getTableData();
	
	int value = 0;
	int actualRows = 0;
	int actualcolumns = 0;
	int numOfColumns = colNames.length;
	
	byte[] checkRow = new byte[contents.size()];
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
			for (int j = 0, k = 0; j < contents.get(0).length(); j++, k++) {
				if (!(columnsArray == null)) {
					for (int v = 0; v < columnsArray.length; v++) {
						String s = colNames[k];
						if (s.equalsIgnoreCase(columnsArray[v])) {
							if (checkColumn[k] == 0) {
								actualcolumns++;
							}
							checkColumn[k] = 1;
							break;
						}
					}
				}

				if (colNames[j].equalsIgnoreCase(firstComparator)) {
					Record oneRow = contents.get(i);
					// conditions
					switch (conditionOprtator) {
					case "=":
						if (oneRow.getItem(k).equals(conditionOperands[1])) {
							checkRow[i] = 1;
							actualRows++;
						}
						break;
					case ">":
						int sample = Integer.parseInt(oneRow.getItem(k));
						if (sample > value) {
							checkRow[i] = 1;
							actualRows++;
						}
						break;
					case "<":
						sample = Integer.parseInt(oneRow.getItem(k));
						if (sample < value) {
							checkRow[i] = 1;
							actualRows++;
						}
						break;
					case "<>":
						sample = Integer.parseInt(oneRow.getItem(k));
						if (sample != value) {
							checkRow[i] = 1;
							actualRows++;
						}
						break;
					case ">=":
						sample = Integer.parseInt(oneRow.getItem(k));
						if (sample >= value) {
							checkRow[i] = 1;
							actualRows++;
						}
						break;
					case "<=":
						sample = Integer.parseInt(oneRow.getItem(k));
						if (sample <= value) {
							checkRow[i] = 1;
							actualRows++;
						}
						break;
					default:
						throw new RuntimeException("Invalid Query!");

					}
				}
			}
		}
	} else if (columnsArray != null && conditionOperands == null) {
		for (int i = 0; i < checkRow.length; i++) {
			checkRow[i] = 1;
			actualRows++;
		}
		for (int j = 0; j < contents.size(); j++) {

			for (int i = 1, k = 0; i < contents.get(0).length(); i += 2, k++) {
				for (int v = 0; v < columnsArray.length; v++) {
					String s = colNames[k];
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

	selected = new Object[actualRows][actualcolumns];
	int c = 0;
	boolean flag = false;
	for (int j = 0; j < contents.size(); j++) {
		flag = false;
		for (int i = 0, k = 0, g = 0; i < contents.get(0).length(); i++, k++) {
			Record oneRow = contents.get(j);
			if (checkRow[j] == 1 && checkColumn[k] == 1) {
				if (!oneRow.getItem(k).equals("")) {
					boolean isNum = true;
					for (char c1 : oneRow.getItem(k).toCharArray()) {
						if (!Character.isDigit(c1)) {
							isNum = false;
						}
					}
					if (isNum) {
						value = Integer.parseInt(oneRow.getItem(k));
						selected[c][g] = value;
					} else {
						selected[c][g] = oneRow.getItem(k);
					}

					g++;
				}
				flag = true;
			}
		}
		if (flag) {
			c++;
		}
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
