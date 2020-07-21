package com.orchidblessing.library.domain;

public class PreparedParamDomain {
    private Object value;
    private Class clazz;

    public PreparedParamDomain(Object value, Class clazz) {
        this.value = value;
        this.clazz = clazz;
    }

    public Object getValue() {
        return value;
    }

    public Class getClazz() {
        return clazz;
    }
}
