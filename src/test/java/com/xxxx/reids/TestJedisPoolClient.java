package com.xxxx.reids;

import com.xxxx.reids.config.RedisConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = RedisConfig.class)
public class TestJedisPoolClient {
    @Autowired
    private JedisPool jedisPool;

    private Jedis jedis = null;

    @BeforeEach
    public void initConn() {
        jedis = jedisPool.getResource();
    }

    @AfterEach
    public void closeConn() {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * 操作string
     * <p>
     * 添加或修改
     * 获取
     * 删除
     * <p>
     * 自增自减
     * <p>
     * 位操作
     * <p>
     * 到期时间
     */
    @Test
    public void testString() {
        //添加或修改一条数据
        jedis.set("username", "zhangsan");
        jedis.set("age", "18");
        //添加或修改多条数据
        jedis.mset("address", "bj", "sex", "1");
        //获取一条数据
        String username = jedis.get("username");
        System.out.println(username);
        jedis.set("username", "lisi");
        //获取多条数据
        List<String> list = jedis.mget("username", "age", "address", "sex");
        list.forEach(System.out::println);
        System.out.println("-----------------");
        //删除指定数据
        jedis.del("address", "username");
        List<String> list2 = jedis.mget("username", "age", "address", "sex");
        list2.forEach(System.out::println);
        //自增、自减
        jedis.incrBy("account::id", 100);
        System.out.println(jedis.get("account::id"));
        //位操作
        jedis.setbit("peter", 10, true);
        jedis.setbit("peter", 20, true);
        jedis.setbit("peter", 21, true);
        jedis.setbit("peter", 30, true);
        System.out.println(jedis.bitcount("peter"));
        //到期时间
        jedis.expire("peter", 300);
    }

    /**
     * 操作hash
     * <p>
     * 添加或修改
     * <p>
     * 获取（一条，多条，所有hash类型数据遍历）
     * <p>
     * 删除
     */
    @Test
    public void testHash() {
        //添加或修改一条数据
        jedis.hset("userInfo", "name", "wangwu");
        //添加或修改多条数据
        Map<String, String> map = new HashMap<>();
        map.put("age", "20");
        map.put("sex", "1");
        jedis.hmset("userInfo", map);
        //获取一条或多条数据
        System.out.println(jedis.hget("userInfo", "name"));
        List<String> list = jedis.hmget("userInfo", "age", "sex");
        list.forEach(System.out::println);
        System.out.println("----------------------------");
        //遍历所有hash类型数据
        Map<String, String> userMap = jedis.hgetAll("userInfo");
        userMap.forEach((k, v) -> {
            System.out.println("field-->" + k + ",value-->" + v);
        });
        System.out.println("+++++++++++++++++++++++++++++++");
        //删除
        jedis.hdel("userInfo", "name");
        Map<String, String> userMap2 = jedis.hgetAll("userInfo");
        for (Map.Entry<String, String> userInfo : userMap2.entrySet()) {
            System.out.println(userInfo.getKey() + "--" + userInfo.getValue());
        }
    }

    @Test
    public void testList() {

    }

}