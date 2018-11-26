package eg.edu.alexu.csd.oop.db.cs28;

import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.cs28.Parser.returnType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;

public class SQLDatabase implements Database {
	private String currentDatabase;
    @Override
    public String createDatabase(String databaseName, boolean dropIfExists) {
    	databaseName = databaseName.toLowerCase(); 
    	File db = new File(databaseName);
    	if (db.exists()) {
			if (dropIfExists) {
				try {
					executeStructureQuery("DROP DATABASE "+databaseName);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			else 
				currentDatabase = databaseName;
		}
    	else {
    		currentDatabase = databaseName;
    		try {
				executeStructureQuery("CREATE DATABASE "+databaseName);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        return db.getAbsolutePath();
    }

    @Override
    public boolean executeStructureQuery(String query) throws SQLException {
    	Parser parser = new Parser();
    	if (!parser.executeStructureQuery(query)) {
			throw new SQLException();
		}
    	HashMap<returnType, Object> map = parser.map;
    	if ((boolean)map.get(returnType.ISDATABASE)&&(boolean)map.get(returnType.ISCREATE)) {
			File dbDir = new File(((String) map.get(returnType.NAME)).toLowerCase());
			dbDir.mkdirs();
			currentDatabase = ((String) map.get(returnType.NAME)).toLowerCase();
			return true;
		}else if ((boolean)map.get(returnType.ISDATABASE)&&!(boolean)map.get(returnType.ISCREATE)) {
			File dbDir = new File(((String) map.get(returnType.NAME)).toLowerCase());
			File[] listFiles = dbDir.listFiles();
			for(File file : listFiles){
				file.delete();
			}
			return true;
		}else if(!(boolean)map.get(returnType.ISDATABASE)&&!(boolean)map.get(returnType.ISCREATE)) {
			new File(currentDatabase+ System.getProperty("file.separator")+((String) map.get(returnType.NAME)).toLowerCase()+".xml").delete();
			new File(currentDatabase+ System.getProperty("file.separator")+((String) map.get(returnType.NAME)).toLowerCase()+".dtd").delete();
		}else {
			try {
				File x = new File(currentDatabase+ System.getProperty("file.separator")+((String) map.get(returnType.NAME)).toLowerCase()+".xml");
				if (x.exists()) {
					return false;
				}
				x.createNewFile();
			} catch (IOException e) {
				return false;
			}
			try {
				new File(currentDatabase+ System.getProperty("file.separator")+((String) map.get(returnType.NAME)).toLowerCase()+".dtd").createNewFile();
				@SuppressWarnings("resource")
				PrintWriter writer= new PrintWriter(currentDatabase+ System.getProperty("file.separator")+((String) map.get(returnType.NAME)).toLowerCase()+".dtd");
				writer.print("<!ELEMENT row (");
				String [] colName = (String[]) map.get(returnType.COLNAME);
				for (int i = 0; i < colName.length; i++) {
					if (i < colName.length - 1) {
						writer.print(colName[i]+",");
					} else {
						writer.println(colName[i]+")>");
					}
				}
				String [] coltype = (String[]) map.get(returnType.COLTYPE);
				for (int i = 0; i < colName.length; i++) {
					writer.println("<!ELEMENT "+colName[i]+" "+coltype[i]+">");
				}
				writer.close();
			} catch (FileNotFoundException e) {
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return true;
		}
        return false;
    }

    @Override
    public Object[][] executeQuery(String query) throws SQLException {
        return new Object[0][];
    }

    @Override
    public int executeUpdateQuery(String query) throws SQLException {
        return 0;
    }
}
