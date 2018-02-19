package com.text.user.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.text.entity.MyPhoto;
import com.text.user.dao.UserDao;

import net.sf.json.JSONObject;

@Service
public class PhotoInputService {
	
	@Autowired
	private UserDao userDao;

	public JSONObject fileUp(String fileName,String userId,int id,String text,HttpServletResponse response) throws IOException {
		HashMap<Object, Object> map = new HashMap<>();
		MyPhoto myPhoto = userDao.select_all(id);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		userDao.create_imgByte(id,nickname,fileName,out.toByteArray());
		if(myPhoto == null) {
			userDao.fileUp(id,fileName,text);
		}else if(myPhoto.getPhoto_1() == null) {
			userDao.fileUp(id,fileName,text);
		}else if(myPhoto.getPhoto_2() == null) {
			userDao.fileUp_2(id,fileName,text);
		}else if(myPhoto.getPhoto_3() == null) {
			userDao.fileUp_3(id,fileName,text);
		}
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
