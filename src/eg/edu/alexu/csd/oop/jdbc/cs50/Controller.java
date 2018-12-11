package eg.edu.alexu.csd.oop.jdbc.cs50;



import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import eg.edu.alexu.csd.oop.db.cs28.Facade;
import eg.edu.alexu.csd.oop.db.cs28.SQLDatabase;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class Controller {
	public String query;
	public int begin = 0;
	public Facade database = new Facade(new SQLDatabase());
	public TextArea console = new TextArea();
	public TextArea logger = new TextArea();
	public TextArea batch = new TextArea();
	public Button addBatch = new Button();
	int counter = 0;
	KeyEvent arg0;
	
	
	
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
    							if(query.toLowerCase().contains("select")) {
    								Object[][] t = database.getSelected();
    								
    								for(int i = 0; i < t.length; i++) {
    									console.setText(console.getText() + "\n");
    									for(int j = 0; j < t[0].length; j++) {
    										console.setText(console.getText() + "		" + t[i][j]);
    									}
    								}
    								begin = console.getText().length() + 1;
    								
    							}
    						} catch (Exception e) {
    							// TODO: handle exception
    							if(e.getMessage().equals(null)) {
    								console.setText(console.getText() + "\nInvalid Query!");
    							}
    							else {
    								console.setText(console.getText() + "\n" + e.getMessage());	
    							}
    							begin = console.getText().length() + 1;
    							
    						}
    						

    					}

    				}
    				
    				
    			}
    			);
    	
    	
    	
    	
    }
    
    
    
    
    
    

}
