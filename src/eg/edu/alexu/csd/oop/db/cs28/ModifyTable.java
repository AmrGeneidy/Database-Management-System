package eg.edu.alexu.csd.oop.db.cs28;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eg.edu.alexu.csd.oop.db.cs28.Parser.returnType;

public class ModifyTable {

	public int insert(Table table, HashMap<returnType, Object> map) throws SQLException {
		Object[] ob1 = (Object[]) map.get(returnType.COLNAME);
		Object[] ob2 = (Object[]) map.get(returnType.COLVALUES);
		String[] colNames = Arrays.copyOf(ob1, ob1.length, String[].class);
		String[] values = Arrays.copyOf(ob2, ob2.length, String[].class);
		boolean allColsCase = false;
		if (colNames.length == 0) {
			colNames = table.getColsNames();
			allColsCase = true;
		}
		if (colNames.length != values.length) {
			throw new SQLException("Did't add Values for all columns!!");
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
				if (dataTypes[i].equals("int") && !(Integer.valueOf(Integer.parseInt(values[i])) instanceof Integer)) {
					throw new SQLException("Mismatching in data types!!");
				} else {
					itemsRow[i] = values[i];
				}
			}
		} else {
			for (int i = 0; i < table.getColsNames().length; i++) {
				for (int inputCount = 0; inputCount < colNames.length; inputCount++) {
					if (table.getColsNames()[i].equalsIgnoreCase(colNames[inputCount])) {
						if (dataTypes[i].equals("int")
								&& !(Integer.valueOf(Integer.parseInt(values[inputCount])) instanceof Integer)) {
							throw new SQLException("Mismatching in data types!!");
						} else {
							itemsRow[i] = values[inputCount];
							break;
						}
					} else {
						itemsRow[i] = "";
					}
				}
			}
		}

		ArrayList<Record> newData = table.getTableData();
		newData.add(new Record(itemsRow));
		table.setTableData(newData);
		return 1;
	}

	public int update(Table table, HashMap<returnType, Object> map) throws SQLException {
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
						throw new SQLException("Mismatching in data types!!");
					}

					indexOfCond = i;
					break;
				}
			}
		}

		// can't find the col in the condition
		if (!noConditionCase && indexOfCond == -1) {
			throw new SQLException("'WHERE' clause is NOT found!!");
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
						throw new SQLException("Mismatching in data types!!");
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


	public int delete(Table table, HashMap<returnType, Object> map) throws SQLException {
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
					if (operands[1].contains("'") && table.getColsDataTypes()[i].equals("int")) {
						throw new SQLException("Mismatching in data types!!");
					}

					indexOfCond = i;
					break;
				}
			}
		}

		// can't find the col in the condition
		if (!noConditionCase && indexOfCond == -1) {
			throw new SQLException("'WHERE' clause is NOT found!!");
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
	
	private String[] getUserValues(HashMap<returnType, Object> map) {
		String[] values = null;
		if (map.containsKey(returnType.COLVALUES)) {
			values = Arrays.copyOf((Object[]) map.get(returnType.COLVALUES),
					((Object[]) map.get(returnType.COLVALUES)).length, String[].class);
		}
		return values;
	}

	private String[] getUserColsNames(HashMap<returnType, Object> map) {
		String[] colsNames = null;
		if (map.containsKey(returnType.COLNAME)) {
			colsNames = Arrays.copyOf((Object[]) map.get(returnType.COLNAME),
					((Object[]) map.get(returnType.COLNAME)).length, String[].class);
		}
		return colsNames;
	}

	private String getCondOperator(HashMap<returnType, Object> map) {
		String operator = null;
		if (map.containsKey(returnType.CONDITIONOPERATOR)) {
			operator = (String) map.get(returnType.CONDITIONOPERATOR);
		}
		return operator;

	}

	private String[] getCondOperands(HashMap<returnType, Object> map) {
		String[] operands = null;
		if (map.containsKey(returnType.CONDITIONOPERANDS)) {
			operands = (String[]) map.get(returnType.CONDITIONOPERANDS);
		}
		return operands;
	}

	//TODO delete this
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
			e.printStackTrace();
			return null;
		}

		return column.toArray(new Item[column.size()]);
	}
}
