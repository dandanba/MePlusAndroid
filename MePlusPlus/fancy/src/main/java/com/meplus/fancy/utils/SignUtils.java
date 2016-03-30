package com.meplus.fancy.utils;

import com.meplus.fancy.Constants;

import java.util.TreeMap;

import cn.trinea.android.common.util.DigestUtils;

/**
 * Created by dandanba on 3/2/16.
 */
public class SignUtils {
    /**
     * @param treeMap
     * @param time    1970-1-1至今的秒数
     * @return
     */
    public static String sign(TreeMap<String, String> treeMap, String time) {
        final String key = Constants.KEY;
        treeMap.put("time", time);
        final String json = JsonUtils.writeValueAsString(treeMap);
        final String md5 = DigestUtils.md5(key.concat(json));
        final String sign = DigestUtils.md5(md5.concat(key));
        return sign;
    }
}
