package org.example;

import com.alibaba.fastjson.annotation.JSONField;


class KeyValue {

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @JSONField(name = "key")
    private String key;
    @JSONField(name = "value")
    private String value;

    public KeyValue(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }
}
