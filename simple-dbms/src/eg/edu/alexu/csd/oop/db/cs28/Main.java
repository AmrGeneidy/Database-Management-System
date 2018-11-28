package eg.edu.alexu.csd.oop.db.cs28;

import java.io.File;
import java.sql.SQLException;

import org.junit.Assert;

import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.test.TestRunner;

public class Main {

	private static File createDatabase(Database db, String name, boolean drop){
		String path = db.createDatabase("sample" + System.getProperty("file.separator") + name, drop); // create database
		//System.out.println(path);
		Assert.assertNotNull("Failed to create database", path);
		File dbDir = new File(path);
		Assert.assertTrue("Database directory is not found or not a directory", dbDir.exists() && dbDir.isDirectory());
		return dbDir;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SQLDatabase d = new SQLDatabase();
		d.createDatabase("Customers", false);
		
		
		try {
			
			d.executeQuery("SELECT * FROM demo WHERE marks>=85");
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

}
