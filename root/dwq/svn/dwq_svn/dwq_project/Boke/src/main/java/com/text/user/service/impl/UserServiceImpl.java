package com.text.user.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.text.entity.Article;
import com.text.entity.Comment;
import com.text.entity.Follow;
import com.text.entity.MyPhoto;
import com.text.entity.User;
import com.text.entity.WordMessage;
import com.text.realm.RedisCacheConfiguration;
import com.text.realm.SerializeUtil;
import com.text.user.dao.UserDao;
import com.text.user.service.UserService;
import com.text.util.RedisUtil;
import com.text.util.RocketMQUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;

@Service
public class UserServiceImpl implements UserService {

	@Resource
	private UserDao userDao;
	@Autowired
	private SerializeUtil redisDateSourse;
	//rocketmq地址
	private static final String ipAddress = "101.132.136.190:9876";
	//产生者组
	private static final String producterName = "dwq";
	//topic名称
	private static final String topicName = "boke";

	/**
	 * 向数据库新增用户信息service实现类
	 * 
	 * @return
	 */
	public void saveUser(User user) {
		userDao.saveUser(user);
	}

	/**
	 * 用户登录方法，返回用户的昵称
	 */
	@Override
	public String Userlogin(User user) {
		return userDao.selectNickname(user);
	}

