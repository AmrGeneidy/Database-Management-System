package eg.edu.alexu.csd.oop.db.cs28;

import java.sql.SQLException;

import eg.edu.alexu.csd.oop.db.Database;

public class Facade {
	Database database;

	public Facade(Database database) {
		this.database = database;
	}

	public void excuteQuery(String query) {
		try {
			if (query.contains("CREATE") || query.contains("DROP")) {
				if (!database.executeStructureQuery(query)) {
					throw new RuntimeException("couldn't excute the query due to error in file system");
				}
			} else if (query.contains("SELECT")) {
				database.executeQuery(query);
			} else if (query.contains("INSERT") || query.contains("DELETE") || query.contains("UPDATE")) {
				database.executeUpdateQuery(query);
			} else {
				throw new RuntimeException("Invalid Query!!");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

}
