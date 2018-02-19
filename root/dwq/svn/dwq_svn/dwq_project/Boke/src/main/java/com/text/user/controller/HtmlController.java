package com.text.user.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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

import com.text.entity.Article;
import com.text.entity.Comment;
import com.text.entity.MyPhoto;
import com.text.entity.User;
import com.text.user.dao.UserDao;
import com.text.user.service.UserService;

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
	  
	/**
	 * 默认首页
	 * @param model
	 * @return
	 */
	@RequestMapping("/")
	public String toIndex(Model model) {
		return "newindex";
	}
	/**
	 * 访问index页面后台跳转方法
	 * @return
	 */
	@RequestMapping("/index") 
    public String ToIndex(Model model) {
		//给一个没登陆的状态位为noLogin，如果登陆则替换为用户信息
		List<Article> list = userService.select_article_all();
		//查询第二条显示的文章
		Article a_two = userService.select_article_two();
		//查询置顶的丁伟强写的文章方法
		Article a_one = userService.select_article_one();
		model.addAttribute("a_two",a_two);
		model.addAttribute("a_one",a_one);
		model.addAttribute("list",list);
		//分页下一页功能(废弃)
//		Subject subject=SecurityUtils.getSubject();
//		Session session=subject.getSession();
//		session.setAttribute("nowPage",2);
		//获取全部页给前端，便于点击下一页时候判断是否到了最后一页
		Subject subject=SecurityUtils.getSubject();
		Session session=subject.getSession();
		session.setAttribute("allPage",userDao.pageNum());
        return "index";  
    } 
	
	/**
	 * 访问文章详情页面后台跳转方法
	 * @return
	 */
	@RequestMapping("/single") 
    public String ToSingel(Model model,String id) { 
		//根据id查询单条文章的所有信息
		Article article = userService.toSingle(id);
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
    public String ToWrite(Model model,String type) {  
		model.addAttribute("type", type);
		return "write";  
    } 
	
	/**
	 * 访问个人中心页面后台跳转方法
	 * @return
	 */
	@RequestMapping("/myworld") 
    public String myWorld(Model model,HttpServletRequest request) { 
		
 		String modify = request.getParameter("modify");
		String articleId = request.getParameter("articleId");
		
		Integer userId = null;
		String nickname = null;
		int Identification = 0;
		
		if(articleId != null) {
			nickname = userDao.toSingle(articleId).getCreate_user();
		}
		
		Subject subject=SecurityUtils.getSubject();
		Session session=subject.getSession();
		User user = (User) session.getAttribute("user");
		
		if(modify != null && !"".equals(modify) && nickname != null && !"".equals(nickname) && !nickname.equals(user.getNickname())) {
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
		List<Article> mylist = userService.select_article_mine(nickname);
		String test1 = userDao.select_myworld_test1(userId);
		String test2 = userDao.select_myworld_test2(userId);
		model.addAttribute("list",mylist);
 		model.addAttribute("test1",test1);
		model.addAttribute("test2",test2);
		MyPhoto myPhoto = userDao.select_all(userId);
		List<HashMap<String,String>> image_list = new ArrayList<>();
		if(myPhoto != null) {
			if(myPhoto.getPhoto_1() != null) {
				String image_1 = "http://www.loveding.top:8089/"+userId + "/" + myPhoto.getPhoto_1();
				HashMap<String,String> map = new HashMap<>();
				map.put("image", image_1);
				map.put("text", myPhoto.getPhoto_1_test());
				image_list.add(map);
			}
			if(myPhoto.getPhoto_2() != null) {
				String image_2 = "http://www.loveding.top:8089/"+userId + "/" + myPhoto.getPhoto_2();
				HashMap<String,String> map = new HashMap<>();
				map.put("image", image_2);
				map.put("text", myPhoto.getPhoto_2_test());
				image_list.add(map);
			}
			if(myPhoto.getPhoto_3() != null) {
				String image_3 = "http://www.loveding.top:8089/"+userId + "/" + myPhoto.getPhoto_3();
				HashMap<String,String> map = new HashMap<>();
				map.put("image", image_3);
				map.put("text", myPhoto.getPhoto_3_test());
				image_list.add(map);
				model.addAttribute("not_see", "yes");
			}
			model.addAttribute("image_list",image_list);
		}
		if(Identification == 1) {
			model.addAttribute("modify",modify);
			model.addAttribute("nickname",userDao.select_user(userId).getNickname());
		}else if(Identification == 2) {
			model.addAttribute("modify","yes");
			model.addAttribute("nickname",nickname);
		}
		return "myworld";  
    } 
	
	/**
	 * 访问注册帐号页面后台跳转方法
	 * @return
	 */
	@RequestMapping("/register") 
    public String ToRegister(Model model) {  
//		Subject subject=SecurityUtils.getSubject();
//		Session session=subject.getSession();
		//判断用户是否登陆，未登录，赋予状态noLogin
//    	if(session.getAttribute("user_Login")==null){
//			model.addAttribute("user_Login", "noLogin");
//		}
		return "login";  
    } 
	
	/**
	 * 访问login页面后台跳转方法，shiro添加支持
	 * @return
	 */
    @RequestMapping(value="/login", method = RequestMethod.GET)
	    public String ToLogin(Model model) {
	        return "login";
	    }

	/**
	 * 用户注销
	 */
    @RequestMapping("/loginOut")
    public String loginOut(Model model){
    	Subject subject=SecurityUtils.getSubject();
    	subject.logout();
    	return ToLogin(model);
    }
    
}