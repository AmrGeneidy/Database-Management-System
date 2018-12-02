package eg.edu.alexu.csd.oop.db.cs28;

import java.io.Console;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
 
public class Main extends Application {

    Button button;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    	
    	
    	
    	VBox layout = new VBox(20);
    	
    	Scene scene = new Scene(layout, 500, 500);
    	
    	
    	Console console = System.console();
    	primaryStage.setScene(scene);
    	primaryStage.show();
    	
        		
    }
}