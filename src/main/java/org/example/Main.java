package org.example;

import com.alibaba.fastjson.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


class Main {
    public static Boolean ops(LSMKVStore store) {
        for (Integer i = 0; i < 4096; i++) {
            store.set(i.toString(), i.toString());
        }

        store.set("i", "i");
        store.set("j", "j");

        for (Integer i = 0; i < 2000; i++) {
            System.out.println(i.toString() + " : " + store.get(i.toString()));
        }
        for (Integer i = 0; i < 2000; i++) {
            store.rm(i.toString());
        }

        for (Integer i = 2000; i < 2500; i++) {
            store.set(i.toString(), i.toString());
        }
        return true;
    }

    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {
        LSMKVStore store = new LSMKVStore("/tmp/kvstore/", 4096);
        ExecutorService es = Executors.newCachedThreadPool();

        for (int i = 0; i < 3; i++) {
            Future<Boolean> res = es.submit(() -> Main.ops(store));
            res.get();
        }

    }
}
