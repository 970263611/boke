package com.text.user.service;

import java.util.List;

import com.text.entity.Article;
import com.text.entity.Comment;
import com.text.entity.User;
import com.text.entity.WordMessage;

public interface UserService {

	/**
	 * 向数据库新增用户信息service接口
	 * @return
	 */
	void saveUser(User user);

	/**
	 * 用户登录方法service接口，返回用户的昵称
	 * @param user
	 * @return
	 */
	String Userlogin(User user);

	/**
	 * 查询数据库，返回文章信息，返回到index页面显示
	 * @param user
	 * @return index
	 */
	List<Article> select_article_all();

	/**
	 * 保存文章方法
	 * @param article 
	 * @return 返回到index页面
	 */
	String save_article(Article article);

	/**
	 * 根据id查询单挑文章记录，用于进入文章详情页面
	 * @param id
	 */
	Article toSingle(String id);

	/**
	 * 根据关联id查询文章的所有评价
	 */
	List<Comment> select_message(String id);

	/**
	 * 提交评价
	 */
	String comment_insert(Comment comment);

	/**
	 * 提交留言信息
	 * @param wordMessage
	 * @return
	 */
	String words_mess(WordMessage wordMessage);

	/**
	 * 用户注册
	 * @param user
	 * @return
	 */
	String userAdd(User user);

	/**
	 * 根据用户昵称获取起所发的文章
	 * @param nickname
	 * @return
	 */
	List<Article> select_article_mine(String nickname);

	/**
	 * 查询第二条显示的文章
	 * @return
	 */
	Article select_article_two();

	/**
	 * 查询置顶的丁伟强写的文章方法
	 * @return
	 */
	Article select_article_one();

	/**
	 * 保存用户的铭言和格言
	 */
	String add_myworld_test(int id, String test1,String test2);

	/**
	 * 分页，跳转对应页
	 * @return
	 */
	List<Article> Go_page(int page);

	/**
	 * 查询文章条数，便于前端分页
	 */
	int pageNum();

	/**
	 * 缓存所有文章的方法，要先执行一下在进入系统，需要管理员手动执行
	 */
	String admin_redis();

	String admin_clear();

	/**
	 * 根据用户名和密码获取user对象
	 * @return
	 */
	User getUser(User user);

	
}