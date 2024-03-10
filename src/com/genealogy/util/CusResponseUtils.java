package com.genealogy.util;

import com.alibaba.fastjson.JSONObject;

public class CusResponseUtils {

    public static JSONObject successContent(Object content){
        JSONObject json = new JSONObject();
        json.put("result", true);
        json.put("content", content);

        return json;
    }
}
