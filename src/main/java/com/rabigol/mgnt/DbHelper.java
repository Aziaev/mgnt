package com.rabigol.mgnt;

/**
 * Statements for database connectivity
 */

class DbHelper {
    static String dropAndCreateTable = "DROP TABLE IF EXISTS TEST;" +
            "CREATE TABLE TEST (" +
            "  FIELD integer" +
            ");";

    static String addFieldsToDb(int n){
        StringBuilder addFields = new StringBuilder("INSERT INTO test (field) VALUES ");
        for (int i = 0; i < n; i++) {
            double a = Math.random() * n;
            int b = (int) a;
            addFields.append("(" + b + "),");
        }
        addFields.deleteCharAt(addFields.length() - 1);
        addFields.append(";");
        return addFields.toString();
    }

    static String getFields = "SELECT * FROM test";
}
