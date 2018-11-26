package eg.edu.alexu.csd.oop.db.cs28;

import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.cs28.Parser.returnType;

import java.sql.SQLException;
import java.util.HashMap;

public class SQLDatabase implements Database {
	@Override
	public String createDatabase(String databaseName, boolean dropIfExists) {
		return null;
	}

	@Override
	public boolean executeStructureQuery(String query) throws SQLException {
		return false;
	}

	@Override
	public Object[][] executeQuery(String query) throws SQLException {
		return new Object[0][];
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
			rowsCount = ModifyTable.insert(null/*workSpacePath*/,map);
		}else if ((boolean)map.get(returnType.ISUPDATE)) {
			rowsCount = ModifyTable.update(null/*workSpacePath*/,map);
		}else if ((boolean)map.get(returnType.ISDELETE)) {
			rowsCount = ModifyTable.delete(null/*workSpacePath*/,map);
		}
	
		return rowsCount;
	}
}
