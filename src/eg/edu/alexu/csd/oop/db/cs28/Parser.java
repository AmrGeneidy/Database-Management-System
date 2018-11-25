package eg.edu.alexu.csd.oop.db.cs28;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static int lastMatchedIndex;
    static HashMap<returnType, Object> map;

    public boolean executeStructureQuery(String query) {
        boolean matched = false;
        map = new HashMap<>();
        String[] queries = {"CREATE DATABASE", "DROP DATABASE", "CREATE TABLE", "DROP TABLE"};
        for (String q : queries) {
            if (regexChecker(q, query)) {
                String s = query.substring(lastMatchedIndex + 1);
                switch (q) {
                    case "CREATE DATABASE":
                        s.replaceAll("\\s+", "");
                        map.put(returnType.ISDATABASE, true);
                        map.put(returnType.ISCREATE, true);
                        map.put(returnType.NAME, s);
                        break;
                    case "DROP DATABASE":
                        s.replaceAll("\\s+", "");
                        map.put(returnType.ISDATABASE, true);
                        map.put(returnType.ISCREATE, false);
                        map.put(returnType.NAME, s);
                        break;
                    case "CREATE TABLE":
                        if (s.indexOf('(') != -1) {
                            map.put(returnType.NAME, s.substring(0, s.indexOf('(')));
                            s = s.substring(s.indexOf('(') + 1, s.indexOf(')'));
                            String[] nameAndType = s.split(",");
                            String[] colName = new String[nameAndType.length];
                            String[] colType = new String[nameAndType.length];
                            for (int i = 0; i < nameAndType.length; i++) {
                                String[] temp = nameAndType[i].split(" ");
                                int spaceShift = 0;
                                while (spaceShift < temp.length && (temp[spaceShift].equals(""))) spaceShift++;
                                colName[i] = temp[spaceShift];
                                colType[i] = temp[spaceShift + 1];
                            }
                            map.put(returnType.COLNAME, colName);
                            map.put(returnType.COLTYPE, colType);
                        } else {
                            map.put(returnType.NAME, s);
                        }
                        map.put(returnType.ISDATABASE, false);
                        map.put(returnType.ISCREATE, true);
                        break;
                    case "DROP TABLE":
                        s.replaceAll("\\s+", "");
                        map.put(returnType.ISDATABASE, false);
                        map.put(returnType.ISCREATE, false);
                        map.put(returnType.NAME, s);
                        break;
                }
                matched = true;
            }
        }
        if (matched) {
            return true;
        } else return false;
    }


    public Object[][] executeQuery(String query) {
        return new Object[0][];
    }

    public int executeUpdateQuery(String query) {
        return 0;
    }

    enum returnType {
        NAME,
        ISCREATE,
        ISDATABASE,
        COLNAME,
        COLTYPE;
    }

    private static boolean regexChecker(String regex, String strToCheck) {
        boolean isMatched = false;
        Pattern checkRegex = Pattern.compile(regex);
        Matcher regexMatcher = checkRegex.matcher(strToCheck);

        while (regexMatcher.find()) {
            isMatched = true;
            if (regexMatcher.group().length() != 0) {
                System.out.println(regexMatcher.group().trim());
            }
            System.out.println("Start Index :" + regexMatcher.start());
            System.out.println("End Index :" + regexMatcher.end());
            lastMatchedIndex = regexMatcher.end();
        }
        return isMatched;
    }

}
