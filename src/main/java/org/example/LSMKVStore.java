package org.example;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;


class LSMKVStore implements KVStore {
    Logger logger = Logger.getLogger(this.getClass().getName());
    LinkedList<SSTable> ssTables = new LinkedList<>();
    ReadWriteLock lock = new ReentrantReadWriteLock();

    Index index;

    int indexSize;

    LSMKVStore(String path, int indexSize) throws FileNotFoundException {
        logger.info("Creating...");
        index = new Index(path);
        this.indexSize = indexSize;
    }

    public String get(String key) {
        lock.readLock().lock();
        String value = index.get(key).getValue();
        lock.readLock().unlock();
        return value;
    }

    public void set(String key, String value) {
        this.put(key, value);
    }

    public void rm(String key) {
        this.put(key, null);
    }


    private void put(String key, String value) {
        KeyValue kv = new KeyValue(key, value);
        try {
            lock.writeLock().lock();
            index.write(kv);
            if (index.switchIndexIfExceeds(indexSize)) {
                // Save to SSTable
                index.getStgWalFilePath();
            }
        } catch (Exception ex) {
            lock.writeLock().unlock();
        } finally {
            lock.writeLock().unlock();
        }
    }

}
