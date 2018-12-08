package eg.edu.alexu.csd.oop.jdbc.cs50;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;


public class ResultSetMetaDataImp implements ResultSetMetaData {
	private String TableName;
	private String[] ColName ;
	private Types[] ColType ;
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getColumnCount() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
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
	public String getColumnLabel(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
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
	public String getTableName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCatalogName(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getColumnType(int column) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getColumnTypeName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
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
