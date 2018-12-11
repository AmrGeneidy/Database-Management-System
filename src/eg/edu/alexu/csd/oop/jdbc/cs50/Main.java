package eg.edu.alexu.csd.oop.jdbc.cs50;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("MainDesign.fxml"));
        primaryStage.setTitle("JDBC");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

        Driver driver = new DriverImp();
        Properties info = new Properties();
        File dbDir = new File("workspace");
        info.put("path", dbDir.getAbsoluteFile());
        Connection connection;
        try {
            connection = driver.connect("jdbc:xmldb://localhost", info);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}