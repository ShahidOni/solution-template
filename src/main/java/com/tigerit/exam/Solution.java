package com.tigerit.exam;

/**
 * All of your application logic should be placed inside this class.
 * Remember we will load your application from our custom container.
 * You may add private method inside this class but, make sure your
 * application's execution points start from inside run method.
 */
import java.util.*;
public class Solution implements Runnable {
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        Map<String, Table> tables = new HashMap<>();
        int tc = scanner.nextInt();
        int nT = 0;
        for (int i = 0; i < tc; i++) {
            nT = scanner.nextInt();
            for (int x = 0; x < nT; x++) {
                String tableName = scanner.next();
                scanner.nextLine();
                tables.put(tableName, new Table(tableName));

                int nR = scanner.nextInt();
                int nC = scanner.nextInt();

                Table table = tables.get(tableName);
                List<String> columnNames = new ArrayList<>();
                Map<String, Integer> columnIndexMapping = new HashMap<>();
                for (int t = 0; t < nC; t++) {
                    String columnName = scanner.next();
                    columnNames.add(columnName);
                    columnIndexMapping.put(columnName, t);
                }
                table.setColumnNames(columnNames);
                table.setColumnIndexMapping(columnIndexMapping);
                scanner.nextLine();
                int data[][] = new int[nR][nC];
                for (int j = 0; j < nR; j++) {
                    for (int k = 0; k < nC; k++) {
                        int value = scanner.nextInt();
                        data[j][k] = value;
                    }
                }
                table.setData(data);
                table.setNumberOfColumn(nC);
                table.setNumberOfRows(nR);
            }

            int queryTestCase = scanner.nextInt();
            scanner.nextLine();
            List<String> query = new ArrayList<>();
            for (int l = 0; l < queryTestCase; l++) {
                for (int j = 0; j < 4; j++) {
                    String queryLine = scanner.nextLine();
                    query.add(queryLine);
                }
                scanner.nextLine();
                if (l == 0) {
                    System.out.println("Test: " + (i + 1));
                }
                QueryProcessor queryProcessor = new QueryProcessor(query, tables);
                queryProcessor.processQuery();
                System.out.println();
                query.clear();
            }
        }
    }
}

class QueryProcessor {
    private List<String> query;
    private Map<String, String> aliasMap;
    private Map<String, Table> tables;

    QueryProcessor(List<String> query, Map<String, Table> tables) {
        this.query = query;
        aliasMap = new HashMap<>();
        this.tables = tables;
    }

    void processQuery() {
        String secondLineOfQuery = query.get(1);
        String[] splitTextOfSecondLine = secondLineOfQuery.split(" ");
        if (splitTextOfSecondLine.length == 3) {
            aliasMap.put(splitTextOfSecondLine[2], splitTextOfSecondLine[1]);
        }
        String thirdLineOfQuery = query.get(2);
        String[] splitTextOfThirdLine = thirdLineOfQuery.split(" ");
        if (splitTextOfThirdLine.length == 3) {
            aliasMap.put(splitTextOfThirdLine[2], splitTextOfThirdLine[1]);
        }
        doOperation();
    }

    private void doOperation() {
        String fourthLineOfQuery = query.get(3);
        String[] splittedText = fourthLineOfQuery.split(" ");

        String tableName1 = splittedText[1].split("\\.")[0];
        String firstColumnName = splittedText[1].split("\\.")[1];

        String tableName2 = splittedText[3].split("\\.")[0];
        String secondColumnName = splittedText[3].split("\\.")[1];

        Table table1, table2;

        table1 = tables.get(tableName1);
        table2 = tables.get(tableName2);

        if (table1 == null) {
            table1 = tables.get(aliasMap.get(tableName1));
        }
        if (table2 == null) {
            table2 = tables.get(aliasMap.get(tableName2));
        }
        List<Integer> qualifiedColumnTable1 = new ArrayList<>();
        List<Integer> qualifiedColumnTable2 = new ArrayList<>();
        printColumn(table1, table2, qualifiedColumnTable1, qualifiedColumnTable2);
        for (int i = 0; i < table1.getNumberOfRows(); i++) {
            for (int j = 0; j < table2.getNumberOfRows(); j++) {
                int columnIndex1 = table1.getColumnIndexMapping().get(firstColumnName);
                int columnIndex2 = table2.getColumnIndexMapping().get(secondColumnName);
                if (table1.getData()[i][columnIndex1] == table2.getData()[j][columnIndex2]) {
                    for (int k = 0; k < table1.getNumberOfColumn(); k++) {
                        if (qualifiedColumnTable1.contains(k)) {
                            System.out.print(table1.getData()[i][k] + " ");
                        }
                    }
                    for (int k = 0; k < table2.getNumberOfColumn(); k++) {
                        if (qualifiedColumnTable2.contains(k)) {
                            System.out.print(table2.getData()[j][k] + " ");
                        }
                    }
                    System.out.println();
                }
            }
        }
    }


    private void printColumn(Table table1, Table table2, List<Integer> qualifiedColumnTable1, List<Integer> qualifiedColumnTable2) {
        String firstLine = query.get(0);
        String[] tokens = firstLine.split(" ");
        if (tokens[1].equals("*")) {
            for (int i = 0; i < table1.getNumberOfColumn(); i++) {
                System.out.print(table1.getColumnNames().get(i) + " ");
                qualifiedColumnTable1.add(i);
            }
            for (int i = 0; i < table2.getNumberOfColumn(); i++) {
                System.out.print(table2.getColumnNames().get(i) + " ");
                qualifiedColumnTable2.add(i);
            }
            System.out.println();
        } else {
            String firstLineWithoutSelect = firstLine.replace("SELECT ", "");
            String[] splitWithComma = firstLineWithoutSelect.split(", ");
            for (int i = 0; i < splitWithComma.length; i++) {
                Table t = null;
                String tableName = aliasMap.get(splitWithComma[i].split("\\.")[0]);
                String columnName = splitWithComma[i].split("\\.")[1];
                if (tableName == null) {
                    tableName = splitWithComma[i].split("\\.")[0];
                }
                t = tables.get(tableName);
                if (t.getTableName().equals(table1.getTableName())) {
                    populateQualifiedColumn(t, qualifiedColumnTable1, columnName);
                } else {
                    populateQualifiedColumn(table2, qualifiedColumnTable2, columnName);
                }
                System.out.print(columnName + " ");
            }
            System.out.println();
        }
    }

    private void populateQualifiedColumn(Table table, List<Integer> qualifiedColumnTable, String qualifiedColumn) {
        for (int i = 0; i < table.getNumberOfColumn(); i++) {
            if (table.getColumnNames().get(i).equals(qualifiedColumn)) {
                qualifiedColumnTable.add(i);
            }
        }
    }

}

class Table {
    private int[][] data;
    private List<String> columnNames;
    private Map<String, Integer> columnIndexMapping;
    private int numberOfRows;
    private int numberOfColumn;
    private String tableName;

    Table(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int[][] getData() {
        return data;
    }

    public void setData(int[][] data) {
        this.data = data;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public Map<String, Integer> getColumnIndexMapping() {
        return columnIndexMapping;
    }

    public void setColumnIndexMapping(Map<String, Integer> columnIndexMapping) {
        this.columnIndexMapping = columnIndexMapping;
    }

    public int getNumberOfColumn() {
        return numberOfColumn;
    }

    public void setNumberOfColumn(int numberOfColumn) {
        this.numberOfColumn = numberOfColumn;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }
}

