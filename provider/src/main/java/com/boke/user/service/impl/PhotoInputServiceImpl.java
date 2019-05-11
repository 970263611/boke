package com.boke.user.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boke.user.dao.UserDao;
import com.dahua.boke.entity.User;
import com.dahua.boke.service.PhotoInputService;
import com.dahua.boke.util.SerializeUtil;

import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class PhotoInputServiceImpl implements PhotoInputService{

	@Autowired
	private UserDao userDao;
	@Autowired
	private JedisPool jedisPool;

	@SuppressWarnings("unchecked")
	public JSONObject fileUp(String fileName,String userId,int id,String text,HttpServletResponse response,User user) throws IOException {
		HashMap<Object, Object> map = new HashMap<>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String date = df.format(new Date());// new Date()为获取当前系统时间
		userDao.fileUp(id,fileName,text,date);
		map.put("data", "success");
		Jedis jedis = jedisPool.getResource();
		byte[] childs = jedis.get(("follow_"+user.getId()).getBytes());
		List<Integer> childIds = (List<Integer>) SerializeUtil.unserialize(childs);
		if(childIds!=null) {
			for(Integer newId:childIds) {
				byte[] noticeByte = jedis.hget("notice".getBytes(), (newId+"").getBytes());
				List<String> notices = new ArrayList<>();
				if(SerializeUtil.unserialize(noticeByte)!=null) {
					notices.addAll((List<String>) SerializeUtil.unserialize(noticeByte));
				}
				notices.add("您关注的"+user.getNickname()+"刚刚发布了一篇留言为"+text+"的图片");
				jedis.hset("notice".getBytes(), (newId+"").getBytes(), SerializeUtil.serialize(notices));
			}
		}
		jedis.close();
		return JSONObject.fromObject(map);
	}

//	public String fileUp_test(int id, String test_photo) {
//		MyPhoto myPhoto = userDao.select_all(id);
//		if(myPhoto == null) {
//			return "error";
//		}else if(myPhoto.getPhoto_1() == null) {
//			return "error";
//		}else if(myPhoto.getPhoto_2() == null && myPhoto.getPhoto_1() != null && myPhoto.getPhoto_1_test() == null) {
//			userDao.fileUp_test_1(id,test_photo);
//			return "success";
//		}else if(myPhoto.getPhoto_3() == null && myPhoto.getPhoto_2() != null && myPhoto.getPhoto_2_test() == null) {
//			userDao.fileUp_test_2(id,test_photo);
//			return "success";
//		}else if(myPhoto.getPhoto_3() != null && myPhoto.getPhoto_3_test() == null) {
//			userDao.fileUp_test_3(id,test_photo);
//			return "success";
//		}else {
//			return "error";
//		}
//	}
}
