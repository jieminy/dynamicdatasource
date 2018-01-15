package com.discipline.thyme.dynamicdatasource;

public class Dbs {
    private static final ThreadLocal<String> local = new ThreadLocal<String>();

    public static String getDbType(){
        return local.get();
    }

    public static void setDbType(String dbName){
        local.set(dbName);
    }

    public static void clear(){
        local.remove();
    }
}
