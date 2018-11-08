package com.text.user.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.text.entity.Article;
import com.text.entity.Comment;
import com.text.entity.MyPhoto;
import com.text.entity.User;
import com.text.user.dao.UserDao;
import com.text.user.service.UserService;
import com.text.user.service.WeChatService;
import com.text.util.BokeUtil;

/**
 * 跳转到index主页
 * @author 丁伟强
 *
 */
@Controller  
public class HtmlController {  
	
	@Autowired
	private UserService userService;
	@Autowired
	private UserDao userDao;
	@Autowired 
	private WeChatService weChatService;
	
	/**
	 * 默认首页
	 * @param model
	 * @return
	 */
	@RequestMapping("/")
	public String toIndex(Model model,HttpServletRequest request) {
		String ipAddress = BokeUtil.getIP(request);
		/**
		 * 限制当前ip1分钟内最多访问20次本页面（防爬虫增大服务器压力）
		 */
		if(!userService.visit(ipAddress,"newindex")){
			return "loginToMuch";
		}
		Subject subject=SecurityUtils.getSubject();
		Session session=subject.getSession();
	    String time = BokeUtil.getStringTime();
		User newUser = (User) session.getAttribute("user");
		if(newUser == null) {
			userDao.saveIP(ipAddress,time,0,null);
		}else {
			userDao.saveIP(ipAddress,time,newUser.getId(),newUser.getName());
		}
		return "newindex";
	}
	/**
	 * 访问index页面后台跳转方法
	 * @return
	 */
	@RequestMapping("/index") 
    public String ToIndex(Model model,HttpServletRequest request) {
		String ipAddress = BokeUtil.getIP(request);
		/**
		 * 限制当前ip1分钟内最多访问20次本页面（防爬虫增大服务器压力）
		 */
		if(!userService.visit(ipAddress,"index")){
			return "loginToMuch";
		}
		
		//分页下一页功能(废弃)
//		Subject subject=SecurityUtils.getSubject();
//		Session session=subject.getSession();
//		session.setAttribute("nowPage",2);
		//获取全部页给前端，便于点击下一页时候判断是否到了最后一页
		Subject subject=SecurityUtils.getSubject();
		Session session=subject.getSession();
		session.setAttribute("allPage",userDao.pageNum());
		User user = (User) session.getAttribute("user");
		if(user!=null){
			model.addAttribute("rowId",user.getRowId()+"");
		}else{
			model.addAttribute("rowId","2");
		}
        return "index";  
    } 
	
	@RequestMapping("images")
	public String images(HttpServletRequest request) {
		String ipAddress = BokeUtil.getIP(request);
		/**
		 * 限制当前ip1分钟内最多访问20次本页面（防爬虫增大服务器压力）
		 */
		if(!userService.visit(ipAddress,"user_article")){
			return "loginToMuch";
		}
		return "images";
	}
	
	/**
	 * 访问文章详情页面后台跳转方法
	 * @return
	 */
	@RequestMapping("/single") 
    public String ToSingel(Model model,String id,HttpServletRequest request) { 
		String ipAddress = BokeUtil.getIP(request);
		/**
		 * 限制当前ip1分钟内最多访问20次本页面（防爬虫增大服务器压力）
		 */
		if(!userService.visit(ipAddress,"single")){
			return "loginToMuch";
		}
		//根据id查询单条文章的所有信息
		Article article = userService.toSingle(id);
		userService.setSee(id);
		//根据id查询关联的所有评价
		List<Comment> commentList= userService.select_message(id);
		model.addAttribute("article", article);
		model.addAttribute("commentList", commentList);
        return "single";  
    } 
	
	/**
	 * 访问文章编辑页面后台跳转方法
	 * @return
	 */
	@RequestMapping("/write") 
    public String ToWrite(Model model,String type,HttpServletRequest request) { 
		String ipAddress = BokeUtil.getIP(request);
		/**
		 * 限制当前ip1分钟内最多访问20次本页面（防爬虫增大服务器压力）
		 */
		if(!userService.visit(ipAddress,"write")){
			return "loginToMuch";
		}
		model.addAttribute("type", type);
		return "write";  
    } 
	
