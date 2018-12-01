package eg.edu.alexu.csd.oop.db.cs28;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    HashMap<returnType, Object> map;

    public boolean executeStructureQuery(String query) {
        boolean matched = false;
        map = new HashMap<>();
        Pattern crDBRegex = Pattern.compile("((?i)CREATE)([\\s]+)((?i)DATABASE)([\\s]+)([A-Za-z0-9_"+"/\\\\"+"_]+)");
        Matcher crDBMatcher = crDBRegex.matcher(query);
        Pattern drDBRegex = Pattern.compile("((?i)DROP)([\\s]+)((?i)DATABASE)([\\s]+)([A-Za-z0-9_"+"/\\\\"+"_]+)");
        Matcher drDBMatcher = drDBRegex.matcher(query);
        Pattern crTRegex1 = Pattern.compile("((?i)CREATE)([\\s]+)((?i)TABLE)([\\s]+)([A-Za-z0-9_"+"/\\\\"+"_]+)");
        Matcher crTMatcher1 = crTRegex1.matcher(query);
        Pattern crTRegex2 = Pattern.compile("((?i)CREATE)([\\s]+)((?i)TABLE)([\\s]+)([A-Za-z0-9_"+"/\\\\"+"_]+)([\\s]*)[(]([^)]+)[)]");
        Matcher crTMatcher2 = crTRegex2.matcher(query);
        Pattern drTRegex = Pattern.compile("((?i)DROP)([\\s]+)((?i)TABLE)([\\s]+)([A-Za-z0-9_"+"/\\\\"+"'_]+)");
        Matcher drTMatcher = drTRegex.matcher(query);
        if (crDBMatcher.find()) {
            map.put(returnType.NAME, crDBMatcher.group(5));
            map.put(returnType.ISDATABASE, true);
            map.put(returnType.ISCREATE, true);
            matched = true;
        } else if (drDBMatcher.find()) {
            map.put(returnType.NAME, drDBMatcher.group(5));
            map.put(returnType.ISDATABASE, true);
            map.put(returnType.ISCREATE, false);
            matched = true;
        } else if (crTMatcher1.find()) {
            map.put(returnType.NAME, crTMatcher1.group(5));
            if (!crTMatcher2.find()) return false;
            query = crTMatcher2.group(7);
            String[] nameAndType = query.split(",");
            String[] colName = new String[nameAndType.length];
            String[] colType = new String[nameAndType.length];
            for (int i = 0; i < nameAndType.length; i++) {
                String[] temp = nameAndType[i].trim().replaceAll("[\\s]+", " ").split(" ");
                colName[i] = temp[0].trim();
                colType[i] = temp[1].trim();
            }
            if (colName.length == 0) return false;
            map.put(returnType.COLNAME, colName);
            map.put(returnType.COLTYPE, colType);
            map.put(returnType.ISDATABASE, false);
            map.put(returnType.ISCREATE, true);
            matched = true;
        } else if (drTMatcher.find()) {
            map.put(returnType.NAME, drTMatcher.group(5));
            map.put(returnType.ISDATABASE, false);
            map.put(returnType.ISCREATE, false);
            matched = true;
        }
        if (matched) {
            return true;
        } else return false;
    }

    public boolean executeQuery(String query) {
        map = new HashMap<>();
        Pattern selectRegex = Pattern.compile("((?i)SELECT)[\\s]+(.+)[\\s]+((?i)FROM)[\\s]+(\'{0,1}[a-zA-Z0-9_]+\'{0,1})");
        Matcher selectMatcher = selectRegex.matcher(query);
        if (selectMatcher.find()) {
            map.put(returnType.NAME, selectMatcher.group(4));
            String selectColumns = selectMatcher.group(2);

            if (!selectColumns.trim().equals("*")) {
                String[] sColArr = selectColumns.split(",");
                for (int i = 0; i < sColArr.length; i++) {
                    sColArr[i] = sColArr[i].trim();
                }
                if (sColArr.length == 0 || sColArr[0].replaceAll("\\s+", "").equals("")) return false;
                map.put(returnType.COLNAME, sColArr);
            }
            Pattern selectConditionRegex = Pattern.compile("((?i)SELECT)[\\s]+(.+)[\\s]+((?i)FROM)[\\s]+(\'{0,1}[a-zA-Z0-9_]+\'{0,1})[\\s]+((i?)WHERE)[\\s]+(.+)");
            Matcher selectConditionMatcher = selectConditionRegex.matcher(query);
            if (selectConditionMatcher.find()) {
                if (!conditionFinder(selectConditionMatcher.group(6))) return false;
            }
        }
        return true;
    }

    public boolean executeUpdateQuery(String query) {
        boolean matched = false;
        map = new HashMap<>();
        Pattern insertRegex = Pattern.compile("((?i)INSERT)[\\s]+((?i)INTO)[\\s]+(\'{0,1}[a-zA-Z0-9_]+\'{0,1})[\\s]+((?i)VALUES)[\\s]*[(](.+)[)]");
        Pattern insertRegexWithCol = Pattern.compile("((?i)INSERT)[\\s]+((?i)INTO)[\\s]+(\'{0,1}[a-zA-Z0-9_]+\'{0,1})[\\s]*[(](.+)[)][\\s]*((?i)VALUES)[\\s]*[(](.+)[)]");
        Matcher insertMatcher = insertRegex.matcher(query);
        Matcher insertMatcherWithCol = insertRegexWithCol.matcher(query);
        boolean insertFind = insertMatcher.find();
        boolean insertWithColFind = insertMatcherWithCol.find();

        Pattern delRegex = Pattern.compile("((?i)DELETE)[\\s]+((?i)FROM)[\\s]+(\'{0,1}[a-zA-Z0-9_]+\'{0,1})");
        Pattern delRegexWithCondition = Pattern.compile("((?i)DELETE)[\\s]+((?i)FROM)[\\s]+(\'{0,1}[a-zA-Z0-9_]+\'{0,1})[\\s]+((?i)WHERE)[\\s]+([^;]+)");
        Matcher delMatcher = delRegex.matcher(query);
        Matcher delMatcherWithCondition = delRegexWithCondition.matcher(query);

        Pattern updateRegex = Pattern.compile("((?i)UPDATE)[\\s]+(\'{0,1}[a-zA-Z0-9_]+\'{0,1})[\\s]+((?i)SET)[\\s]+([^;]+)");
        Matcher updateMatcher = updateRegex.matcher(query);
        if (insertFind || insertWithColFind) {
            Matcher matcher;
            if (insertFind) matcher = insertMatcher;
            else matcher = insertMatcherWithCol;
            map.put(returnType.NAME, matcher.group(3));
            ArrayList<Object> col = new ArrayList<>();
            ArrayList<Object> val = new ArrayList<>();
            regexUpdateQuery(matcher.group(insertFind ? 5 : 6), val);
            if (val.isEmpty()) return false;
            map.put(returnType.COLVALUES, val.toArray());
            if (insertWithColFind) {
                regexUpdateQuery(insertMatcherWithCol.group(4), col);
                map.put(returnType.COLNAME, col.toArray());
            }else {
            	map.put(returnType.COLNAME, new Object[0]);
            }
            map.put(returnType.ISUPDATE, false);
            map.put(returnType.ISDELETE, false);
            map.put(returnType.ISINSERT, true);
            matched = true;
        } else if (delMatcher.find()) {
            map.put(returnType.NAME, delMatcher.group(3));
            if (delMatcherWithCondition.find()) {
                conditionFinder(delMatcherWithCondition.group(5));
            }
            map.put(returnType.ISUPDATE, false);
            map.put(returnType.ISDELETE, true);
            map.put(returnType.ISINSERT, false);
            matched = true;
        } else if (updateMatcher.find()) {
            map.put(returnType.NAME, updateMatcher.group(2));
            ArrayList<Object> column = new ArrayList<>();
            ArrayList<Object> value = new ArrayList<>();
            updateSetter(updateMatcher.group(4), column, value);
            if (column.isEmpty() || value.isEmpty()) return false;
            map.put(returnType.COLNAME, column.toArray());
            map.put(returnType.COLVALUES, value.toArray());
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

    private void regexUpdateQuery(String query, ArrayList<Object> colOrVal) {
        Pattern regex = Pattern.compile("\'{0,1}[a-zA-Z0-9_]+\'{0,1}");
        Matcher matcher = regex.matcher(query);
        while (matcher.find()) {
            colOrVal.add(matcher.group().trim());
        }
    }

    private boolean conditionFinder(String s) {
        String[] possibleOperators = {"!=", "<>", "<=", ">=", "==", "<", ">", "="};
        boolean validCondition = false;
        for (String op : possibleOperators) {
            if (s.contains(op)) {
                String[] operands = s.split(op);
                for (int i = 0; i < operands.length; i++) {
                    Matcher matcher = Pattern.compile("\'{0,1}[A-Za-z0-9_]+\'{0,1}").matcher(operands[i]);
                    if (matcher.find()) {
                        operands[i] = matcher.group();
                    }
                }
                map.put(returnType.CONDITIONOPERATOR, op);
                map.put(returnType.CONDITIONOPERANDS, operands);
                validCondition = true;
                break;
            }
        }
        return validCondition;
    }

    private void updateSetter(String s, ArrayList<Object> col, ArrayList<Object> val) {
        String[] catchCondition;
        Pattern where = Pattern.compile("(i?)WHERE");
        Matcher whereMatcher = where.matcher(s);
        int startIndex, endIndex;
        if (whereMatcher.find()) {
            startIndex = whereMatcher.start();
            endIndex = whereMatcher.end();
            catchCondition = new String[2];
            catchCondition[0] = s.substring(0, startIndex);
            catchCondition[1] = s.substring(endIndex + 1);
            conditionFinder(catchCondition[1]);
            s = catchCondition[0];
        }
        Pattern regex = Pattern.compile("\'{0,1}[a-zA-Z0-9_]+\'{0,1}");
        Matcher matcher = regex.matcher(s);
        while (matcher.find()) {
            col.add(matcher.group().trim());
            matcher.find();
            val.add(matcher.group().trim());
        }
    }
}