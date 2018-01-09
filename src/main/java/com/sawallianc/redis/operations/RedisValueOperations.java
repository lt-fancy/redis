package com.sawallianc.redis.operations;

import com.alibaba.fastjson.JSON;
import com.sawallianc.redis.AbstractBaseRedisOperations;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by fingertap on 2017/6/8.
 */
@Component
public class RedisValueOperations extends AbstractBaseRedisOperations<String,Object> {
    public RedisValueOperations(){

    }
    public boolean setIfAbsent(int dbIndex,String key,Object value,long seconds){
        return redisTemplate.execute((connection) -> {
            connection.select(dbIndex);
            connection.setNX(serializeKey(key),serializeValue(value));
            connection.expire(serializeKey(key),seconds <= 0L?DEFAULT_EXPIRE_TIME:seconds);
            return true;
        },false,false);
    }
    public boolean setIfAbsent(String key,Object value,long seconds){
        return setIfAbsent(DEFAULT_DB_INDEX,key,value,seconds);
    }
    public boolean setIfAbsent(String key,Object value){
        return setIfAbsent(DEFAULT_DB_INDEX,key,value,DEFAULT_EXPIRE_TIME);
    }
    public void set(int dbIndex,String key,Object value,long seconds){
        redisTemplate.execute((connection) ->{
            connection.select(dbIndex);
            connection.setEx(serializeKey(key),seconds <= 0L?DEFAULT_EXPIRE_TIME:seconds,serializeValue(value));
            return Boolean.valueOf(true);
        },false,false);
    }
    public void set(String key,Object value,long seconds){
        set(DEFAULT_DB_INDEX,key,value,seconds);
    }
    public void set(String key,Object value){
        set(DEFAULT_DB_INDEX,key,value,DEFAULT_EXPIRE_TIME);
    }
    public String getAndSet(int dbIndex,String key,Object value){
        return redisTemplate.execute((connection) -> {
            connection.select(dbIndex);
            return deserializeValue(connection.getSet(serializeKey(key),serializeValue(value)));
        },false,false);
    }
    public String getAndSet(String key,Object value){
        return getAndSet(0,key,value);
    }
    public String get(int dbIndex,String key){
        return redisTemplate.execute((connection) -> {
            connection.select(dbIndex);
            return deserializeValue(connection.get(serializeKey(key)));
        },false,false);
    }
    public String get(String key){
        return get(DEFAULT_DB_INDEX,key);
    }
    public <T> T get(String key,Class<T> clazz){
        return JSON.parseObject(get(key),clazz);
    }
    public long increase(int dbIndex,String key){
        return redisTemplate.execute((connection) -> {
            connection.select(dbIndex);
            return connection.incr(serializeKey(key));
        },false,false);
    }

    public <T> List<T> getArray(String key,Class<T> clazz){
        return JSON.parseArray(this.get(key),clazz);
    }

    public long increase(String key){
        return increase(DEFAULT_DB_INDEX,key);
    }
    public long decrease(int dbIndex,String key){
        return redisTemplate.execute((connection) -> {
            connection.select(dbIndex);
            return connection.decr(serializeKey(key));
        },false,false);
    }
    public long decrease(String key){
        return decrease(DEFAULT_DB_INDEX,key);
    }
    public long increaseBy(int dbIndex,String key,long delta){
        return redisTemplate.execute((connection) -> {
            connection.select(dbIndex);
            return connection.incrBy(serializeKey(key),delta);
        },false,false);
    }
    public long increaseBy(String key,long delta){
        return increaseBy(DEFAULT_DB_INDEX,key,delta);
    }
    public long decreaseBy(int dbIndex,String key,long delta){
        return redisTemplate.execute((connection) -> {
            connection.select(dbIndex);
            return connection.decrBy(serializeKey(key),delta);
        },false,false);
    }
    public long decreaseBy(String key,long delta){
        return decreaseBy(DEFAULT_DB_INDEX,key,delta);
    }
}
