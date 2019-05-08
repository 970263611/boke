package com.text.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class SerializeUtil{
	
	@Autowired
	private JedisPool jedisPool;


		public static byte[] serialize(Object object) {
	    	  ObjectOutputStream oos = null;
	    	  ByteArrayOutputStream baos = null;
	    	  try {
	    		//序列化
	    		baos = new ByteArrayOutputStream();
	    		oos = new ObjectOutputStream(baos);
	    		oos.writeObject(object);
	    		byte[] bytes = baos.toByteArray();
	    		return bytes;
    		} catch (Exception e) {
    		}
	    		return null;
	     }

	      public static Object unserialize(byte[] bytes) {
	           ByteArrayInputStream bais = null;
	            try {
	                 // 反序列化
	                bais = new ByteArrayInputStream(bytes);
	                ObjectInputStream ois = new ObjectInputStream(bais);
	                 return ois.readObject();
	           } catch (Exception e) {

	           }
	            return null;
	     }
	      
		
			// 连接redis
			public Jedis getRedis() {
				Jedis jedis = jedisPool.getResource();
				return jedis;
			}
		
			// redis释放连接
			public void closeRedis(Jedis jedis) {
				jedis.close();
			}
			
			public Object get(String key)
		     {
		            byte[] value = getRedis().get(key.getBytes());
		            return SerializeUtil.unserialize(value);
		     }
		     
		      /**delete a key**/
		      public boolean del(String key)
		     {
		            return getRedis().del(key.getBytes())>0;
		     }
	}
