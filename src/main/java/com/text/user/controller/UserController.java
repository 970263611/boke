package com.text.user.controller;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.text.entity.Article;
import com.text.entity.Comment;
import com.text.entity.MyPhoto;
import com.text.entity.User;
import com.text.entity.WordMessage;
import com.text.user.service.UserService;
import com.text.util.BokeUtil;
import com.text.util.SerializeUtil;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
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
 		String userString=request.getParameter("user");
 		if(userString!=null&&username==null&&password==null) {
 			byte[] user_bytes = Base64.decodeBase64(userString);
 			username =((User) SerializeUtil.unserialize(user_bytes)).getName();
 			password =((User) SerializeUtil.unserialize(user_bytes)).getPassword();
 		}
		//通过shiro获取session
 		Subject subject= SecurityUtils.getSubject();
		Session session=BokeUtil.getSession();
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
	public String save_article(HttpServletRequest request){
		Session session=BokeUtil.getSession();
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
	public String comment_insert(HttpServletRequest request){
		Session session=BokeUtil.getSession();
		User user = (User) session.getAttribute("user");
		int a_id = Integer.parseInt(request.getParameter("a_id"));
		String message = request.getParameter("message");
		Comment comment = new Comment();
		comment.setA_id(a_id);
		if(user==null) {
			comment.setCreate_user("游客");
		}else {
			comment.setCreate_user(user.getNickname());
		}
		comment.setCreate_time(getTime());
		comment.setMessage(message);
		return userService.comment_insert(comment);
	}
	
	/**
	 * 添加留言
	 */
	@RequestMapping("words_mess")
	public String words_mess(HttpServletRequest request){
		Session session=BokeUtil.getSession();
		User user = (User) session.getAttribute("user");
		String message = request.getParameter("words_mess");
		WordMessage wordMessage = new WordMessage();
		if(user!=null){
			wordMessage.setCreate_user(user.getNickname());
		}else {
			wordMessage.setCreate_user("游客");
		}
		wordMessage.setCreate_time(getTime());
		wordMessage.setMessage(message);
		return userService.words_mess(wordMessage);
	}
	
	/**
	 * 用户注册
	 */
	@RequestMapping("userAdd")
	public String userAdd(HttpServletRequest request){
		User user = new User();
		String nickname = request.getParameter("nickname");
		String name = request.getParameter("username");
		String password = request.getParameter("password");
		String realname = request.getParameter("realname");
		if(BokeUtil.checkNull(nickname) || BokeUtil.checkNull(name) || BokeUtil.checkNull(password)){
			return "注册失败";
		}
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
	public String save_mytest(HttpServletRequest request){
		//开启session
		Session session=BokeUtil.getSession();
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
	public List<Article> Go_page(HttpServletRequest request){
		String page = request.getParameter("page");
		return userService.Go_page(Integer.parseInt(page));
	}
	
	/**
	 * 查询文章条数，便于前端分页
	 */
	@RequestMapping("pageNum")
	public int pageNum() {
		return userService.pageNum();
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
	
	@RequestMapping("getMYandLY")
	public HashMap<?, ?> getmyandly(String uId) {
		return userService.getmyandly(uId);
	}
	
	/**
	 * 照片页面获取照片
	 */
	@RequestMapping("getImages")
	public List<MyPhoto> getImages(){
		/**
		 * 查询最新照片信息
		 * @param originalFilename
		 * @return
		 */
		List<MyPhoto> photo = userService.select_all_four();
		
		for(MyPhoto pho:photo) {
			pho.setPhoto("http://www.loveding.top:8089/"+pho.getUser_id() + "/" + pho.getPhoto());
		}
		return photo;

	}
	
	/**
	 * 查询用户的关注关系
	 */
	@RequestMapping("checkFolllw")
	public String checkFolllw(String articleId) {
		return userService.checkFolllw(articleId);
	}
	
	/**
	 * 用户添加标签存储方法
	 */
	@RequestMapping("saveTags")
	public String saveTags(String tag) {
		return userService.saveTags(tag);
	}
	
	/**
	 * 获取消息
	 */
	@RequestMapping("getNotice")
	public List<String> getNotice(){
		
		Session session=BokeUtil.getSession();
		User user = (User) session.getAttribute("user");
		if(user!=null) {
			List<String> notices = userService.getNotices(user.getId());
			return notices;
		}
		return null;
	}
	
	/**
	 * 查看后删除消息
	 */
	@RequestMapping("delNotice")
	public String delNotice(){
		
		Session session=BokeUtil.getSession();
		User user = (User) session.getAttribute("user");
		if(user!=null) {
			return userService.delNotice(user.getId());
		}
		return null;
	}
	
	/**
	 * 分享文章
	 */
	@RequestMapping("shareArticle")
	public String shareArticle(HttpServletRequest request){
		String id = request.getParameter("id");
		Session session=BokeUtil.getSession();
		User user = (User) session.getAttribute("user");
		String nickname = "游客";
		if(user!=null) {
			nickname = user.getNickname();
		}
		return userService.shareArticle(id,nickname);
	}
	
}
