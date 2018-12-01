package eg.edu.alexu.csd.oop.db.cs28;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Facade database = new Facade(new SQLDatabase());
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String query = sc.nextLine();
            try {
                database.executeQuery(query);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
