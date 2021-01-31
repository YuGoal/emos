package io.caoyu.emos.wx.common.util;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class R extends HashMap<String, Object> {

    public R() {
        put("code", HttpStatus.SC_OK);
        put("msg", "success");
    }

    public static R error() {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }

    private static R error(int scInternalServerError, String s) {
        R r = new R();
        r.put("code", scInternalServerError);
        r.put("msg", s);
        return r;
    }

    public static R ok(String s) {
        R r = new R();
        r.put("msg", s);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }
    public static R ok() {
        return new R();
    }
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
