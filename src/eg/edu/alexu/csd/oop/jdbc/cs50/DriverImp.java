package eg.edu.alexu.csd.oop.jdbc.cs50;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.cs28.SQLDatabase;

public class DriverImp implements Driver {
	//Pool
	private List<Connection> connections;
	

	// limited number of connections (Pool)
	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		//TODO temp implementation
		Connection x;
		if (acceptsURL(url)) {
			File dir = (File) info.get("path");
			String path = dir.getAbsolutePath();
			Database database = new SQLDatabase(path);
			x = new ConnectionImp(database,info);
			//connections.add(x);
		} else {
			// TODO add log and exception message
			throw new SQLException();
		}

		return x;
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		if (url.equalsIgnoreCase("jdbc:xmldb://localhost"))
			return true;
		else
			return false;
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMajorVersion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMinorVersion() {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean jdbcCompliant() {
		throw new UnsupportedOperationException();

	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new UnsupportedOperationException();

	}

}
