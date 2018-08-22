package com.text.user.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.text.user.dao.UserDao;

import net.sf.json.JSONObject;

@Service
public class PhotoInputService {
	
	@Autowired
	private UserDao userDao;

	public JSONObject fileUp(String fileName,String userId,int id,String text,HttpServletResponse response) throws IOException {
		HashMap<Object, Object> map = new HashMap<>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String date = df.format(new Date());// new Date()为获取当前系统时间
		userDao.fileUp(id,fileName,text,date);
		map.put("data", "success");
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
