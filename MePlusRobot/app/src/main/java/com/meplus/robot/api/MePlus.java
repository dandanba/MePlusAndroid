package com.meplus.robot.api;

import java.util.HashMap;

/**
 * Created by dandanba on 3/15/16.
 */
public class MePlus {
    public static int ErrorCodeEmptyData = 1000;
    private final static HashMap<Integer, String> sHashMap = new HashMap<>();

    static {
        sHashMap.put(ErrorCodeEmptyData, "查询后，无数据");
    }

    public static String getDescrpition(int code) {
        return sHashMap.get(code);
    }

}
