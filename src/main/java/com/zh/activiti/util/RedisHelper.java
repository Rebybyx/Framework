package com.zh.activiti.util;


import redis.clients.jedis.Jedis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class RedisHelper extends RedisProvider {


    public static String set(String key, String value) {
        Jedis jedis = null;
        String rtn = null;
        try {
            jedis = getJedis();
            rtn = jedis.setex(key, EXPIRE, value);
        } catch (Exception e) {
            e.printStackTrace();
            jedispool.returnBrokenResource(jedis);
        } finally {
            returnResource(jedispool, jedis);
        }
        return rtn;
    }

    public static void addObject(String key, Object obj) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.set(key.getBytes(), ObjectToByte(obj));
          //  jedis.expire(key.getBytes(), EXPIRE);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(ExceptionUtil.getTrace(e));
            jedispool.returnBrokenResource(jedis);
        } finally {
            returnResource(jedispool, jedis);
        }
    }

    public static void addString(String key, String  str) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.set(key, str);
            //  jedis.expire(key.getBytes(), EXPIRE);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(ExceptionUtil.getTrace(e));
            jedispool.returnBrokenResource(jedis);
        } finally {
            returnResource(jedispool, jedis);
        }
    }

    public static void addList(String key, List list) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            for (Object obj:list) {
                jedis.sadd(key.getBytes(), ObjectToByte(obj));
            }
          //  jedis.expire(key.getBytes(), EXPIRE);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(ExceptionUtil.getTrace(e));
            jedispool.returnBrokenResource(jedis);
        } finally {
            returnResource(jedispool, jedis);
        }
    }

    public static List<Object> getListObject(String key) {
        List<Object> list = new ArrayList<>();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            Set<byte[]> set = jedis.smembers(key.getBytes());
            if (set != null && !set.isEmpty()) {
                Iterator<byte[]> it = set.iterator();
                for (; it.hasNext(); ) {
                    byte[] b = it.next();
                    Object obj = ByteToObject(b);
                    list.add(obj);
                }
            }
        } catch (Exception e) {
            LOG.error(ExceptionUtil.getTrace(e));
            jedispool.returnBrokenResource(jedis);
        } finally {
            returnResource(jedispool, jedis);
        }
        return list;
    }

    public static Object getObject(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            byte[] bytes = jedis.get(key.getBytes());
            if (bytes != null ) {
                Object obj = ByteToObject(bytes);
                return obj;
            }
        } catch (Exception e) {
            LOG.error(ExceptionUtil.getTrace(e));
            jedispool.returnBrokenResource(jedis);
        } finally {
            returnResource(jedispool, jedis);
        }
        return null;
    }

    public static String getString(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String string = jedis.get(key);
            return string;
        } catch (Exception e) {
            LOG.error(ExceptionUtil.getTrace(e));
            jedispool.returnBrokenResource(jedis);
        } finally {
            returnResource(jedispool, jedis);
        }
        return null;
    }

    public static void delAllObject(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.del(key.getBytes());
        } catch (Exception e) {
            LOG.error(ExceptionUtil.getTrace(e));
            jedispool.returnBrokenResource(jedis);
        } finally {
            returnResource(jedispool, jedis);
        }
    }

    /**
     * 设置分布式锁
     *
     * @param key
     * @param value
     * @return
     */
    public static long setLock(String key, String value) {
        Jedis jedis = null;
        Long rtn = null;
        try {
            jedis = getJedis();
            rtn = jedis.setnx(key, value);
            jedis.expire(key, EXPIRE);
        } catch (Exception e) {
            LOG.error(ExceptionUtil.getTrace(e));
            jedispool.returnBrokenResource(jedis);
        } finally {
            returnResource(jedispool, jedis);
        }
        return rtn;
    }

    public static Set<String> getKeys(String pattern) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.keys(pattern);
        } catch (Exception e) {
            LOG.error(ExceptionUtil.getTrace(e));
            jedispool.returnBrokenResource(jedis);
        } finally {
            returnResource(jedispool, jedis);
        }
        return new HashSet();
    }

    /**
     * 释放锁
     *
     * @param key
     * @return
     */
    public static long delLock(String key) {
        Jedis jedis = null;
        Long rtn = null;
        try {
            jedis = getJedis();
            rtn = jedis.del(key);
        } catch (Exception e) {
            LOG.error(ExceptionUtil.getTrace(e));
            jedispool.returnBrokenResource(jedis);
        } finally {
            returnResource(jedispool, jedis);
        }
        return rtn;
    }

    public static void main(String args[]) {

        //  ScreenReceiveData temp = new ScreenReceiveData();
        //set("123","666666660");
        //setLock("123","666666660");
        List<Object> tttt = getListObject("DP006");
        set("123", "66666666677777");
      /*  addObject("465", temp);

        List<ScreenReceiveData> strs = (List<ScreenReceiveData>) (List) getAllObject("465");
        for (ScreenReceiveData tt : strs) {
            System.out.println(tt.getPage());
        }*/

    }


    public static Object ByteToObject(byte[] bytes) {
        Object obj = null;
        try {
            // bytearray to object
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);

            obj = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return obj;
    }

    public static byte[] ObjectToByte(Object obj) {
        byte[] bytes = null;
        try {
            // object to bytearray
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);

            bytes = bo.toByteArray();

            bo.close();
            oo.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return bytes;
    }
}
