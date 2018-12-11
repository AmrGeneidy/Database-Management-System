package eg.edu.alexu.csd.oop.jdbc.cs50;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

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
}
