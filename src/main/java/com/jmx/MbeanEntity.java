package com.jmx;

public class MbeanEntity {

    String objectName = "com.javacodegeeks.snippets.enterprise:type=Hello";
    Object obj;

    public MbeanEntity(String objectName, Object obj) {
        this.objectName = objectName;
        this.obj = obj;
    }

    public String getObjectName() {
        return objectName;
    }

    public Object getObj() {
        return obj;
    }
}
