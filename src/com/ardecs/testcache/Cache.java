package com.ardecs.testcache;

public interface Cache {
    void add(String key, String value);

    Object get(String key);

    void allDelete();
}
