package eg.edu.alexu.csd.oop.jdbc.cs50;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Logger;

public class ResultSetMetaDataImp implements ResultSetMetaData {
	
	private Logger logger = Log.getLoggeer();
	private String tableName;
	private String[] colName;
	private String[] colType;

	public ResultSetMetaDataImp(String tableName, String[] colName, String[] colType) {
		this.tableName = tableName;
		this.colName = colName;
		this.colType = colType;
	}
	
	@Override
	public int getColumnCount() throws SQLException {
		logger.info("getting column count");
		return colName.length;
	}
	
	@Override
	public String getColumnLabel(int column) throws SQLException {
		logger.info("getting column label");
		return colName[column - 1];
	}

	@Override
	public String getColumnName(int column) throws SQLException {
		logger.info("getting column name");
		return colName[column - 1];
	}
	
	@Override
	public String getTableName(int column) throws SQLException {
		logger.info("getting table name");
		return tableName;
	}
	
	@Override
	public int getColumnType(int column) throws SQLException {
		logger.info("getting column type");
		switch (colName[column]) {
		case "int":
			logger.info("type is integer");
			return Types.INTEGER;
		case "varchar":
			logger.info("type is varChar");
			return Types.VARCHAR;
		}
		logger.warning("couldn't determine the type!!");
		return 0;
	}

	@Override
	public String getColumnTypeName(int column) throws SQLException {
		logger.info("getting column type name");
		return colType[column];
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAutoIncrement(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCaseSensitive(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSearchable(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCurrency(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int isNullable(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSigned(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSchemaName(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPrecision(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getScale(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCatalogName(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isReadOnly(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWritable(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDefinitelyWritable(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getColumnClassName(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

}
