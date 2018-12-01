package eg.edu.alexu.csd.oop.db.cs28;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

	public static int insert(Table table, String[] colNames, String[] values) {
		// TODO method doesn't terminate when process fail
		boolean processFailed = false;
		boolean allColsCase = false;
		if (colNames.length == 0) {
			colNames = table.getColsNames();
			allColsCase = true;
		}
		if (colNames.length != values.length) {
			processFailed = true;
		}

		for (int i = 0; i < values.length; i++) {
			if (values[i].contains("'")) {
				values[i] = values[i].replaceAll("'", "");
			}
		}
		
		
		String[] itemsRow = new String[colNames.length];
		String[] dataTypes = table.getColsDataTypes();
		if (allColsCase) {
			for (int i = 0; i < itemsRow.length; i++) {
				// check if Value is Integer or not TODO test this line
				if (dataTypes[i].equals("int") && !(Integer.valueOf(Integer.parseInt(values[i])) instanceof Integer)) {
					processFailed = true;
				} else {
					itemsRow[i] = values[i];
				}
			}
		} else {
			// TODO not efficient
			for (int i = 0; i < table.getColsNames().length; i++) {
				for (int inputCount = 0; inputCount < colNames.length; inputCount++) {
					if (table.getColsNames()[i].equalsIgnoreCase(colNames[inputCount])) {
						if (dataTypes[i].equals("int")
								&& !(Integer.valueOf(Integer.parseInt(values[inputCount])) instanceof Integer)) {
							processFailed = true;
						} else {
							itemsRow[i] = values[inputCount];
							break;
						}
					} else {
						// TODO empty or null ?!
						itemsRow[i] = "";

					}
				}
			}
		}

		if (processFailed) {
			return 0;
		}
		ArrayList<Record> newData = table.getTableData();
		newData.add(new Record(itemsRow));
		table.setTableData(newData);
		return 1;
	}

	public static int update(Table table, HashMap<returnType, Object> map) {
		// TODO check if user condition & values matches table's dataTypes or not
		int ans = 0;
		String operator = getCondOperator(map);
		String[] operands = getCondOperands(map);
		boolean noConditionCase = false;
		int indexOfCond = -1;
		String[] userColsNames = getUserColsNames(map);
		String[] userValues = getUserValues(map);

		if (operator == null || operands == null) {
			noConditionCase = true;
		} else {
			for (int i = 0; i < table.getColsNames().length; i++) {
				if (operands[0].equalsIgnoreCase(table.getColsNames()[i])) {
					// mismatch in dataTypes
					if (operands[1].contains("'") && table.getColsDataTypes()[i].equals("int")) {
						return 0;
					}

					indexOfCond = i;
					break;
				}
			}
		}

		// can't find the col in the condition
		if (!noConditionCase && indexOfCond == -1) {
			return 0;
		}

		// get index of cols we want to updates
		ArrayList<Integer> colsIndices = new ArrayList<>();
		// i index of table cols
		for (int i = 0; i < table.getColsNames().length; i++) {
			// j index of user cols
			for (int j = 0; j < userColsNames.length; j++) { 
				if (table.getColsNames()[i].equalsIgnoreCase(userColsNames[j])) {
					// dataType mismatch
					if (table.getColsDataTypes()[i].equals("int") && userValues[j].contains("'")) {
						return 0;
					}
					colsIndices.add(i);
				}

			}
		}

		// delete '
		if (!noConditionCase && operands[1].contains("'")) {
			operands[1] = operands[1].replaceAll("'", "");
		}
		for (int i = 0; i < userValues.length; i++) {
			if (userValues[i].contains("'")) {
				userValues[i] = userValues[i].replaceAll("'", "");
			}
		}
		
		ArrayList<Record> newData = table.getTableData();

		// update all table data
		if (noConditionCase) {
			ans = table.getTableData().size();
			// loop all records
			for (int i = 0; i < newData.size(); i++) {
				for (int j = 0; j < colsIndices.size(); j++) {
					int indexOfCol = colsIndices.get(j);
					newData.get(i).setItem(indexOfCol, userValues[j]);
				}
			}
		} else {
			switch (operator) {
			case "=":
				for (int i = 0; i < newData.size(); i++) {
					String x = newData.get(i).getItem(indexOfCond);
					// TODO case insensitive ?!
					if (x.equalsIgnoreCase(operands[1])) {
						for (int j = 0; j < colsIndices.size(); j++) {
							int indexOfCol = colsIndices.get(j);
							newData.get(i).setItem(indexOfCol, userValues[j]);
						}
						i--;
						ans++;
					}
				}
				break;
			case ">":
				for (int i = 0; i < newData.size(); i++) {
					String x = newData.get(i).getItem(indexOfCond);
					// TODO case insensitive ?!
					if (Integer.parseInt(x) > Integer.parseInt(operands[1])) {
						for (int j = 0; j < colsIndices.size(); j++) {
							int indexOfCol = colsIndices.get(j);
							newData.get(i).setItem(indexOfCol, userValues[j]);
						}
						i--;
						ans++;
					}
				}
				break;
			case "<":
				for (int i = 0; i < newData.size(); i++) {
					String x = newData.get(i).getItem(indexOfCond);
					// TODO case insensitive ?!
					if (Integer.parseInt(x) < Integer.parseInt(operands[1])) {
						for (int j = 0; j < colsIndices.size(); j++) {
							int indexOfCol = colsIndices.get(j);
							newData.get(i).setItem(indexOfCol, userValues[j]);
						}
						i--;
						ans++;
					}
				}
				break;
			case "<>":
				for (int i = 0; i < newData.size(); i++) {
					String x = newData.get(i).getItem(indexOfCond);
					// TODO case insensitive ?!
					if (!x.equalsIgnoreCase(operands[1])) {
						for (int j = 0; j < colsIndices.size(); j++) {
							int indexOfCol = colsIndices.get(j);
							newData.get(i).setItem(indexOfCol, userValues[j]);
						}
						i--;
						ans++;
					}
				}
				break;
			case ">=":
				for (int i = 0; i < newData.size(); i++) {
					String x = newData.get(i).getItem(indexOfCond);
					// TODO case insensitive ?!
					if (Integer.parseInt(x) >= Integer.parseInt(operands[1])) {
						for (int j = 0; j < colsIndices.size(); j++) {
							int indexOfCol = colsIndices.get(j);
							newData.get(i).setItem(indexOfCol, userValues[j]);
						}
						i--;
						ans++;
					}
				}
				break;
			case "<=":
				for (int i = 0; i < newData.size(); i++) {
					String x = newData.get(i).getItem(indexOfCond);
					// TODO case insensitive ?!
					if (Integer.parseInt(x) <= Integer.parseInt(operands[1])) {
						for (int j = 0; j < colsIndices.size(); j++) {
							int indexOfCol = colsIndices.get(j);
							newData.get(i).setItem(indexOfCol, userValues[j]);
						}
						i--;
						ans++;
					}
				}
				break;

			}

		}
		table.setTableData(newData);
		return ans;
	}

	private static String[] getUserValues(HashMap<returnType, Object> map) {
		String[] values = null;
		if (map.containsKey(returnType.COLVALUES)) {
			values = Arrays.copyOf((Object[])map.get(returnType.COLVALUES), ((Object[])map.get(returnType.COLVALUES)).length, String[].class);
		}
		return values;
	}

	private static String[] getUserColsNames(HashMap<returnType, Object> map) {
		String[] colsNames = null;
		if (map.containsKey(returnType.COLNAME)) {
			colsNames = Arrays.copyOf((Object[])map.get(returnType.COLNAME), ((Object[])map.get(returnType.COLNAME)).length, String[].class);
		}
		return colsNames;
	}

	public static int delete(Table table, HashMap<returnType, Object> map) {
		// TODO check if user condition matches table's dataTypes or not
		int ans = 0;
		String operator = getCondOperator(map);
		String[] operands = getCondOperands(map);
		boolean noConditionCase = false;
		int indexOfCond = -1;

		if (operator == null || operands == null) {
			noConditionCase = true;
		} else {
			for (int i = 0; i < table.getColsNames().length; i++) {
				if (operands[0].equalsIgnoreCase(table.getColsNames()[i])) {
					// mismatch in dataTypes
					if (operands[1].contains("'") && table.getColsDataTypes()[i].equals("int")) {
						return 0;
					}

					indexOfCond = i;
					break;
				}
			}
		}

		// can't find the col in the condition
		if (!noConditionCase && indexOfCond == -1) {
			return 0;
		}

		// delete '
		if (!noConditionCase && operands[1].contains("'")) {
			operands[1] = operands[1].replaceAll("'", "");
		}

		ArrayList<Record> newData = table.getTableData();

		// delete all table data
		if (noConditionCase) {
			ans = table.getTableData().size();
			newData.clear();
		} else {
			switch (operator) {
			case "=":
				for (int i = 0; i < newData.size(); i++) {
					String x = newData.get(i).getItem(indexOfCond);
					// TODO case insensitive ?!
					if (x.equalsIgnoreCase(operands[1])) {
						newData.remove(i);
						i--;
						ans++;
					}
				}
				break;
			case ">":
				for (int i = 0; i < newData.size(); i++) {
					String x = newData.get(i).getItem(indexOfCond);
					// TODO case insensitive ?!
					if (Integer.parseInt(x) > Integer.parseInt(operands[1])) {
						newData.remove(i);
						i--;
						ans++;
					}
				}
				break;
			case "<":
				for (int i = 0; i < newData.size(); i++) {
					String x = newData.get(i).getItem(indexOfCond);
					// TODO case insensitive ?!
					if (Integer.parseInt(x) < Integer.parseInt(operands[1])) {
						newData.remove(i);
						i--;
						ans++;
					}
				}
				break;
			case "<>":
				for (int i = 0; i < newData.size(); i++) {
					String x = newData.get(i).getItem(indexOfCond);
					// TODO case insensitive ?!
					if (!x.equalsIgnoreCase(operands[1])) {
						newData.remove(i);
						i--;
						ans++;
					}
				}
				break;
			case ">=":
				for (int i = 0; i < newData.size(); i++) {
					String x = newData.get(i).getItem(indexOfCond);
					// TODO case insensitive ?!
					if (Integer.parseInt(x) >= Integer.parseInt(operands[1])) {
						newData.remove(i);
						i--;
						ans++;
					}
				}
				break;
			case "<=":
				for (int i = 0; i < newData.size(); i++) {
					String x = newData.get(i).getItem(indexOfCond);
					// TODO case insensitive ?!
					if (Integer.parseInt(x) <= Integer.parseInt(operands[1])) {
						newData.remove(i);
						i--;
						ans++;
					}
				}
				break;

			}
		}
		table.setTableData(newData);
		return ans;
	}

	// Duplicated Delete it
	private static int oldInsert(String workSpacePath, HashMap<returnType, Object> map) {
		// TODO method doesn't terminate when process fail
		boolean processFailed = false;
		String tableName = ((String) map.get(returnType.NAME)).toLowerCase();
		Object[] colNames;
		Object[] colValues = (Object[]) map.get(returnType.COLVALUES);

		Item[] record = readDTD(workSpacePath + System.getProperty("file.separator") + tableName + ".dtd");
		// couldn't read DTD file
		if (record.length == 0) {
			processFailed = true;
		}

		// All cols case
		if (((Object[]) map.get(returnType.COLNAME)).length == 0) {
			if (record.length != colValues.length) {
				processFailed = true;
			} else {
				for (int i = 0; i < record.length; i++) {
					// check if Value is Integer or not TODO test this line
					if (record[i].getDataType().equals("int")
							&& !(Integer.valueOf(Integer.parseInt((String) (colValues[i]))) instanceof Integer)) {
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
						if (((String) colNames[colCount]).equalsIgnoreCase(record[i].getColName())) {
							// check if Value is Integer or not TODO test this line (copied from above)
							if (record[i].getDataType().equals("int") && !(Integer
									.valueOf(Integer.parseInt((String) colValues[colCount])) instanceof Integer)) {
								processFailed = true;
							} else {
								record[i].setValue((String) colValues[colCount]);
							}
						}
					}
				}
			}
		}
		// Finally Data now in Record

		// terminate before editing the table
		if (processFailed) {
			return 0;
		}

		// open table file.xml and edit without deleting it
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder
					.parse(new File(workSpacePath + System.getProperty("file.separator") + tableName + ".xml"));
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

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(
					new File(workSpacePath + System.getProperty("file.separator") + tableName + ".xml"));
			transformer.transform(source, result);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException | IOException e) {
			// TODO table not found
			processFailed = true;
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO error while writing into file
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (processFailed) {
			return 0;
		}

		return 1;
	}

	private static String getCondOperator(HashMap<returnType, Object> map) {
		String operator = null;
		if (map.containsKey(returnType.CONDITIONOPERATOR)) {
			operator = (String) map.get(returnType.CONDITIONOPERATOR);
		}
		return operator;

	}
	
	/* TODO delete this
	private static String[] getCondOperands(HashMap<returnType, Object> map) {
		String[] operands = null;
		if (map.containsKey(returnType.CONDITIONOPERANDS)) {
			operands = Arrays.copyOf((Object[])map.get(returnType.CONDITIONOPERANDS), ((Object[])map.get(returnType.CONDITIONOPERANDS)).length, String[].class);
		}
		return operands;
	}
	*/
	private static String[] getCondOperands(HashMap<returnType, Object> map) {
		String[] operands = null;
		if (map.containsKey(returnType.CONDITIONOPERANDS)) {
			operands = (String[]) map.get(returnType.CONDITIONOPERANDS);
		}
		return operands;
	}

	public static Item[] readDTD(String path) {
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

		return column.toArray(new Item[column.size()]);
	}
}
