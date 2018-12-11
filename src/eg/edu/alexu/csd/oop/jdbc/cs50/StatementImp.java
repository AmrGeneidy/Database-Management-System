package eg.edu.alexu.csd.oop.jdbc.cs50;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import eg.edu.alexu.csd.oop.db.Database;

public class StatementImp implements Statement {
	
	private Logger logger = Log.getLoggeer();
	private Database database;
	private Connection connection;
	private List<String> batch;
	private ResultSet currentResultSet; 
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
			logger.severe("Statement is closed!!");
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
	public ResultSet executeQuery(String sql) throws SQLException {
		checkIfClosed();
		checkIfTimeout();
		logger.info("executing query : " + sql);
		database.executeQuery(sql);
		ResultSetMetaDataImp resultMetaData = new ResultSetMetaDataImp(database.getTableName(),
				database.getColName(), this.database.getColTypes());
		currentResultSet = new ResultsetImp(database.executeQuery(sql) , resultMetaData, this);
		return currentResultSet;
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		checkIfClosed();
		checkIfTimeout();
		logger.info("executing modify query : " + sql);
		return this.database.executeUpdateQuery(sql);
	}

	@Override
	public void close() throws SQLException {
		database = null;
		connection = null;
		batch.clear();
		batch = null;
		if (currentResultSet != null) {
			currentResultSet.close();
		}
		isClosed = true;
		logger.info("Statement is closed successfully");

	}

	@Override
	public int getQueryTimeout() throws SQLException {
		checkIfClosed();
		logger.info("Get Query Timeout limit");
		return timeout;
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		checkIfClosed();
		// 0 seconds for no timeout limit
		if (seconds < 0) {
			logger.severe("Couldn't Set Timeout limit !!");
			throw new SQLException("Couldn't Set Timeout limit !!");
		}
		timeout = seconds;
		logger.info("Query timeout has been set properly");

	}

	@Override
	public boolean execute(String sql) throws SQLException {
		checkIfClosed();
		checkIfTimeout();
		if (sql.toUpperCase().contains("CREATE") || sql.toUpperCase().contains("DROP")) {
			logger.info("executing structure query : " + sql);
			return database.executeStructureQuery(sql);
		} else if (sql.toUpperCase().contains("SELECT")) {
			logger.info("executing query : " + sql);
			Object [][] x = database.executeQuery(sql);
			return x.length != 0;
		} else if (sql.toUpperCase().contains("INSERT") || sql.toUpperCase().contains("DELETE") || sql.toUpperCase().contains("UPDATE")) {
			logger.info("executing modify query : " + sql);
			int x = database.executeUpdateQuery(sql);
			return x != 0;
		} else {
			logger.severe("Invalid Query : " + sql);
			throw new SQLException("Invalid Query!!");
		}
		
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		checkIfClosed();
		logger.info("adding query to batch : " + sql);
		boolean containsInsert = sql.toLowerCase().contains("insert");
		boolean containsUpdate = sql.toLowerCase().contains("update");
		if (containsInsert || containsUpdate) {
			batch.add(sql);
			logger.info("Query has been added : " + sql);
		} else {
			logger.severe("NOT INSERT or UPDATE Query : " + sql);
			throw new SQLException("INSERT or UPDATE SQL statements only !!");
		}
	}

	@Override
	public void clearBatch() throws SQLException {
		checkIfClosed();
		logger.info("clearing batch");
		batch.clear();
		logger.info("Batch has been cleared successfully");
	}

	@Override
	public int[] executeBatch() throws SQLException {
		checkIfClosed();
		checkIfTimeout();
		logger.info("executing Batch");
		int[] numOfUpdatedRecordsInEachQuery = new int[batch.size()];
		for (int i = 0; i < numOfUpdatedRecordsInEachQuery.length; i++) {
			try {
				numOfUpdatedRecordsInEachQuery[i] = database.executeUpdateQuery(batch.get(i));
				logger.info("query executed successfully : " + batch.get(i));
			} catch (SQLException e) {
				numOfUpdatedRecordsInEachQuery[i] = EXECUTE_FAILED;
				logger.severe("query failed to execute : " + batch.get(i));
			}
		}
		logger.info("execute batch finished");
		return numOfUpdatedRecordsInEachQuery;
	}

	@Override
	public Connection getConnection() throws SQLException {
		checkIfClosed();
		logger.info("getting connection");
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
	
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();

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



}
