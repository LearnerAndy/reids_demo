package com.xxxx.reids;

//import org.junit.Test;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class TestJedisClient {

    @Test
    public void initConn01() {
        Jedis jedis = new Jedis("192.168.110.222", 6389);
        jedis.auth("123456");
        jedis.select(1);
        System.out.println(jedis.ping());

        jedis.set("username", "zhangsan");

        System.out.println(jedis.get("username"));

        if (jedis != null) {
            jedis.close();
        }
    }

    @Test
    public void initConn02() {
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "192.168.110.222", 6389, 10000, "123456");
        Jedis jedis = jedisPool.getResource();
        jedis.select(2);
        System.out.println(jedis.ping());
        jedis.set("username", "zhangsan");
        System.out.println(jedis.get("username"));
        if (jedis != null){
            jedis.close();
        }
    }


}
