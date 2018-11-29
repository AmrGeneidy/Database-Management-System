package eg.edu.alexu.csd.oop.db.cs28;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        SQLDatabase d = new SQLDatabase();
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String query = sc.nextLine();
            String upperQ = query.toUpperCase();
            try {
                if(upperQ.contains("CREATE") || upperQ.contains("DROP")) {
                    d.executeStructureQuery(query);
                } else if(upperQ.contains("SELECT")) {
                    d.executeQuery(query);
                } else if(upperQ.contains("INSERT") || upperQ.contains("DELETE") || upperQ.contains("UPDATE")) {
                    d.executeUpdateQuery(query);
                } else {
                    throw new RuntimeException("Invalid Query!!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
