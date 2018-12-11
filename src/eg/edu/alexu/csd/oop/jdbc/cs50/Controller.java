package eg.edu.alexu.csd.oop.jdbc.cs50;

import eg.edu.alexu.csd.oop.db.cs28.Facade;
import eg.edu.alexu.csd.oop.db.cs28.SQLDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

public class Controller {
    static Statement statement;
    @FXML
    TextArea logger;
    @FXML
    TextArea batch;
    public TextArea console = new TextArea();
    public String query;
    public int begin = 0;
    public Facade database = new Facade(new SQLDatabase());


    @FXML
    void add() {
        for (String line : batch.getText().split("\\n")) {
            try {
                statement.addBatch(line);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        loggerUpdate();
    }

    static void setStatement(Statement st) {
        statement = st;
    }

    void loggerUpdate() {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("log.txt")))) {
            logger.setText("");
            String line;
            while ((line = reader.readLine()) != null) {
                logger.setText(logger.getText() + line + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.setScrollTop(logger.getLength());
    }

    public void consoleEditor() {

        console.setOnKeyPressed(

                event -> {
                    String s = event.getText();
                    if (s.equals("\r")) {
                        String[] lines = console.getText().split("\\n");
                        query = lines[0];
                        begin = console.getText().length() + 1;
                        if (query.equalsIgnoreCase("clear")) {
                            console.setText(null);
                            begin = 0;
                        } else {
                            try {
                                database.executeQuery(query);
                                if (query.toLowerCase().contains("select")) {
                                    Object[][] t = database.getSelected();

                                    for (int i = 0; i < t.length; i++) {
                                        console.setText(console.getText() + "\n");
                                        for (int j = 0; j < t[0].length; j++) {
                                            console.setText(console.getText() + "		" + t[i][j]);
                                        }
                                    }
                                    begin = console.getText().length() + 1;

                                }
                            } catch (Exception e) {
                                // TODO: handle exception
                                if (e.getMessage().equals(null)) {
                                    console.setText("Invalid Query!\n" + console.getText());
                                } else {
                                    console.setText(e.getMessage() + "\n" + console.getText());
                                }
                                begin = console.getText().length() + 1;
                            }
                        }
                    }
                }
        );
        console.setText("\n" + console.getText());
        loggerUpdate();
    }
}
