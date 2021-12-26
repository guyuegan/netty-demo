package com.neo.codec.serialization.java;

import java.io.Serializable;

class Resp implements Serializable {
    private int id;
    private String code;
    private String msg;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Resp{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
