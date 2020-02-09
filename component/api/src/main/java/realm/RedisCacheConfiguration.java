package realm;


	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.beans.factory.annotation.Value;
	import org.springframework.cache.annotation.CachingConfigurerSupport;
	import org.springframework.cache.annotation.EnableCaching;
	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.Configuration;
	import redis.clients.jedis.JedisPool;
	import redis.clients.jedis.JedisPoolConfig;

	/**
	 * 类名：RedisCacheConfiguration<br>
	 * 描述：<br>
	 * 创建人：<br>
	 * 创建时间：2016/9/6 17:33<br>
	 *
	 * @version v1.0
	 */

	@Configuration
	@EnableCaching
	public class RedisCacheConfiguration extends CachingConfigurerSupport {
	    Logger logger = LoggerFactory.getLogger(RedisCacheConfiguration.class);

	    @Value("${spring.redis.host}")
	    private String host;

	    @Value("${spring.redis.port}")
	    private int port;

	    @Value("${spring.redis.timeout}")
	    private int timeout;

	    @Value("${spring.redis.pool.max-idle}")
	    private int maxIdle;

	    @Value("${spring.redis.pool.max-wait}")
	    private long maxWaitMillis;

//	    @Value("${spring.redis.password}")
//	    private String password;

	    @Bean
	    public JedisPool redisPoolFactory() {
	        logger.info("JedisPool注入成功！！");
	        logger.info("redis地址：" + host + ":" + port);
	        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
	        jedisPoolConfig.setMaxIdle(maxIdle);
	        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);

//	        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
	        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);

	        return jedisPool;
	    }

	}
