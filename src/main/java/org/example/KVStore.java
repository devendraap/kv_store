package org.example;

interface KVStore {
    public void set(String key, String value);

    public String get(String key);

    public void rm(String key);
}
