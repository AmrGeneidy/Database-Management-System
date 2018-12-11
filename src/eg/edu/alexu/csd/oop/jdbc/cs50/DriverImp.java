package eg.edu.alexu.csd.oop.jdbc.cs50;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.cs28.SQLDatabase;

public class DriverImp implements Driver {
	
	private Properties info;
	
	//TODO  we must support Class.forName("foo.bah.Driver")
	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		if (acceptsURL(url)) {
			this.info = info;
			File dir = (File) info.get("path");
			String path = dir.getAbsolutePath();
			Database database = new SQLDatabase(path);
			return new ConnectionImp(database, info);
		} else {
			throw new SQLException("Couldn't connect to database !!");
		}

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
		try {
			if (acceptsURL(url)) {
				DriverPropertyInfo[] x = new DriverPropertyInfo[info.size()];
			    Enumeration<?> e = info.propertyNames();
				for (int i = 0; i < x.length; i++) {
			        String key = (String) e.nextElement();
					x[i] = new DriverPropertyInfo(key, this.info.getProperty(key));
				}
				return x;
			} else {
				throw new SQLException("Couldn't connect to database !!");
			}
		} catch (Exception e) {
			throw new SQLException("Couldn't get Driver Property Info !!");
		}
		
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

	public String getWorkSpace() {
		return info.getProperty("path");
	}

}
