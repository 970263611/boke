package com.dahua.boke.service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.dahua.boke.entity.User;

import net.sf.json.JSONObject;

public interface PhotoInputService {

	JSONObject fileUp(String fileName,String userId,int id,String text,HttpServletResponse response, User user) throws IOException;
}
