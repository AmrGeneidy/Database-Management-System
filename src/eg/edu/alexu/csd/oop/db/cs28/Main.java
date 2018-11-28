package eg.edu.alexu.csd.oop.db.cs28;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        SQLDatabase d = new SQLDatabase();
//		d.createDatabase("Customers", false);
        try {
//			d.executeQuery("    SELECT     column_name1     FROM      table_name13      WHERE     coluMN_NAME2    <    5      ");
            d.executeStructureQuery("CREATE   TABLE   table_name1(column_name1 varchar , column_name2    int,  column_name3 varchar)       ");
//            d.executeUpdateQuery("INSERT     INTO table_name13(column_NAME1, column_name2, COLUMN_name3) VALUES ('value1', 4, 'value3')");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

}
