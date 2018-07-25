package com.bfei.icrane.common.util;

import com.bfei.icrane.core.models.Doll;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by moying on 2018/7/24.
 */
public class DollRedisUtil {

    RedisUtil redisUtil = new RedisUtil();

    public List<Doll> getDollList(String key) {
        Gson gson = new Gson();
        String string = redisUtil.getString(key);
        Type type1 = new TypeToken<List<Doll>>() {
        }.getType();
        return gson.fromJson(string, type1);
    }

    public void setDollList(String key, List<Doll> dollList, int time) {
        Gson gson = new Gson();
        redisUtil.setString(key, gson.toJson(dollList),time);
    }

    public Long getTTl(String key) {
        return redisUtil.getTTl(key);
    }
}
