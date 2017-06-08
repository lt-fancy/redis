package com.sawallianc.redis.operations;

import com.google.common.collect.Lists;
import com.sawallianc.redis.AbstractBaseRedisOperations;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by fingertap on 2017/6/8.
 */
@Component
@Scope("prototype")
public class RedisListOperations extends AbstractBaseRedisOperations<String,Object> {
    public RedisListOperations(){
        super();
    }
    public RedisListOperations selectDB(int dbIndex){
        super.dbIndex.set(dbIndex);
        return this;
    }

    /**
     * 添加多个值
     * @param key
     * @param values
     * @param seconds
     */
    public void add(final String key, final List values, final long seconds){
        redisTemplate.executePipelined(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException{
                connection.select(dbIndex.get());
                final byte[] rawKey = serializeKey(key);
                for(Object object : values){
                    connection.rPush(rawKey,serializeValue(object));
                }
                if(seconds > 0){
                    connection.expire(rawKey,seconds);
                }
                return null;
            }
        });
    }

    /**
     * 压栈
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public Long push(final String key,final Object value,final long seconds){
        return redisTemplate.execute((connection) -> {
            connection.select(dbIndex.get());
            final byte[] rawKey = serializeKey(key);
            Long result = connection.lPush(rawKey,serializeValue(value));
            if(seconds > 0L){
                connection.expire(rawKey,seconds);
            }
            return result;
        },false,false);
    }

    /**
     * 压栈，当且仅当KEY存在并且是一个列表
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public Long pushIfAbs(final String key,final Object value,final long seconds){
        return redisTemplate.execute((connection) -> {
            connection.select(dbIndex.get());
            final byte[] rawKey = serializeKey(key);
            Long result = connection.lPushX(rawKey,serializeValue(value));
            if(seconds > 0L){
                connection.expire(rawKey,seconds);
            }
            return result;
        },false,false);
    }

    /**
     * 出栈
     * @param key
     * @return
     */
    public Object popObject(final String key){
        return redisTemplate.execute((connection) -> {
            connection.select(dbIndex.get());
            return deserializeValue(connection.lPop(key.getBytes()));
        },false,false);
    }

    /**
     * 出栈
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T pop(final String key,final Class<T> clazz){
        return clazz.cast(popObject(key));
    }

    /**
     * 入队
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public Long in(final String key,final Object value,final long seconds){
        return redisTemplate.execute((connection) -> {
            connection.select(dbIndex.get());
            final byte[] rawKey = serializeKey(key);
            Long result = connection.rPush(rawKey,serializeValue(value));
            if(seconds > 0L){
                connection.expire(rawKey,seconds);
            }
            return result;
        },false,false);
    }

    /**
     * 入队，当且仅当KEY存在并且是一个列表
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public Long inIfAbs(final String key,final Object value,final long seconds){
        return redisTemplate.execute((connection) -> {
            connection.select(dbIndex.get());
            final byte[] rawKey = serializeKey(key);
            Long result = connection.rPushX(rawKey,serializeValue(value));
            if(seconds > 0L){
                connection.expire(rawKey,seconds);
            }
            return result;
        },false,false);
    }

    /**
     * 出队
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T out(final String key,Class<T> clazz){
        return pop(key,clazz);
    }

    /**
     * 栈/队列长
     * @param key
     * @return
     */
    public int size(final String key){
        return redisTemplate.execute((connection) -> {
            connection.select(dbIndex.get());
            return connection.lLen(key.getBytes()).intValue();
        },false,false);
    }

    /**
     * 范围检索
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<Object> range(final String key,final int start,final int end){
        return redisTemplate.execute((connection) -> {
            connection.select(dbIndex.get());
            List<byte[]> resultBytes = connection.lRange(key.getBytes(),start,end);
            List<Object> result = Lists.newArrayList();
            for(byte[] bs : resultBytes){
                result.add(deserializeValue(bs));
            }
            return result;
        },false,false);
    }

    /**
     * 移除
     * @param key
     * @param count
     * @param value
     * @return
     */
    public Long remove(final String key,final long count,final Object value){
        return redisTemplate.execute((connection) -> {
            connection.select(dbIndex.get());
            return connection.lRem(serializeKey(key),count,serializeValue(value));
        },false,false);
    }

    /**
     * 检索
     * @param key
     * @param index
     * @return
     */
    public Object indexObject(final String key,final int index){
        return redisTemplate.execute((connection) -> {
            connection.select(dbIndex.get());
            return deserializeValue(connection.lIndex(key.getBytes(),index));
        },false,false);
    }

    /**
     * 检索
     * @param key
     * @param index
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T index(final String key,final int index,Class<T> clazz){
        return clazz.cast(indexObject(key,index));
    }

    /**
     * 置值
     * @param key
     * @param index
     * @param value
     * @param seconds
     */
    public void set(final String key,final int index,final Object value,final long seconds){
        redisTemplate.execute((connection) -> {
            connection.select(dbIndex.get());
            final byte[] rawKey = serializeKey(key);
            connection.lSet(rawKey,index,serializeValue(value));
            if(seconds > 0L){
                connection.expire(rawKey,seconds);
            }
            return null;
        },false,false);
    }
}