	/**
	 * 查询分页
	 */
	@Override
	public List<Article> Go_page(int page) {
		//开启redis连接
		Jedis jedis = redisDateSourse.getRedis();
		//限定一页装载的文章数量
		int pageSize = 6;
		//设定截止到当前页一共需要的文章条数
		int articleSize = page * pageSize;
		//本页第一条文章的位置
		int firstArticle = (page-1)*pageSize;
		/**
		 * redis排序方式
		 */
		SortingParams sortingParameters = new SortingParams();  
		sortingParameters.desc(); 
        sortingParameters.alpha();
        //新建装在置顶文章标题的list
        List<String> timeStr = new ArrayList<>();
        //置顶文章的数量
        int size = jedis.llen("top").intValue();
        //最后返回的list，所有要返回的数据装在这里面
        List<Article> acList = new ArrayList<>();
        //非置顶文章的数量
        int asize = jedis.llen("article").intValue();
        //当redis中置顶文章数量为0的时候
        if (size != 0) {
        	//当redis中置顶文章数量大于当前页（包含前面的页）所需要的总文章条数
	        if(size > articleSize) {
	        	//将文章标题从redis中取出
	        	timeStr = jedis.sort("top",sortingParameters).subList(firstArticle, pageSize);
	        	//将文章从redis中取出
	        	acList.addAll(RedisUtil.hgetArticle(timeStr, jedis));
	        	//返回文章信息
	        	return acList;
	        	//当redis中置顶文章不大于当前页（包含前面的页）所需要的总文章条数
	        }else {
	        	//当置顶文章数量取余本页所需要文章数量为0的时候
	        	if(size%pageSize==0){
	        		//判断当前页是否有置顶文章，通过置顶文章总数除于当前一页所需要文章数量如果等于当前的页码（则本页有置顶文章）
	        		if(size/pageSize==page) {
	        			//到此可以保证前面的都是置顶文章，排序redis中的置顶文章取出当前页所需要第一条，就是置顶文章里面对应的那条，取到置顶文章条数结束，因为到此可以保证置顶文章数量不够本页的条数
	        			timeStr = jedis.sort("top",sortingParameters).subList(firstArticle,size);
	        			//将取出的文章放到返回的list里面
	        			acList.addAll(RedisUtil.hgetArticle(timeStr, jedis));
	        		}
        		//当置顶文章数量取余本页所需要文章数量不为0的时候
	        	}else{
	        		//判断当前页是否有置顶文章，通过置顶文章总数除于当前一页所需要文章数量加上1如果等于当前的页码（则本页有置顶文章）
	        		if(size/pageSize+1==page) {
	        			//到此可以保证前面的都是置顶文章，排序redis中的置顶文章取出当前页所需要第一条，就是置顶文章里面对应的那条，取到置顶文章条数结束，因为到此可以保证置顶文章数量不够本页的条数
	        			timeStr = jedis.sort("top",sortingParameters).subList(firstArticle,size);
	        			//将取出的文章放到返回的list里面
	        			acList.addAll(RedisUtil.hgetArticle(timeStr, jedis));
	        		}
	        	}
	        	//当非置顶文章数量不为0的时候
	        	if(asize != 0) {
	        		//定义非置顶文章标题的list
	        		List<String> notops = new ArrayList<>();
	        		//当非置顶文章的数量大于等于当前页（包含前面的页）所需要的总文章条数减去置顶文章的数量时
	        		if(asize>=(articleSize-size)){
	        			//当置顶文章数量和非置顶文章数量的和除于本页所需要文章数量（一共的页数，本应加1，等式后面也要加1，抵消了）减去置顶文章数量除于本页所需要文章数量（即置顶文章的页数）大于0的时候（说明文章不全是置顶文章，存在非置顶文章）
	        			if((size+asize)/pageSize-(size/pageSize)>0) {
	        				//当当前页码减1乘上本页所需要文章数量(截止到前一页所需要的文章总数)减去置顶文章数小于0的时候（说明本页有置顶文章，但是置顶文章有不够本页时）
	        				if((page-1)*pageSize-size<0) {
	        					//redis中取出非置顶文章的标题
	        					notops = jedis.sort("article",sortingParameters).subList(0, articleSize-size);
	        				}else {
	        					//说明本页没有置顶文章，全是非置顶文章，在redis中取出所需要的非置顶文章标题
	        					//(page-1)*本页所需要文章数量-size本页减1乘上本页所需要文章数量减去置顶文章数为非置顶文章填补给置顶文章的个数，从这个取开始取（截止到本页所需要的减去置顶文章）个（填补给置顶文章凑齐本页个数，保证个数正确）
	        					notops = jedis.sort("article",sortingParameters).subList((page-1)*pageSize-size, articleSize-size);
	        				}
        				//当置顶文章数量和非置顶文章数量的和除于每页所需要文章数量（一共的页数，本应加1，等式后面也要加1，抵消了）减去置顶文章数量除于每页所需要文章数量*（即置顶文章的页数）不大于0的时候（说明不全是置顶文章，存在非置顶文章，所以从0开始取，取本页减去置顶文章个数剩余的个数）
	        			}else {
	        				notops = jedis.sort("article",sortingParameters).subList(0, pageSize-size%pageSize);
	        			}
        			//当非置顶文章的总数量不够填补本页了
	        		}else {
	        			//从redis中取出所有非置顶文章标题
	        			notops = jedis.sort("article",sortingParameters).subList(0, asize);
	        		}
	        		//根据标题从redis中取出应该返回的非置顶文章添加到返回的list中
	        		acList.addAll(RedisUtil.hgetArticle(notops,jedis));
        		//当非置顶文章个数为0的时候
	        	}else {
	        		//从数据库中取出所有的非置顶文章
	        		List<Article> untops = userDao.select_article_untop();
	        		//将所有的非置顶文章缓存到redis中
	    			RedisUtil.hsetArticle(untops,jedis);
	    			//递归调用并返回
	    			return Go_page(page);
	        	}
	        }
        //当置顶文章个数为0的时候
        }else {
        	//判断数据库置顶文章个数是否为0，当为0的情况时
        	if(userDao.select_article_top().size()==0) {
        		//从redis中查询非置顶文章的个数
        		int newasize = jedis.llen("article").intValue();
        		//当redis中非置顶文章个数大于等于本页所需要文章数量的时候（足够填充本页）
        		if(newasize>=pageSize) {
        			//从redis取出非置顶文章的前本页所需要文章数量条
        			acList = RedisUtil.hgetArticle(jedis.sort("article",sortingParameters).subList(0, pageSize),jedis);
    			//当redis中非置顶文章个数不足够填充本页时
        		}else {
        			//取出redis中所有的非置顶文章
        			acList = RedisUtil.hgetArticle(jedis.sort("article",sortingParameters).subList(0, asize),jedis);
        		}
    		//判断数据库置顶文章个数是否为0，当不为0的情况时	
        	}else {
        		List<Article> topac = userDao.select_article_top();
        		//查询出数据库所有的置顶文章
        		acList.addAll(topac);
        		//将置顶文章缓存到redis中
        		RedisUtil.hsetTopArticle(acList,jedis);
        		//递归调用并返回
    			return Go_page(page);
        	}
		}
        //关闭redis连接，释放资源
        redisDateSourse.closeRedis(jedis);
        //返回文章信息给前端
		return acList;
	}
	
	/**
	 * 保存文章方法事物层实现
	 */
	@Override
	public String save_article(Article ac) {
		Jedis jedis = redisDateSourse.getRedis();
		int size = userDao.saveArticle(ac);
		if (size == 1) {
			RedisUtil.hsetArticleSingle(ac,jedis);
			redisDateSourse.closeRedis(jedis);
			return "success";
		} else {
			return "error";
		}
	}

