package eg.edu.alexu.csd.oop.db.cs28;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import eg.edu.alexu.csd.oop.db.cs28.Parser.returnType;

public class Table {

    private static Table singleTable;

    private String tableFullPathXML;
    private String tableFullPathDTD;
    private ArrayList<Record> tableData;
    private String[] colsNames;
    private String[] colsDataTypes;

    private Table() {
    }

    // clear Cache and get new instance
    public static Table loadNewTable(String xmlPath) throws SQLException {
        if (singleTable == null) {
            singleTable = new Table();
        }
        if (singleTable.tableFullPathXML != null && xmlPath.equalsIgnoreCase(singleTable.tableFullPathXML)) {
            return singleTable;
        }
        singleTable.tableFullPathXML = xmlPath;
        singleTable.tableFullPathDTD = singleTable.getDTDPath();
        singleTable.loadData();
        return singleTable;
    }

    // setters & getters
    public ArrayList<Record> getTableData() {
        return tableData;
    }

    public void setTableData(ArrayList<Record> tableData) {
        this.tableData = tableData;
    }

    public String[] getColsNames() {
        return colsNames;
    }

    public String[] getColsDataTypes() {
        return colsDataTypes;
    }

    public int insert(HashMap<returnType, Object> map) throws SQLException {
        ModifyTable m = new ModifyTable();
        int ans = m.insert(singleTable, map);
        if (ans != 0) {
            saveDataInXML();
        }
        return ans;
    }

    public int update(HashMap<returnType, Object> map) throws SQLException {
        ModifyTable m = new ModifyTable();
        int ans = m.update(singleTable, map);
        if (ans != 0) {
            saveDataInXML();
        }
        return ans;
    }

    public int delete(HashMap<returnType, Object> map) throws SQLException {
        ModifyTable m = new ModifyTable();
        int ans = m.delete(singleTable, map);
        if (ans != 0) {
            saveDataInXML();
        }
        return ans;
    }

    // save the data from table(cache) in xml file
    private void saveDataInXML() throws SQLException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            File file = new File(tableFullPathXML);
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();

            while (root.hasChildNodes()) {
                root.removeChild(root.getFirstChild());
            }

            Element xmlRecord;
            for (Record record : tableData) {
                xmlRecord = doc.createElement("record");
                for (int i = 0; i < record.length(); i++) {
                    Element tag = doc.createElement(colsNames[i]);
                    Text value = doc.createTextNode(record.getItem(i));
                    tag.appendChild(value);
                    xmlRecord.appendChild(tag);
                }
                root.appendChild(xmlRecord);
            }
            file.delete();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(tableFullPathXML));
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException e) {
            throw new SQLException("Error while saving data into file!!");
        } catch (SAXException | IOException e) {
            throw new SQLException("Table NOT Found!!");
        }

    }

    // fill colsNames & colsDataTypes
    private boolean readDTD() throws SQLException {
        BufferedReader reader = null;
        Pattern pattern = Pattern.compile("<!ELEMENT (\\S+) (\\S+)>");
        Matcher matcher = null;
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> dataTypes = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(singleTable.tableFullPathDTD));
            reader.readLine();
            String thisLine = reader.readLine();
            while (thisLine != null) {
                matcher = pattern.matcher(thisLine);
                matcher.matches();
                names.add(matcher.group(1));
                dataTypes.add(matcher.group(2));
                thisLine = reader.readLine();
            }
            singleTable.colsNames = names.toArray(new String[names.size()]);
            singleTable.colsDataTypes = dataTypes.toArray(new String[dataTypes.size()]);
            reader.close();
        } catch (IOException e) {
            throw new SQLException("DTD file not found");
        }
        return true;
    }

    private void loadData() throws SQLException {
        singleTable.readDTD();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(tableFullPathXML));
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("record");
            tableData = new ArrayList<Record>();
            String[] record = new String[colsNames.length];
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    for (int j = 0; j < colsNames.length; j++) {
                        Element eElement = (Element) nNode;
                        String value = eElement.getElementsByTagName(colsNames[j]).item(0).getTextContent();
                        record[j] = value;
                    }
                    tableData.add(i, new Record(record));
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new SQLException("XML file not found");
        }
    }

    private String getDTDPath() {
        String ans = tableFullPathXML.substring(0, tableFullPathXML.length() - 3) + "dtd";
        return ans;
    }
}
