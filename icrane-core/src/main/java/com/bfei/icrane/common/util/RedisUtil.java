package com.bfei.icrane.common.util;

import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class RedisUtil {
    private static PropFileManager propFileMgr = new PropFileManager("redis.properties");
    String redisAddr = propFileMgr.getProperty("REDISADDR");
    String redisPwd = propFileMgr.getProperty("REDISPWD");
    Integer redisPort = Integer.parseInt(propFileMgr.getProperty("REDISPORT"));
    Integer redisTimeout = Integer.parseInt(propFileMgr.getProperty("REDISOUT"));

    public void setString(String key, String value, int expire) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        jedis.set(key, value);
        jedis.expire(key, expire);
        if (jedis != null) {
            jedis.close();
        }
    }

    public void setString(String key, String value) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        jedis.set(key, value);
        if (jedis != null) {
            jedis.close();
        }
    }

    public Long getTTl(String key) {
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        Long ttl = jedis.ttl(key);
        if (jedis != null) {
            jedis.close();
        }
        return ttl;
    }

    public String getString(String key) {
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        String value = jedis.get(key);
        if (jedis != null) {
            jedis.close();
        }
        return value;
    }

    // 该命令用于在 key 存在时删除 key。
    public void delKey(String key) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        jedis.del(key);
        if (jedis != null) {
            jedis.close();
        }
    }

    // 检查给定 key 是否存在。
    public boolean existsKey(String key) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        boolean result = jedis.exists(key);
        if (jedis != null) {
            jedis.close();
        }
        return result;
    }

    // 向集合添加一个或多个成员
    public Long addSet(String key, String... member) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        Long sadd = jedis.sadd(key, member);
        if (jedis != null) {
            jedis.close();
        }
        return sadd;
    }

    // 向list集合添加一个或多个成员放在最右边
    public void addListQueue(String key, String... member) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        jedis.rpush(key, member);
        if (jedis != null) {
            jedis.close();
        }
    }

    // 获取list所有元素
    public List<String> getList(String key) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        Integer startIndex = 0;
        Integer endIndex = -1;// 获取所有
        List<String> list = jedis.lrange(key, startIndex, endIndex);
        if (jedis != null) {
            jedis.close();
        }
        return list;
    }

    // 移除list集合添加一个或多个成员
    public void remListQueue(String key, String member) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        jedis.lrem(key, 0, member);
        if (jedis != null) {
            jedis.close();
        }
    }

    // 移除集合中一个或多个成员
    public Long remSet(String key, String... member) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        Long srem = jedis.srem(key, member);
        if (jedis != null) {
            jedis.close();
        }
        return srem;
    }

    public Set<String> getSMembers(String key) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        Set<String> result = jedis.smembers(key);
        if (jedis != null) {
            jedis.close();
        }
        return result;
    }

    // 获取集合成员数
    public long getSCard(String key) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        long result = jedis.scard(key);
        if (jedis != null) {
            jedis.close();
        }
        return result;
    }

    // 判断 元素是否是集合 key 的成员
    public boolean sIsMember(String key, String member) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        boolean result = jedis.sismember(key, member);
        if (jedis != null) {
            jedis.close();
        }
        return result;
    }

    /**
     * 随机返回set中的元素
     *
     * @param key
     * @param count
     * @return
     */
    public List<String> srandMember(String key, Integer count) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        List<String> set = (jedis.srandmember(key, count));
        if (jedis != null) {
            jedis.close();
        }
        return set;
    }

    // 向hashset添加一个或多个成员
    public void addHashSet(String key, String field, String value) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        jedis.hset(key, field, value);
        if (jedis != null) {
            jedis.close();
        }
    }

    // 获取hashset成员
    public String getHashSet(String key, String field) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(redisAddr, redisPort, redisTimeout);
        jedis.auth(redisPwd);
        String value = jedis.hget(key, field);

        if (jedis != null) {
            jedis.close();
        }
        return value;
    }

}
