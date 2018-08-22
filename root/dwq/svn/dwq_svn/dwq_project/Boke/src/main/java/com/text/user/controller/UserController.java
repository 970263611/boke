package com.text.user.controller;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.text.entity.Article;
import com.text.entity.Comment;
import com.text.entity.User;
import com.text.entity.WordMessage;
import com.text.user.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private HttpServletRequest request;
	
	/**
	 * 获取当前时间的公用方法
	 */
	public String getTime(){ 
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yy-MM-dd HH:mm");
		String time=format.format(date);
		return time;
	}
	
	/**
	 * 用户登陆方法
	 * @param request
	 * @param model
	 * @return
	 */
    // 登录提交地址和applicationontext-shiro.xml配置的loginurl一致。 (配置文件方式的说法)
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request,Model model){
    	String username=request.getParameter("username");
		String password=request.getParameter("password");
		//通过shiro获取session
		Subject subject=SecurityUtils.getSubject();
		Session session=subject.getSession();
		//令牌验证登陆
		UsernamePasswordToken token=new UsernamePasswordToken(username, password);
		try{
			subject.login(token);
			User user = new User(username,password);
			User newUser = userService.getUser(user);
			SecurityUtils.getSubject().getSession().setTimeout(600000);
	    	session.setAttribute("user", newUser);
			session.setAttribute("user_Login","alLogin");
	        return "success";
		}catch(Exception e){
			return "error";
		}
    }
	
	/**
	 * 保存文章方法
	 */
	@RequestMapping("save_article")
	public String save_article(){
		Subject subject=SecurityUtils.getSubject();
		Session session=subject.getSession();
		User user = (User) session.getAttribute("user");
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String lead = request.getParameter("lead");
		String type = request.getParameter("type");
		Article article = new Article();
		article.setCreate_user(user.getNickname());
		article.setCreate_time(getTime());
		article.setTitle(title);
		article.setContent(content);
		if(lead.length()>150){
			article.setLead(lead.substring(0,150));
		}else{
			article.setLead(lead);
		}
		article.setType(type);
		return userService.save_article(article);
	}
	
	/**
	 * 提交评价
	 */
	@RequestMapping("comment_insert")
	public String comment_insert(){
		Subject subject=SecurityUtils.getSubject();
		Session session=subject.getSession();
		User user = (User) session.getAttribute("user");
		int a_id = Integer.parseInt(request.getParameter("a_id"));
		String message = request.getParameter("message");
		Comment comment = new Comment();
		comment.setA_id(a_id);
		comment.setCreate_user(user.getNickname());
		comment.setCreate_time(getTime());
		comment.setMessage(message);
		return userService.comment_insert(comment);
	}
	
	/**
	 * 添加留言
	 */
	@RequestMapping("words_mess")
	public String words_mess(){
		Subject subject=SecurityUtils.getSubject();
		Session session=subject.getSession();
		User user = (User) session.getAttribute("user");
		String message = request.getParameter("words_mess");
		WordMessage wordMessage = new WordMessage();
		if(user!=null){
			wordMessage.setCreate_user(user.getNickname());
		}
		wordMessage.setCreate_time(getTime());
		wordMessage.setMessage(message);
		return userService.words_mess(wordMessage);
	}
	
	/**
	 * 用户注册
	 */
	@RequestMapping("userAdd")
	public String userAdd(){
		User user = new User();
		String nickname = request.getParameter("nickname");
		String name = request.getParameter("username");
		String password = request.getParameter("password");
		String realname = request.getParameter("realname");
		user.setNickname(nickname);
		user.setName(name);
		user.setPassword(password);
		user.setRealname(realname);
		user.setCreateTime(new Date());
		return userService.userAdd(user);
	}
	
	/**
	 * 保存用户的铭言和格言
	 */
	@RequestMapping("save_mytest")
	public String save_mytest(){
		//开启session
		Subject subject=SecurityUtils.getSubject();
		Session session=subject.getSession();
		User user = (User) session.getAttribute("user");
		
		int id = user.getId();
		String test1 = request.getParameter("test1");
		String test2 = request.getParameter("test2");
		
		return userService.add_myworld_test(id,test1,test2);
	}
	
	/**
	 * 分页跳转，传过来页数
	 */
	@RequestMapping("go_page")
	public List<Article> Go_page(){
		String page = request.getParameter("page");
		if(Integer.parseInt(page)>1) {
			return userService.Go_page(Integer.parseInt(page));
		}else {
			return null;
		}
	}
	
	/**
	 * 分页跳转，传过来页数
	 */
	@RequestMapping("go_page_first")
	public List<Article> go_page_first(){
		List<Article> tops = userService.select_article_top();
		List<Article> list = new ArrayList<>();
		if(tops.size()>6){
			tops = tops.subList(0, 6);
		}else{
			list = userService.select_article_all();
			list = list.subList(0, 6-tops.size());
		}
		List<Article> data = new ArrayList<Article>();
		data.addAll(tops);
		data.addAll(list);
		return data;
	}
	
	/**
	 * 查询文章条数，便于前端分页
	 */
	@RequestMapping("pageNum")
	public int pageNum() {
		return userService.pageNum();
	}
	
	/**
	 * 判断用户是否登陆
	 */
	@RequestMapping("userLoginOr")
	public String userLoginOr() {
		Subject subject=SecurityUtils.getSubject();
		Session session=subject.getSession();
		User user = (User) session.getAttribute("user");
		if(user==null) {
			return null;
		}else {
			return user.getNickname();
		}
	}
	
	/**
	 * 用户关注的方法
	 */
	@RequestMapping("follow")
	public String follow(String articleId){
		return userService.follow(articleId);
	}
	
	/**
	 * 文章置顶的方法
	 */
	@RequestMapping("top")
	public String top(String articleId,String time){
		return userService.top(articleId,time);
	}
	
	/**
	 * 文章取消置顶的方法
	 */
	@RequestMapping("untop")
	public String untop(String articleId,String time){
		return userService.untop(articleId,time);
	}
	
	/**
	 * 文章删除的方法
	 */
	@RequestMapping("isdel")
	public String isdel(String articleId,String time){
		return userService.isdel(articleId,time);
	}
	
}
