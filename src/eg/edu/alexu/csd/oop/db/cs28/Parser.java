package eg.edu.alexu.csd.oop.db.cs28;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private int lastMatchedIndex;
    HashMap<returnType, Object> map;

    public boolean executeStructureQuery(String query) {
        boolean matched = false;
        map = new HashMap<>();
        if (regexChecker("CREATE[\\s]+DATABASE", query.toUpperCase())) {
            String s = query.substring(lastMatchedIndex + 1).trim();
            map.put(returnType.ISDATABASE, true);
            map.put(returnType.ISCREATE, true);
            map.put(returnType.NAME, s);
            matched = true;
        } else if (regexChecker("DROP[\\s]+DATABASE", query.toUpperCase())) {
            String s = query.substring(lastMatchedIndex + 1).trim();
            map.put(returnType.ISDATABASE, true);
            map.put(returnType.ISCREATE, false);
            map.put(returnType.NAME, s);
            matched = true;
        } else if (regexChecker("CREATE[\\s]+TABLE", query.toUpperCase())) {
            String s = query.substring(lastMatchedIndex + 1).trim();
            if (s.indexOf('(') != -1) {
                map.put(returnType.NAME, s.substring(0, s.indexOf('(')));
                s = s.substring(s.indexOf('(') + 1, s.indexOf(')'));
                String[] nameAndType = s.split(",");
                String[] colName = new String[nameAndType.length];
                String[] colType = new String[nameAndType.length];
                for (int i = 0; i < nameAndType.length; i++) {
                    String[] temp = nameAndType[i].trim().replaceAll("[\\s]+", " ").split(" ");
                    colName[i] = temp[0];
                    colType[i] = temp[1];
                }
                if(colName.length == 0) throw new RuntimeException("Invalid Query!!");
                map.put(returnType.COLNAME, colName);
                map.put(returnType.COLTYPE, colType);
            } else {
                return false;
            }
            map.put(returnType.ISDATABASE, false);
            map.put(returnType.ISCREATE, true);
            matched = true;
        } else if (regexChecker("DROP[\\s]+TABLE", query.toUpperCase())) {
            String s = query.substring(lastMatchedIndex + 1).trim();
            map.put(returnType.ISDATABASE, false);
            map.put(returnType.ISCREATE, false);
            map.put(returnType.NAME, s);
            matched = true;
        }
        if (matched) {
            return true;
        } else return false;
    }

    public boolean executeQuery(String query) {
        map = new HashMap<>();
        if (regexChecker("SELECT", query.toUpperCase())) {
            String s = query.substring(lastMatchedIndex + 1);
            String[] colCollector = s.split("(?i)FROM");
            if (!colCollector[0].trim().equals("*")) {
                String[] col = colCollector[0].split(",");
                for (int i = 0; i < col.length; i++) {
                    col[i] = col[i].trim();
                }
                if (col[0].replaceAll("\\s+", "").equals("")) return false;
                if(col.length == 0) throw new RuntimeException("Invalid Query!!");
                map.put(returnType.COLNAME, col);
            }
            s = colCollector[1];
            String[] collector = s.split("(?i)WHERE");
            map.put(returnType.NAME, collector[0].trim());
            conditionFinder(s, collector);
        }
        return true;
    }

    public boolean executeUpdateQuery(String query) {
        boolean matched = false;
        map = new HashMap<>();
        if (regexChecker("INSERT[\\s]+INTO", query.toUpperCase())) {
            String s = query.substring(lastMatchedIndex + 1);
            if (regexChecker("\\*", s)) return false;
            ArrayList<Object> col = new ArrayList<>();
            ArrayList<Object> val = new ArrayList<>();
            regexUpdateQuery(s, "VALUES", col, val);
            if(val.isEmpty()) throw new RuntimeException("Invalid Query!!");
            map.put(returnType.COLNAME, col.toArray());
            map.put(returnType.COLVALUES, val.toArray());
            map.put(returnType.ISUPDATE, false);
            map.put(returnType.ISDELETE, false);
            map.put(returnType.ISINSERT, true);
            matched = true;
        } else if (regexChecker("DELETE[\\s]+FROM", query.toUpperCase())) {
            String s = query.substring(lastMatchedIndex + 1);
            String[] conditionCollector = s.split("(?i)WHERE");
            map.put(returnType.NAME, conditionCollector[0].trim());
            conditionFinder(s, conditionCollector);
            map.put(returnType.ISUPDATE, false);
            map.put(returnType.ISDELETE, true);
            map.put(returnType.ISINSERT, false);
            matched = true;
        } else if (regexChecker("UPDATE", query.toUpperCase())) {
            String s = query.substring(lastMatchedIndex + 1);
            String[] collector = s.split("(?i)WHERE");
            ArrayList<Object> column = new ArrayList<>();
            ArrayList<Object> value = new ArrayList<>();
            updateSetter(collector[0], column, value);
            if(column.isEmpty() || value.isEmpty()) throw new RuntimeException("Invalid Query!!");
            map.put(returnType.COLNAME, column.toArray());
            map.put(returnType.COLVALUES, value.toArray());
            conditionFinder(s, collector);
            map.put(returnType.ISUPDATE, true);
            map.put(returnType.ISDELETE, false);
            map.put(returnType.ISINSERT, false);
            matched = true;
        }
        if (matched) {
            return true;
        } else return false;
    }

    enum returnType {
        NAME,
        ISCREATE,
        ISDATABASE,
        COLNAME,
        COLTYPE,
        COLVALUES,
        ISINSERT,
        ISUPDATE,
        ISDELETE,
        CONDITIONOPERATOR,
        CONDITIONOPERANDS
    }

    private boolean regexChecker(String regex, String strToCheck) {
        boolean isMatched = false;
        Pattern checkRegex = Pattern.compile(regex);
        Matcher regexMatcher = checkRegex.matcher(strToCheck);

        while (regexMatcher.find()) {
            isMatched = true;
            lastMatchedIndex = regexMatcher.end();
            break;
        }
        return isMatched;
    }

    private void regexUpdateQuery(String query, String separator, ArrayList<Object> col, ArrayList<Object> val) {
        Pattern regex = Pattern.compile("[a-zA-Z0-9_]+");
        Matcher matcher = regex.matcher(query);
        if (matcher.find()) map.put(returnType.NAME, matcher.group().trim());
        boolean foundSeparator = false;
        while (matcher.find()) {
            String s = matcher.group();
            if (s.toUpperCase().equals(separator)) {
                foundSeparator = true;
                continue;
            }
            if (!foundSeparator) {
                col.add(s);
            } else {
                val.add(s);
            }
        }
    }

    private void conditionFinder(String s, String[] collector) {
        if (s.toUpperCase().contains("WHERE")) {
            String condition = collector[1].trim();
            String[] possibleOperators = {"!=", "<>", "<=", ">=", "==", "<", ">", "="};
            for (String op : possibleOperators) {
                if (condition.contains(op)) {
                    String[] operands = condition.split(op);
                    for (int i = 0; i < operands.length; i++) {
                        Matcher matcher = Pattern.compile("\'{0,1}[A-Za-z0-9_]+\'{0,1}").matcher(operands[i]);
                        if (matcher.find()) {
                            operands[i] = matcher.group().trim();
                        }
                    }
                    map.put(returnType.CONDITIONOPERATOR, op);
                    map.put(returnType.CONDITIONOPERANDS, operands);
                    break;
                }
            }
        }
    }

    private boolean updateSetter(String s, ArrayList<Object> col, ArrayList<Object> val) {
        Pattern regex = Pattern.compile("\'{0,1}[a-zA-Z0-9_]+\'{0,1}");
        Matcher matcher = regex.matcher(s);
        matcher.find();
        map.put(returnType.NAME, matcher.group().trim());
        if (matcher.find() & matcher.group().toUpperCase().trim().equals("SET")) {
            while (matcher.find()) {
                col.add(matcher.group().trim());
                matcher.find();
                val.add(matcher.group().trim());
            }
        } else {
            return false;
        }
        return true;
    }
}
