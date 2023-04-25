package org.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.TreeMap;

class Index implements Closeable {
    private String walFilePath, stgWalFilePath;
    private TreeMap<String, KeyValue> index, stgIndex;
    private RandomAccessFile walFileHandle;


    Index(String path) throws FileNotFoundException {
        walFilePath = path + FileName.WAL.name();
        stgWalFilePath = path + FileName.STG_WAL.name();
        index = generateIndex(walFilePath);
        stgIndex = generateIndex(stgWalFilePath);
        walFileHandle = new RandomAccessFile(walFilePath, "rw");
    }

    public String getWalFilePath() {
        return walFilePath;
    }

    public String getStgWalFilePath() {
        return stgWalFilePath;
    }

    public TreeMap<String, KeyValue> generateIndex(String path) throws RuntimeException {
        TreeMap<String, KeyValue> index = new TreeMap<>();
        try {
            if (new File(path).exists() && new File(path).length() != 0) {
                RandomAccessFile file = new RandomAccessFile(path, "rw");
                String line;
                while ((line = file.readLine()) != null) {
                    KeyValue cmd = JSON.parseObject(line, KeyValue.class);
                    index.put(cmd.getKey(), cmd);
                }
                file.close();
            } else {
                new File(path).createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return index;
    }

    public void write(KeyValue kv) {
        try {
            walFileHandle.writeBytes(JSONObject.toJSONString(kv) + "\n");
            index.put(kv.getKey(), kv);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean switchIndexIfExceeds(int indexSize) throws IOException {

        if (index.size() > indexSize) {
            File stgWal = new File(stgWalFilePath);
            if (stgWal.exists()) stgWal.delete();

            new File(walFilePath).renameTo(stgWal);
            walFileHandle.close();

            File file = new File(walFilePath);
            walFileHandle = new RandomAccessFile(file, "rw");

            stgIndex = index;
            index = new TreeMap<>();
            return true;
        }
        return false;
    }

    public KeyValue get(String key) {
        if (index.containsKey(key)) return index.get(key);
        return stgIndex.get(key);
    }

    @Override
    public void close() throws IOException {
        walFileHandle.close();
    }
}
