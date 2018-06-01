package com.text.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.text.realm.SerializeUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class JedisUtil {

		@Autowired
		private JedisPool jedisPool;
	
		// 连接redis
		public Jedis getRedis() {
	
			Jedis jedis = jedisPool.getResource();
			return jedis;
	
		}
	
		// redis释放连接
		public void closeRedis(Jedis jedis) {
			jedisPool.close();
		}
		
		public Object get(String key)
	     {
	            byte[] value = getRedis().get(key.getBytes());
	            return SerializeUtil. unserialize(value);
	     }
	     
	      /**delete a key**/
	      public boolean del(String key)
	     {
	            return getRedis().del(key.getBytes())>0;
	     }
}

