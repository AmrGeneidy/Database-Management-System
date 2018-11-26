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
	private String workSpacePath = "TEST";
	private String currentDatabase;
    @Override
    public String createDatabase(String databaseName, boolean dropIfExists) {
        return null;
    }

    @Override
    public boolean executeStructureQuery(String query) throws SQLException {
    	Parser parser = new Parser();
    	if (!parser.executeStructureQuery(query)) {
			throw new SQLException();
		}
    	HashMap<returnType, Object> map = parser.map;
    	if ((boolean)map.get(returnType.ISDATABASE)&&(boolean)map.get(returnType.ISCREATE)) {
			File dbDir = new File(workSpacePath + System.getProperty("file.separator")+map.get(returnType.NAME));
			dbDir.mkdirs();
			currentDatabase = (String) map.get(returnType.NAME);
			return true;
		}else if ((boolean)map.get(returnType.ISDATABASE)&&!(boolean)map.get(returnType.ISCREATE)) {
			File dbDir = new File(workSpacePath + System.getProperty("file.separator")+map.get(returnType.NAME));
			File[] listFiles = dbDir.listFiles();
			for(File file : listFiles){
				file.delete();
			}
			dbDir.delete();
			return true;
		}else if(!(boolean)map.get(returnType.ISDATABASE)&&!(boolean)map.get(returnType.ISCREATE)) {
			new File(workSpacePath + System.getProperty("file.separator")+currentDatabase+ System.getProperty("file.separator")+map.get(returnType.NAME)+".xml").delete();
			new File(workSpacePath + System.getProperty("file.separator")+currentDatabase+ System.getProperty("file.separator")+map.get(returnType.NAME)+".dtd").delete();
		}else {
			try {
				new File(workSpacePath + System.getProperty("file.separator")+map.get(returnType.NAME)+".xml").createNewFile();
			} catch (IOException e) {
				return false;
			}
			try {
				PrintWriter writer= new PrintWriter(workSpacePath + System.getProperty("file.separator")+currentDatabase+ System.getProperty("file.separator")+map.get(returnType.NAME)+".dtd");
				writer.print("<!ELEMENT row (");
				String [] colName = (String[]) map.get(returnType.COLNAME);
				for (int i = 0; i < colName.length; i++) {
					if (i < colName.length - 1) {
						writer.print(colName[i]+",");
					} else {
						writer.print(colName[i]+">");
					}
				}
				String [] coltype = (String[]) map.get(returnType.COLTYPE);
				for (int i = 0; i < colName.length; i++) {
					writer.println("<!ELEMENT "+colName[i]+" "+coltype[i]+">");
				}
			} catch (FileNotFoundException e) {
				return false;
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
