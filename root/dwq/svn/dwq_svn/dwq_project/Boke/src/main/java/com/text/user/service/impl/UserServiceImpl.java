package com.text.user.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Select;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.text.entity.Article;
import com.text.entity.Comment;
import com.text.entity.MyPhoto;
import com.text.entity.PageData;
import com.text.entity.User;
import com.text.entity.WordMessage;
import com.text.realm.RedisCacheConfiguration;
import com.text.realm.SerializeUtil;
import com.text.user.dao.UserDao;
import com.text.user.service.UserService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;

@Service
public class UserServiceImpl implements UserService {

	@Resource
	private UserDao userDao;
	@Autowired
	private SerializeUtil redisDateSourse;

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
	 * 查询数据库，返回文章信息，返回到index页面显示
	 * 
	 * @param user
	 * @return
	 */
	@Override
	public List<Article> select_article_all() {
		List<Article> list = new ArrayList<Article>();;
		Jedis jedis = redisDateSourse.getRedis();
		SortingParams sortingParameters = new SortingParams();  
		sortingParameters.desc(); 
        sortingParameters.alpha();
		// 通过排序article这个键，得到排序后想要的值，再根据值（就是对应文章对象的键）去查出文章对象的byte数组转换后返回
		if (jedis.llen("article") != 0) {
			List<String> timeStr = jedis.sort("article",sortingParameters).subList(0, 4);
			if (timeStr.size() > 0) {
				for (String a:timeStr) {
					byte[] value = jedis.get(a.getBytes());
					Object object = SerializeUtil.unserialize(value);
					if (object != null) {
						Article ac = (Article) object;
						list.add(ac);
					}
				}
			}
		} else {
			list = userDao.select_article_all();
			for (Article ac : list) {
				jedis.lpush("article", ac.getCreate_time()+ac.getId());
				jedis.set((ac.getCreate_time()+ac.getId()).getBytes(), SerializeUtil.serialize(ac));
				jedis.set(String.valueOf(ac.getId()).getBytes(), SerializeUtil.serialize(ac));
			}
		}
		redisDateSourse.closeRedis(jedis);
		return list;
	}

	/**
	 * 保存文章方法事物层实现
	 */
	@Override
	public String save_article(Article ac) {
		Jedis jedis = redisDateSourse.getRedis();
		int size = userDao.saveArticle(ac);
		if (size == 1) {
			jedis.lpush("article", ac.getCreate_time()+ac.getId());
			jedis.set((ac.getCreate_time()+ac.getId()).getBytes(), SerializeUtil.serialize(ac));
			jedis.set(String.valueOf(ac.getId()).getBytes(), SerializeUtil.serialize(ac));
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
		byte[] value = jedis.get(String.valueOf(id).getBytes());
		if(value!=null) {
			Object object = SerializeUtil.unserialize(value);
			if (object != null) {
				ac = (Article) object;
			}
		}else {
			ac = userDao.toSingle(id);
			jedis.lpush("article", ac.getCreate_time()+ac.getId());
			jedis.set((ac.getCreate_time()+ac.getId()).getBytes(), SerializeUtil.serialize(ac));
			jedis.set(String.valueOf(ac.getId()).getBytes(), SerializeUtil.serialize(ac));
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
	 * 查询第二条显示的文章
	 * 
	 * @return
	 */
	@Override
	public Article select_article_two() {
		Jedis jedis = redisDateSourse.getRedis();
		Article ac = null;
		if(jedis.get("top2") != null) {
			byte[] bytes = jedis.get(jedis.get("top2").getBytes());
			ac = (Article) SerializeUtil.unserialize(bytes);
		}else {
			ac = (Article) userDao.select_article_two();
			jedis.set("top2", ac.getCreate_time()+ac.getId());
			jedis.set((ac.getCreate_time()+ac.getId()).getBytes(), SerializeUtil.serialize(ac));
			jedis.set(String.valueOf(ac.getId()).getBytes(), SerializeUtil.serialize(ac));
		}
		redisDateSourse.closeRedis(jedis);
		return ac;
	}

	/**
	 * 查询置顶的丁伟强写的文章方法
	 * 
	 * @return
	 */
	@Override
	public Article select_article_one() {
		Jedis jedis = redisDateSourse.getRedis();
		Article ac = null;
		if(jedis.get("top1") != null) {
			byte[] bytes = jedis.get(jedis.get("top1").getBytes());
			ac = (Article) SerializeUtil.unserialize(bytes);
		}else {
			ac = (Article) userDao.select_article_one();
			jedis.set("top1", ac.getCreate_time()+ac.getId());
			jedis.set((ac.getCreate_time()+ac.getId()).getBytes(), SerializeUtil.serialize(ac));
			jedis.set(String.valueOf(ac.getId()).getBytes(), SerializeUtil.serialize(ac));
		}
		redisDateSourse.closeRedis(jedis);
		return ac;
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
	 * 查询分页其他页（第一页除外）
	 */
	@Override
	public List<Article> Go_page(int page) {
		int first = (page - 1) * 6 - 1;
		List<Article> list = new ArrayList<Article>();
		Jedis jedis = redisDateSourse.getRedis();
		SortingParams sortingParameters = new SortingParams();  
        sortingParameters.desc(); 
        sortingParameters.alpha();
		// 通过排序article这个键，得到排序后想要的值，再根据值（就是对应文章对象的键）去查出文章对象的byte数组转换后返回
        Long size = jedis.llen("article");
        List<String> timeStr = null;
		if (size - first + 1 > 0) {
			//由于sublist的特殊性，所以要特殊写，不能沿用mysql的数字
			if(size > first-1+6) {
				timeStr = jedis.sort("article",sortingParameters).subList(first-1, first-1+6);
			}else {
				timeStr = jedis.sort("article",sortingParameters).subList(first-1,size.intValue());
			}
			if (timeStr.size() > 0) {
				for (String a:timeStr) {
					byte[] value = jedis.get(a.getBytes());
					Object object = SerializeUtil.unserialize(value);
					if (object != null) {
						Article ac = (Article) object;
						list.add(ac);
					}
				}
			}
		} else {
			list = userDao.select_article_Gopage(first-1);
			for (Article ac : list) {
				jedis.lpush("article", ac.getCreate_time()+ac.getId());
				jedis.set((ac.getCreate_time()+ac.getId()).getBytes(), SerializeUtil.serialize(ac));
				jedis.set(String.valueOf(ac.getId()).getBytes(), SerializeUtil.serialize(ac));
			}
		}
		redisDateSourse.closeRedis(jedis);
		return list;
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
		for (Article ac : list) {
			if(ac.getId()!=1 && ac.getId()!=2) {
				jedis.lpush("article", ac.getCreate_time()+ac.getId());
				jedis.set((ac.getCreate_time()+ac.getId()).getBytes(), SerializeUtil.serialize(ac));
				jedis.set(String.valueOf(ac.getId()).getBytes(), SerializeUtil.serialize(ac));
			}else if(ac.getId()==1) {
				jedis.set("top1", ac.getCreate_time()+ac.getId());
				jedis.set((ac.getCreate_time()+ac.getId()).getBytes(), SerializeUtil.serialize(ac));
				jedis.set(String.valueOf(ac.getId()).getBytes(), SerializeUtil.serialize(ac));
			}else if(ac.getId()==2) {
				jedis.set("top2", ac.getCreate_time()+ac.getId());
				jedis.set((ac.getCreate_time()+ac.getId()).getBytes(), SerializeUtil.serialize(ac));
				jedis.set(String.valueOf(ac.getId()).getBytes(), SerializeUtil.serialize(ac));
			}
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
}
