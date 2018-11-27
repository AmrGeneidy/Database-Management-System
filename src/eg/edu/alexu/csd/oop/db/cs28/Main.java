package eg.edu.alexu.csd.oop.db.cs28;

import java.sql.SQLException;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SQLDatabase d = new SQLDatabase();
		d.createDatabase("Customers", false);
		
		try {
			d.executeQuery("SELECT * FROM Customers");
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

}
