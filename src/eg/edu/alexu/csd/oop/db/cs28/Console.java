package eg.edu.alexu.csd.oop.db.cs28;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JTextArea;

import eg.edu.alexu.csd.oop.db.cs28.Parser.returnType;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.HashMap;

public class Console {

	private JFrame frame;
	private String query;
	private int begin = 0;
	private Facade database = new Facade(new SQLDatabase());
	private Table table;
	private String cDatabase;
	private SQLDatabase sql = new SQLDatabase();
	private Parser parser = new Parser();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Console window = new Console();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Console() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		HashMap<returnType, Object> data = parser.map;
		cDatabase = sql.getCurrentDataBase();
		
		frame = new JFrame("SQL Database System");
		frame.setBounds(100, 100, 575, 419);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Berlin Sans FB", Font.PLAIN, 18));
		textArea.setForeground(Color.WHITE);
		textArea.setBackground(Color.BLACK);
		textArea.setBounds(10, 11, 539, 369);
		panel.setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane(textArea); // place the JTextArea in a scroll pane
		panel.add(scroll, BorderLayout.CENTER);
		panel.setBackground(Color.BLACK);
		frame.getContentPane().add(panel, BorderLayout.CENTER);

		
		
		
		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == 10) {
					query = textArea.getText().substring(begin, textArea.getText().length());
					begin = textArea.getText().length() + 1;
					if (query.equalsIgnoreCase("clear")) {
						textArea.setText(null);
						begin = 0;
					} else {
						
						try {
							database.executeQuery(query);
							if(query.toLowerCase().contains("select")) {
								Object[][] t = database.getSelected();
								
								for(int i = 0; i < t.length; i++) {
									textArea.setText(textArea.getText() + "\n");
									for(int j = 0; j < t[0].length; j++) {
										textArea.setText(textArea.getText() + "		" + t[i][j]);
									}
								}
								begin = textArea.getText().length() + 1;
								
							}
							
						} catch (Exception e) {
							// TODO: handle exception
							if(e.getMessage().equals(null)) {
								textArea.setText(textArea.getText() + "\nInvalid Query!");
							}
							else {
								textArea.setText(textArea.getText() + "\n" + e.getMessage());	
							}
							begin = textArea.getText().length() + 1;
							
						}

					}

				}
			}
		});

	}

}
