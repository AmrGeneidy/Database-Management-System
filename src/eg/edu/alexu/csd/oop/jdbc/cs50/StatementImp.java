package eg.edu.alexu.csd.oop.jdbc.cs50;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import eg.edu.alexu.csd.oop.db.Database;

//TODO need revision
public class StatementImp implements Statement {

	private Database database;
	private Connection connection;
	private List<String> batch;
	private boolean isClosed;
	private int timeout = 0;

	public StatementImp(Connection connection, Database database) {
		this.database = database;
		this.connection = connection;
		batch = new ArrayList<String>();
		isClosed = false;
	}

	private void checkIfClosed() throws SQLException {
		if (isClosed) {
			throw new SQLException("This statement is closed!!");
		}
	}

	private void checkIfTimeout() throws SQLTimeoutException {
		// TODO
		/*
		 * if(timeoutCondition) { throw new
		 * SQLTimeoutException("Process took too long !!"); }
		 */
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
	public ResultSet executeQuery(String sql) throws SQLException {
		checkIfClosed();
		checkIfTimeout();
		// TODO not finished yet
		this.database.executeQuery(sql);
		return null;
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		checkIfClosed();
		checkIfTimeout();
		return this.database.executeUpdateQuery(sql);
	}

	@Override
	public void close() throws SQLException {
		database = null;
		connection = null;
		batch = null;
		isClosed = true;
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public int getMaxRows() throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public int getQueryTimeout() throws SQLException {
		checkIfClosed();
		return timeout;
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		checkIfClosed();
		// 0 seconds for no timeout limit
		if (seconds < 0) {
			throw new SQLException("Couldn't Set Timeout limit !!");
		}
		timeout = seconds;

	}

	@Override
	public void cancel() throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void clearWarnings() throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setCursorName(String name) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean execute(String sql) throws SQLException {
		checkIfClosed();
		checkIfTimeout();
		// TODO: handle return
		return database.executeStructureQuery(sql);
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public int getUpdateCount() throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean getMoreResults() throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public int getFetchDirection() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public int getFetchSize() throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public int getResultSetType() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		checkIfClosed();
		boolean containsInsert = sql.toLowerCase().contains("insert");
		boolean containsUpdate = sql.toLowerCase().contains("update");
		// TODO if error exists in syntax don't add to batch
		if (containsInsert || containsUpdate) {
			batch.add(sql);
		} else {
			throw new SQLException("INSERT or UPDATE SQL statements only !!");
		}
	}

	@Override
	public void clearBatch() throws SQLException {
		checkIfClosed();
		batch.clear();
	}

	@Override
	public int[] executeBatch() throws SQLException {
		checkIfClosed();
		checkIfTimeout();
		int[] numOfUpdatedRecordsInEachQuery = new int[batch.size()];
		//TODO someone check if this is works
		for (int i = 0; i < numOfUpdatedRecordsInEachQuery.length; i++) {
			try {
				numOfUpdatedRecordsInEachQuery[i] = database.executeUpdateQuery(batch.get(i));
			} catch (SQLException e) {
				numOfUpdatedRecordsInEachQuery[i] = EXECUTE_FAILED;
			}
		}
		return numOfUpdatedRecordsInEachQuery;
	}

	@Override
	public Connection getConnection() throws SQLException {
		checkIfClosed();
		return this.connection;
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean isClosed() throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean isPoolable() throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void closeOnCompletion() throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		throw new UnsupportedOperationException();

	}

}
