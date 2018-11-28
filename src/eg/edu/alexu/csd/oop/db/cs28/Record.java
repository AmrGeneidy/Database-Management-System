package eg.edu.alexu.csd.oop.db.cs28;

public class Record {
	private Item[] record;
	public Record(Item[] record) {
		this.record = record;
	}
	
	public int length() {
		return record.length;
	}
	
	public Item getItem(int index) {
		return record[index];
	}
	
}
