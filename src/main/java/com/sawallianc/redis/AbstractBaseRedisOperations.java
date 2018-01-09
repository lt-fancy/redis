package com.sawallianc.redis;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.List;
import java.util.Set;

/**
 *
 * Created by fingertap on 2017/6/8.
 */
public abstract class AbstractBaseRedisOperations<K,V> {
    //一天的过期时间
    protected static final long DEFAULT_EXPIRE_TIME = 60 * 60 * 24;
    protected static final int DEFAULT_DB_INDEX = 0;
    protected ThreadLocal<Integer> dbIndex = new ThreadLocal<Integer>();
    @Autowired
    public RedisTemplate<K,V> redisTemplate;

    /**
     * 删除指定key的存储信息
     * @param dbIndex
     * @param key
     * @return
     */
    public boolean delete(final int dbIndex, final String key){
        Long result = this.redisTemplate.execute((connection) -> {
            connection.select(dbIndex);
            return connection.del(new byte[][]{this.serializeKey(key)});
        },false,false);
        return result != null && result >= 1L;
    }

    public boolean delete(String key){
        return delete(DEFAULT_DB_INDEX,key);
    }
    public boolean exists(int dbIndex,String key){
        return this.redisTemplate.execute((connection) -> {
            connection.select(dbIndex);
            return connection.exists(this.serializeKey(key));
        },false,false);
    }

    public boolean exists(String key){
        return exists(DEFAULT_DB_INDEX,key);
    }

    public List<String> keys(String pattern){
        return this.redisTemplate.execute((connection) -> {
            connection.select(DEFAULT_DB_INDEX);
            Set<byte[]> set = connection.keys(pattern.getBytes());
            List<String> keysList = Lists.newArrayList();
            for(byte[] bs : set){
                keysList.add(new String(bs));
            }
            return keysList;
        },false,false);
    }

    public boolean flushAll(){
        return this.redisTemplate.execute((connection) -> {
            connection.select(DEFAULT_DB_INDEX);
            connection.flushAll();
            return true;
        },false,false);
    }

    public Long ttl(int dbIndex,String key){
        return this.redisTemplate.execute((connection) -> {
            connection.select(dbIndex);
            return connection.ttl(this.serializeKey(key));
        },false,false);
    }

    public Long ttl(String key){
        return ttl(DEFAULT_DB_INDEX,key);
    }

    protected byte[] serializeKey(String key){
        return key.getBytes();
    }
    protected byte[] serializeValue(Object value){
        if(value instanceof String){
           return ((String) value).getBytes();
        } else {
           return ((RedisSerializer<String>) this.redisTemplate.getValueSerializer()).serialize(JSON.toJSONString(value));
        }

    }
    protected String deserializeValue(byte[] value){
        if(value == null){
            return null;
        }
        return ((RedisSerializer<String>) this.redisTemplate.getValueSerializer()).deserialize(value);
    }
}
