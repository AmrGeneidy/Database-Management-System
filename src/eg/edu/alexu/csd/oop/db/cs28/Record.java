package eg.edu.alexu.csd.oop.db.cs28;

public class Record {
	private String[] record;
	public Record(String[] record) {
		this.record = record;
	}
	
	public int length() {
		return record.length;
	}
	
	public String getItem(int index) {
		return record[index];
	}
	
	public void setItem(int index, String value) {
		this.record[index] = value;
	}
	
}
