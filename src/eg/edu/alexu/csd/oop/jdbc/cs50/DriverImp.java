package eg.edu.alexu.csd.oop.jdbc.cs50;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class DriverImp implements Driver {

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		Connection x = new ConnectionImp();
		return x;
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		if(url.equalsIgnoreCase("jdbc:xmldb://localhost"))
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
