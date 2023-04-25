package org.example;

import java.io.FileNotFoundException;

class LSMKVStoreTest {

    @org.junit.jupiter.api.Test
    void get() throws FileNotFoundException {
        LSMKVStore store = new LSMKVStore("/tmp/kvstoreget", 1);
        store.set("1", "1");
        assert store.get("1") == "1";
    }

    @org.junit.jupiter.api.Test
    void rm() throws FileNotFoundException {
        LSMKVStore store = new LSMKVStore("/tmp/kvstorerm", 1);
        store.set("1", "1");
        store.rm("1");
        assert store.get("1") == null;
    }
}