	/**
	 * 根据id查询单挑文章记录，用于进入文章详情页面
	 */
	@Override
	public Article toSingle(String id) {
		Jedis jedis = redisDateSourse.getRedis();
		Article ac = null;
		String value = jedis.hget("article_" + id, "id");
		if(RedisUtil.notEnpty(value)) {
			ac = RedisUtil.hgetArticleSingle(id, jedis);
		}else {
			ac = userDao.toSingle(id);
			RedisUtil.hsetArticleSingle(ac,jedis);
		}
		redisDateSourse.closeRedis(jedis);
		return ac;
	}

	/**
	 * 根据关联id查询文章的所有评价
	 */
	@Override
	public List<Comment> select_message(String id) {
		return userDao.select_message(id);
	}

	/**
	 * 提交评价
	 */
	@Override
	public String comment_insert(Comment comment) {
		int size = userDao.comment_insert(comment);
		if (size == 1) {
			return "success";
		}
		return null;
	}

	/**
	 * 提交留言信息
	 * 
	 * @param wordMessage
	 * @return
	 */
	@Override
	public String words_mess(WordMessage wordMessage) {
		int size = userDao.words_mess(wordMessage);
		if (size == 1) {
			return "success";
		}
		return null;
	}

	/**
	 * 用户注册
	 * 
	 * @param user
	 * @return
	 */
	@Override
	public String userAdd(User user) {
		// 先查询新注册用户昵称是否重名
		String id_two = userDao.user_seNickname(user);
		if (id_two != null) {
			return "NickNamealReguster";
		}
		// 先查询新注册用户用户名是否重名
		String id = userDao.user_seName(user);
		if (id != null) {
			return "alReguster";
		}
		// 将拥护注册信息添加到数据库
		int size = userDao.userAdd(user);
		if (size == 1) {
			//通过shiro获取session
			Subject subject=SecurityUtils.getSubject();
			Session session=subject.getSession();
			//令牌验证登陆
			subject.login(new UsernamePasswordToken(user.getName(), user.getPassword()));
			session.setAttribute("user", user);
			return "success";
		}
		return null;
	}

	/**
	 * 根据用户昵称获取起所发的文章
	 * 
	 * @param nickname
	 * @return
	 */
	@Override
	public List<Article> select_article_mine(String nickname) {
		return (List<Article>) userDao.select_article_mine(nickname);
	}

	/**
	 * 保存用户的铭言和格言
	 */
	@Override
	public String add_myworld_test(int id, String test1, String test2) {
		int a = 0;
		String test_1 = userDao.select_myworld_test1(id);
		String test_2 = userDao.select_myworld_test2(id);
		if (test_1 != null || test_2 != null) {
			a = userDao.update_myworld_test(id, test1, test2);
		} else {
			a = userDao.add_myworld_test(id, test1, test2);
		}
		if (a == 1) {
			return "success";
		}
		return "error";
	}

	/**
	 * 查询文章条数，便于前端分页
	 */
	@Override
	public int pageNum() {
		return userDao.pageNum();
	}

	/**
	 * 缓存所有文章的方法，要先执行一下在进入系统，需要管理员手动执行
	 */
	@Override
	public String admin_redis() {
		List<Article> list = userDao.admin_redis();
		Jedis jedis = redisDateSourse.getRedis();
		Logger logger = LoggerFactory.getLogger(RedisCacheConfiguration.class);
		List<Article> topAc = new ArrayList<>();
		List<Article> untopAc = new ArrayList<>();
		for(Article ac:list) {
			if(ac.getTop().equals("1")) {
				topAc.add(ac);
			}else {
				untopAc.add(ac);
			}
		}
		RedisUtil.hsetTopArticle(topAc,jedis);
		RedisUtil.hsetArticle(untopAc,jedis);
		List<Follow> follows = userDao.getFollows();
		HashSet<Integer> set = new HashSet<>();
		for(Follow follow:follows){
			set.add(follow.getParentId());
		}
		for(Integer parentId:set){
			List<Integer> childList = new ArrayList<>();
			for(Follow follow:follows){
				if(parentId == follow.getParentId()){
					childList.add(follow.getChildId());
				}
			}
			jedis.set(("follow_"+parentId).getBytes(), SerializeUtil.serialize(childList));
		}
		redisDateSourse.closeRedis(jedis);
		logger.info("------所有文章缓存成功------");
		return "redis_success";
	}

	@Override
	public String admin_clear() {
		Logger logger = LoggerFactory.getLogger(RedisCacheConfiguration.class);
		Jedis jedis = redisDateSourse.getRedis();
		jedis.flushAll();
		redisDateSourse.closeRedis(jedis);
		logger.info("------清空缓存成功------");
		return "redis_clear";
	}

	/**
	 * 根据用户名和密码获取user对象
	 * @return
	 */
	@Override
	public User getUser(User user) {
		User newUser = userDao.finduser(user);
		return newUser;
	}

