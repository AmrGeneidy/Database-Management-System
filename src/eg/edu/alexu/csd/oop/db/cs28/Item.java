package eg.edu.alexu.csd.oop.db.cs28;


//TODO this class is duplicated (delete it)
public class Item {

	private String colName;
	private String dataType;
	private String value;
	
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Item(String colName, String dataType, String value) {
		this.colName= colName;
		this.dataType = dataType;
		this.value = value;
	}
	
	public Item(String colName, String dataType) {
		this.colName= colName;
		this.dataType = dataType;
	}
}

