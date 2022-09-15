package com.xxxx.reids;

import com.xxxx.reids.entity.User;
import com.xxxx.reids.util.SerializeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.params.SetParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
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
        userMap.forEach((k, v) -> System.out.println("field-->" + k + ",value-->" + v));
        System.out.println("+++++++++++++++++++++++++++++++");
        //删除
        jedis.hdel("userInfo", "name");
        Map<String, String> userMap2 = jedis.hgetAll("userInfo");
        for (Map.Entry<String, String> userInfo : userMap2.entrySet()) {
            System.out.println(userInfo.getKey() + "--" + userInfo.getValue());
        }
    }

    /**
     * 操作List
     * <p>
     * 入队（左右添加）lpush rpush
     * <p>
     * 出队 (左右)lpop rpop
     * <p>
     * 遍历lrange
     * <p>
     * 获取lindex
     * <p>
     * 修改lset
     * <p>
     * 获取总条数llen
     * <p>
     * 删除list里面的单条 n个 lrem
     * <p>
     * 删除list   del
     */
    @Test
    public void testList() {
        //左添加
        jedis.lpush("student", "zhangsan", "lisi", "wangwu");
        //遍历
        jedis.lrange("student", 0, -1).forEach(System.out::println);
        System.out.println("--------------------------------");
        //右添加
        jedis.rpush("student", "zhaoliu", "qianqi", "sunjiu");
        //遍历
        List<String> student = jedis.lrange("student", 0, -1);
        for (String stu : student) {
            System.out.println(stu);
        }
        System.out.println("--------------------------------");
        //获取总条数
        System.out.println(jedis.llen("student"));
        //删除单条
        jedis.lrem("student", 1, "zhangsan");
        jedis.lrange("student", 0, -1).forEach(System.out::println);
        //出队
        System.out.println(jedis.rpop("student"));
        System.out.println(jedis.lpop("student"));
        //获取某条
        System.out.println(jedis.lindex("student", 1));
        System.out.println(jedis.lset("student", 1, "zhoushi"));
        System.out.println(jedis.lindex("student", 1));
        //删除列表
        jedis.del("student");
    }

    /**
     * 操作set
     * <p>
     * 添加
     * <p>
     * 获取
     * <p>
     * 获取总条数
     * <p>
     * 删除
     */
    @Test
    public void testSet() {
        //添加数据
        jedis.sadd("letters", "aaa", "bbb", "ccc", "ddd", "eee");
        //获取数据
        jedis.smembers("letters").forEach(System.out::println);
        //获取总条数
        System.out.println(jedis.scard("letters"));
        //删除数据
        jedis.srem("letters", "aaa", "bbb");
        jedis.smembers("letters").forEach(System.out::println);

    }

    /**
     * 操作Sorted Set
     * <p>
     * 准备Map数据
     * <p>
     * 添加
     * <p>
     * 获取
     * <p>
     * 获取总条数
     * <p>
     * 删除
     */
    @Test
    public void testSortedSet() {
        //准备添加数据
        Map<String, Double> scoreMembers = new HashMap<>();
        scoreMembers.put("zhangsan", 7D);
        scoreMembers.put("lisi", 3D);
        scoreMembers.put("wangwu", 5D);
        scoreMembers.put("zhaoliu", 6D);
        scoreMembers.put("tianqi", 2D);
        //添加数据
        jedis.zadd("score", scoreMembers);
        //获取数据
        jedis.zrange("score", 0, 4).forEach(System.out::println);
        //获取数量
        System.out.println(jedis.zcard("score"));
        //删除数据
        jedis.zrem("score", "zhangsan", "lisi");
        jedis.zrange("score", 0, 4).forEach(System.out::println);
    }

    /**
     * Redis中以层级关系、目录形式存储数据
     */
    @Test
    public void testDir() {
        jedis.set("user:01", "user_zhangsan");
        System.out.println(jedis.get("user:01"));
    }

    /**
     * 设置key的失效时间
     */
    @Test
    public void testExpire() {
        //方法一：
        jedis.set("code", "test");
        jedis.expire("code", 180);
        jedis.pexpire("code", 180000L);
        System.out.println(jedis.ttl("code"));//获取秒
        //方法二：
        jedis.setex("code", 180, "test");
        jedis.psetex("code", 180000L, "test");
        System.out.println(jedis.pttl("code"));//获取毫秒
        // 方法三：
        SetParams setParams = new SetParams();
        //不存在的时候才能设置成功
        // setParams.nx();
        // 存在的时候才能设置成功
        setParams.xx();
        //设置失效时间，单位秒
        // setParams.ex(30);
        //查看失效时间，单位毫秒
        setParams.px(30000);
        jedis.set("code", "test", setParams);
    }

    /**
     * 获取所有key
     */
    @Test
    public void testAllKeys() {
        //获取当前key的数量
        System.out.println(jedis.dbSize());
        //获取当前key的名称
        jedis.keys("*").forEach(System.out::println);
    }

    /**
     * 操作事务
     */
    @Test
    public void testMulti() {
        Transaction tx = jedis.multi();
        //开启事务
        tx.set("tel", "10010");
        //提交事务
//        tx.exec();
        //回滚事务
        tx.discard();
    }

    /**
     * 删除
     */
    @Test
    public void testDelete() {
        // 删除 通用 适用于所有数据类型
        jedis.del("score");
    }

    /**
     * 操作byte
     */
    @Test
    public void testByte() {
        User user = new User();
        user.setId(2);
        user.setUsername("zhangsan");
        user.setPassword("123");
        //序列化
        byte[] userKey = SerializeUtil.serialize("user:" + user.getId());
        byte[] userValue = SerializeUtil.serialize(user);
        jedis.set(userKey,userValue);
        //获取数据
        byte[] userResult = jedis.get(userKey);
        //反序列化
        User u = (User) SerializeUtil.unserialize(userResult);
        System.out.println(u);
    }
}