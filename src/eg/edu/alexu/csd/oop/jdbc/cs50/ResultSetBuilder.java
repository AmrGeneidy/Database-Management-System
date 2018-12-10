package eg.edu.alexu.csd.oop.jdbc.cs50;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import eg.edu.alexu.csd.oop.db.Database;

public class ResultSetBuilder {
	private Database db;
	private String query;
	private Statement statement;
	
	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public void setDb(Database db) {
		this.db = db;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	public void getQuery(String query) {
		this.query = query;
	}

	public ResultSet build() throws SQLException {
		Object [][] set = db.executeQuery(query);
		String tableName = db.getTableName();
		String [] colName = db.getColName();
		String [] colType = db.getColTypes();
		String [] cType = new String[colType.length];
		for (int i = 0; i < cType.length; i++) {
			if (colType[i].equalsIgnoreCase("varchar") ) {
				cType[i] = "varchar";
			}else {
				cType[i] = "int";
			}
		}
		ResultSetMetaData  metaData = new ResultSetMetaDataImp(tableName, colName, cType);
		return new ResultsetImp(set,metaData, statement);
		
	}
}
