package com.boke.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.dahua.boke.entity.User;

@Mapper
public interface WeChatDao {
	
	/**
	 * 通过用户的open_id查询此用户是否注册过
	 */
	@Select("select * from user where realname = #{0}")
	User getUserId(String realname);

}