	/**
	 * 访问个人中心页面后台跳转方法
	 * @return
	 */
	@RequestMapping("/myworld") 
    public String myWorld(Model model,HttpServletRequest request) { 
		String ipAddress = BokeUtil.getIP(request);
		/**
		 * 限制当前ip1分钟内最多访问20次本页面（防爬虫增大服务器压力）
		 */
		if(!userService.visit(ipAddress,"myworld")){
			return "loginToMuch";
		}
		
 		String modify = request.getParameter("modify");
		String articleId = request.getParameter("articleId");
		
		Integer userId = null;
		String nickname = null;
		int Identification = 0;
		
		if(articleId != null) {
			nickname = userDao.toSingle(articleId).getCreate_user();
			model.addAttribute("articleId",articleId);
		}else {
			model.addAttribute("articleId","no");
		}
		
		Subject subject=SecurityUtils.getSubject();
		Session session=subject.getSession();
		User user = (User) session.getAttribute("user");
		
		if(modify == null && user == null) {
			return "login";
		}
		
		if(((modify != null && modify.equals("no") && user == null) || modify != null && !"".equals(modify) && nickname != null && !"".equals(nickname) && !nickname.equals(user.getNickname()))) {
			User nickname_user = new User();
			nickname_user.setNickname(nickname);
			userId = Integer.parseInt(userDao.user_seNickname(nickname_user));
			Identification = 1;
		}else {
			//从session中获取当前登陆人昵称
			nickname = user.getNickname();
			userId = user.getId();
			Identification = 2;
		}
		List<Article> mylist = userService.select_article_user_all(nickname);
		model.addAttribute("userId",userId);
		model.addAttribute("list",mylist);
		List<MyPhoto> myPhotoList = userDao.select_all(userId);
		List<HashMap<String,String>> image_list = new ArrayList<>();
		if(myPhotoList.size() > 0) {
			for(MyPhoto myPhoto:myPhotoList) {
				String image = "http://www.loveding.top:8089/"+userId + "/" + myPhoto.getPhoto();
				HashMap<String,String> map = new HashMap<>();
				map.put("image", image);
				map.put("text", myPhoto.getPhoto_test());
				image_list.add(map);
			}
			model.addAttribute("image_list",image_list);
		}
		List<String> html = new ArrayList<>();
		if(Identification == 1) {
			model.addAttribute("modify",modify);
			String newnickname = userDao.select_user(userId).getNickname();
			for(int i=0;i<newnickname.length();i++) {
				html.add(String.valueOf(newnickname.charAt(i)));
			}
			model.addAttribute("newnickname",html);
			model.addAttribute("nickname",newnickname);
		}else if(Identification == 2) {
			model.addAttribute("modify","yes");
			for(int i=0;i<nickname.length();i++) {
				html.add(String.valueOf(nickname.charAt(i)));
			}
			model.addAttribute("newnickname",html);
			model.addAttribute("nickname",nickname);
		}
		return "myword";  
    } 
	
	/**
	 * 访问注册帐号页面后台跳转方法
	 * @return
	 */
	@RequestMapping("/register") 
    public String ToRegister(Model model,HttpServletRequest request) {  
		String ipAddress = BokeUtil.getIP(request);
		/**
		 * 限制当前ip1分钟内最多访问20次本页面（防爬虫增大服务器压力）
		 */
		if(!userService.visit(ipAddress,"login")){
			return "loginToMuch";
		}
		return "login";  
    } 
	
	/**
	 * 访问login页面后台跳转方法，shiro添加支持
	 * @return
	 */
    @RequestMapping(value="/login", method = RequestMethod.GET)
	    public String ToLogin(Model model,HttpServletRequest request) {
    	String code = request.getParameter("code");
    	String state = request.getParameter("state");
    	System.out.println("state-------------------------------------------------------"+state);
    	System.out.println("code-------------------------------------------------------"+code);
		if(code!=null){
			weChatService.weChatLogin(code);
			if("write1".equals(state)){
				return ToWrite(model,"1",request);
			}else if("write2".equals(state)){
				return ToWrite(model,"2",request);
			}else if("write3".equals(state)){
				return ToWrite(model,"3",request);
			}else if("index".equals(state)){
				return ToIndex(model,request);
			}else if("myworld".equals(state)){
				return myWorld(model,request);
			}else if("images".equals(state)){
				return images(request);
			}
		}else{
			String ipAddress = BokeUtil.getIP(request);
			if(!userService.visit(ipAddress,"loginToMuch")){
				return "loginToMuch";
			}
		}
	        return "login";
	    }

	/**
	 * 用户注销
	 */
    @RequestMapping("/loginOut")
    public String loginOut(Model model,HttpServletRequest request){
    	Subject subject=SecurityUtils.getSubject();
    	subject.logout();
    	return ToLogin(model,request);
    }
    
    /**
	 * 用户文章
	 */
    @RequestMapping("/user_article")
    public String user_article(Model model,String nickname,HttpServletRequest request){
    	String ipAddress = BokeUtil.getIP(request);
		/**
		 * 限制当前ip1分钟内最多访问20次本页面（防爬虫增大服务器压力）
		 */
		if(!userService.visit(ipAddress,"user_article")){
			return "loginToMuch";
		}
    	List<Article> list = userService.select_article_user_all(nickname);
    	model.addAttribute("list", list);
    	return "user_article";
    }
    
    /**
     * 访问照片详情页面
     */
    @RequestMapping("/user_photo")
    public String user_photo(Model model,String nickname,HttpServletRequest request){
    	String ipAddress = BokeUtil.getIP(request);
		/**
		 * 限制当前ip1分钟内最多访问20次本页面（防爬虫增大服务器压力）
		 */
		if(!userService.visit(ipAddress,"loginToMuch")){
			return "loginToMuch";
		}
    	List<MyPhoto> photos = userService.select_photo_user_all(nickname);
    	for(MyPhoto myPhoto:photos) {
    		myPhoto.setPhoto("http://www.loveding.top:8089/"+myPhoto.getUser_id() + "/" +myPhoto.getPhoto());
    	}
    	model.addAttribute("photos", photos);
    	return "user_photo";
    }
    
    @RequestMapping("/mine")
    public String mine(HttpServletRequest request){
    	String ipAddress = BokeUtil.getIP(request);
		/**
		 * 限制当前ip1分钟内最多访问20次本页面（防爬虫增大服务器压力）
		 */
		if(!userService.visit(ipAddress,"user_article")){
			return "loginToMuch";
		}
    	return "mine";
    }
}