	/**
	 * 根据用户昵称获取起所有的文章
	 * @param nickname
	 * @return
	 */
	@Override
	public List<Article> select_article_user_all(String nickname) {
		return userDao.select_article_user_all(nickname);
	}

	/**
	 * 查询最新照片信息
	 * @param originalFilename
	 * @return
	 */
	@Override
	public List<MyPhoto> select_all_four() {
		return userDao.select_all_four();
	}

	/**
	 * 根据用户id查询用户所有信息
	 */
	
	@Override
	public User select_user(Integer userId) {
		return userDao.select_user(userId);
	}

	/**
	 * 查询用户所有上传的照片
	 * @param nickname
	 * @return
	 */
	@Override
	public List<MyPhoto> select_photo_user_all(String nickname) {
		return userDao.select_photo_user_all(nickname);
	}

	/**
	 * 限制当前ip1分钟内最多访问10次本页面（防爬虫增大服务器压力）
	 */
	public boolean visit(String ip,String sign){
		boolean flag = false;
		Jedis jedis = redisDateSourse.getRedis();
		String loginSize = jedis.get(ip+sign);
		if(loginSize!=null && !loginSize.equals("")){
			int size = Integer.parseInt(loginSize);
			if(size<11){
				int time = jedis.ttl(ip+sign).intValue();//查询剩余过期时间
				jedis.set(ip+sign, (Integer.parseInt(loginSize)+1)+"");//覆盖访问次数
				jedis.expire(ip+sign, time);//重新设置过期时间
				flag = true;
			}
		}else{
			jedis.set(ip+sign, "1");
			jedis.expire(ip+sign, 60);
			flag = true;
		}
		redisDateSourse.closeRedis(jedis);
		return flag;
	}

	/**
	 * 关注方法
	 */
	@Override
	public String follow(String articleId) {
		Integer parentId = userDao.getUserIdByArticleId(articleId);
		Subject subject=SecurityUtils.getSubject();
		Session session=subject.getSession();
		User user = (User) session.getAttribute("user");
		Jedis jedis = redisDateSourse.getRedis();
		byte[] bytes = jedis.get(("follow_"+parentId).getBytes());
		List<Integer> childIds = (List<Integer>) SerializeUtil.unserialize(bytes);//取出关注用户的子集
		if(childIds.contains(user.getId())){//如果子集包含当前登录用户
			System.out.println("此用户已经关注了");
			return "successed";
		}else{
			Follow follow = new Follow();
			follow.setParentId(parentId);;
			follow.setChildId(user.getId());
			follow.setCreateTime(new Date());
			RocketMQUtil.producer(ipAddress, producterName, topicName, "follow", JSONObject.toJSONString(follow));
			childIds.add(user.getId());
			jedis.set(("follow_"+parentId).getBytes(), SerializeUtil.serialize(childIds));
		}
		redisDateSourse.closeRedis(jedis);
        return "success";
    }

	/**
	 * 文章置顶的方法
	 */
	@Override
	public String top(String articleId,String time) {
		Jedis jedis = redisDateSourse.getRedis();
		jedis.lpush("top", time+"*"+articleId);
		jedis.hset("article_" + articleId+"", "top", "1");
		jedis.lrem("article", 0, time+"*"+articleId);
		redisDateSourse.closeRedis(jedis);
		RocketMQUtil.producer(ipAddress, producterName, topicName, "top", articleId);
		return "success";
	}

	/**
	 * 文章取消置顶的方法
	 */
	@Override
	public String untop(String articleId,String time) {
		Jedis jedis = redisDateSourse.getRedis();
		jedis.lrem("top", 0, time+"*"+articleId);
		jedis.hset("article_" + articleId+"", "top", "0");
		jedis.lpush("article", time+"*"+articleId);
		redisDateSourse.closeRedis(jedis);
		RocketMQUtil.producer(ipAddress, producterName, topicName, "untop", articleId);
		return "success";
	}

	/**
	 * 文章删除的方法
	 */
	@Override
	public String isdel(String articleId,String time) {
		Jedis jedis = redisDateSourse.getRedis();
		jedis.del("article_" + articleId);
		redisDateSourse.closeRedis(jedis);
		RocketMQUtil.producer(ipAddress, producterName, topicName, "isdel", articleId);
		return "success";
	}

	/**
	 * 消费者消费方法
	 * @param msgs
	 */
	public void custom(List<MessageExt> msgs) {
		/*for (MessageExt msg : msgs) {
			String topic = msg.getTags();
			String body = new String(msg.getBody());
			Follow follow = JSON.parseObject(new String(msg.getBody()), Follow.class);
        }*/
	}

}
