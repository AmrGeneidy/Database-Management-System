package eg.edu.alexu.csd.oop.db.cs28;

import java.sql.SQLException;

import eg.edu.alexu.csd.oop.db.Database;

public class Facade {
    private Database database;
    private Object[][] selected;

    public Facade(Database database) {
        this.database = database;
    }

    public Object[][] getSelected() {
        return this.selected;
    }

    public void executeQuery(String query) {
        try {
            if (query.toUpperCase().contains("CREATE") || query.toUpperCase().contains("DROP")) {
                if (!database.executeStructureQuery(query)) {
                    throw new RuntimeException("couldn't excute the query due to error in file system");
                }
            } else if (query.toUpperCase().contains("SELECT")) {
                try {
                    selected = database.executeQuery(query);
                } catch (Exception e) {
                    // TODO: handle exception
                    throw new RuntimeException("There is no data recorded!");
                }
            } else if (query.toUpperCase().contains("INSERT") || query.toUpperCase().contains("DELETE") || query.toUpperCase().contains("UPDATE")) {
                database.executeUpdateQuery(query);
            } else {
                throw new RuntimeException("Invalid Query!!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
