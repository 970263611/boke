package com.text.user.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.text.entity.Article;
import com.text.entity.Comment;
import com.text.entity.MyPhoto;
import com.text.entity.MyTest;
import com.text.entity.Permission;
import com.text.entity.Role;
import com.text.entity.User;
import com.text.entity.WordMessage;
import com.text.user.dao.AdminDao;
import com.text.user.service.UserService;

import net.sf.json.JSONArray;

@Controller
public class AdminController {

	@Autowired
	private AdminDao adminDao;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private UserService userService;
	
	/**
	 *跳转到admin后台管理的页面 
	 */
	@RequestMapping("/admin")
	public String admin(){
		return "admin";
	}
	
	/**
	 * 查询所有用户信息
	 * @return
	 */
	@RequestMapping("/admin_select_user")
	@ResponseBody
	public String admin_select_user(){
		return json(adminDao.admin_select_user());
	}
	
	/**
	 * 查询所有文章信息
	 * @return
	 */
	@RequestMapping("/admin_select_article")
	@ResponseBody
	public String admin_select_article(){
		return json(adminDao.admin_select_article());
	}
	
	/**
	 * 查询所有留言信息
	 * @return
	 */
	@RequestMapping("/admin_select_wordMessage")
	@ResponseBody
	public String admin_select_wordMessage(){
		return json(adminDao.admin_select_wordMessage());
	}
	
	/**
	 * 查询所有角色信息
	 * @return
	 */
	@RequestMapping("/admin_select_role")
	@ResponseBody
	public String admin_select_role(){
		return json(adminDao.admin_select_role());
	}
	
	/**
	 * 查询所有权限信息
	 * @return
	 */
	@RequestMapping("/admin_select_permission")
	@ResponseBody
	public String admin_select_permission(){
		return json(adminDao.admin_select_permission());
	}
	
	/**
	 * 查询所有用户个人中心文字信息
	 * @return
	 */
	@RequestMapping("/admin_select_myTest")
	@ResponseBody
	public String admin_select_mytest(){
		return json(adminDao.admin_select_mytest());
	}
	
	/**
	 * 查询所有用户个人中心图片信息
	 * @return
	 */
	@RequestMapping("/admin_select_myPhoto")
	@ResponseBody
	public String admin_select_myphoto(){
		return json(adminDao.admin_select_myphoto());
	}
	
	/**
	 * 更新文章表
	 */
	@RequestMapping("/admin_update_article")
	@ResponseBody
	public String admin_update_article(){
		Article article = new Article();
		
		int id = Integer.parseInt(request.getParameter("id"));
		String title = request.getParameter("title");    
		String content = request.getParameter("content");
		String lead = request.getParameter("lead");
		String create_user = request.getParameter("create_user");
		String create_time = request.getParameter("create_time");
		String type = request.getParameter("type");
		
		article.setId(id);
		article.setTitle(title);
		article.setContent(content);
		article.setLead(lead);
		article.setCreate_user(create_user);
		article.setCreate_time(create_time);
		article.setType(type);
		int a = adminDao.admin_update_article(article);
		if(a == 1) {
			return "success";
		}
		return null;
	}
	
	/**
	 * 更新评价表
	 */
	@RequestMapping("/admin_update_comment")
	@ResponseBody
	public String admin_update_comment(){
		Comment comment = new Comment();
		
		int id = Integer.parseInt(request.getParameter("id"));
		String create_user = request.getParameter("create_user");
		String create_time = request.getParameter("create_time");
		int a_id = Integer.parseInt(request.getParameter("a_id"));
		String message = request.getParameter("message");
		
		comment.setId(id);
		comment.setCreate_user(create_user);
		comment.setCreate_time(create_time);
		comment.setA_id(a_id);
		comment.setMessage(message);
		int a = adminDao.admin_update_comment(comment);
		if(a == 1) {
			return "success";
		}
		return null;
	}
	
	/**
	 * 更新个人照片表
	 */
	@RequestMapping("/admin_update_myPhoto")
	@ResponseBody
	public String admin_update_myPhoto(){
		MyPhoto myPhoto = new MyPhoto();
		
		int id = Integer.parseInt(request.getParameter("id"));
		int user_id = Integer.parseInt(request.getParameter("user_id"));
		String photo_1 = request.getParameter("photo_1");
		String photo_1_test = request.getParameter("photo_1_test");
		String photo_2 = request.getParameter("photo_2");
		String photo_2_test = request.getParameter("photo_2_test");
		String photo_3 = request.getParameter("photo_3");
		String photo_3_test = request.getParameter("photo_3_test");
		
		myPhoto.setId(id);
		myPhoto.setUser_id(user_id);
		myPhoto.setPhoto_1(photo_1);
		myPhoto.setPhoto_1_test(photo_1_test);
		myPhoto.setPhoto_2(photo_2);
		myPhoto.setPhoto_2_test(photo_2_test);
		myPhoto.setPhoto_3(photo_3);
		myPhoto.setPhoto_3_test(photo_3_test);
		
		int a = adminDao.admin_update_myPhoto(myPhoto);
		if(a == 1) {
			return "success";
		}
		return null;
	}
	
	/**
	 * 更新个人文字表
	 */
	@RequestMapping("/admin_update_myTest")
	@ResponseBody
	public String admin_update_myTest(){
		MyTest myTest = new MyTest();
		
		int id = Integer.parseInt(request.getParameter("id"));
		String test_1 = request.getParameter("test_1");
		String test_2 = request.getParameter("test_2");
		int user_id = Integer.parseInt(request.getParameter("user_id"));
		
		myTest.setId(id);
		myTest.setTest_1(test_1);
		myTest.setTest_2(test_2);
		myTest.setUser_id(user_id);
		
		int a = adminDao.admin_update_myTest(myTest);
		if(a == 1) {
			return "success";
		}
		return null;
	}
	
	/**
	 * 更新权限表
	 */
	@RequestMapping("/admin_update_permission")
	@ResponseBody
	public String admin_update_permission(){
		Permission permission = new Permission();
		
		int id = Integer.parseInt(request.getParameter("id"));
		String permissionName = request.getParameter("permissionName");
		int rowId = Integer.parseInt(request.getParameter("rowId"));
		
		permission.setId(id);
		permission.setPermissionName(permissionName);
		permission.setRowId(rowId);
		
		int a = adminDao.admin_update_permission(permission);
		if(a == 1) {
			return "success";
		}
		return null;
	}
	
	/**
	 * 更新角色表
	 */
	@RequestMapping("/admin_update_role")
	@ResponseBody
	public String admin_update_role(){
		Role role = new Role();
		
		int id = Integer.parseInt(request.getParameter("id"));
		String roleName = request.getParameter("roleName");
		
		role.setId(id);
		role.setRoleName(roleName);
		
		int a = adminDao.admin_update_role(role);
		if(a == 1) {
			return "success";
		}
		return null;
	}
	
	/**
	 * 更新用户表
	 */
	@RequestMapping("/admin_update_user")
	@ResponseBody
	public String admin_update_user(){
		User user = new User();
		
		int id = Integer.parseInt(request.getParameter("id"));
		String nickname = request.getParameter("nickname");
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		int rowId = Integer.parseInt(request.getParameter("rowId"));
		String realname = request.getParameter("realname");
		
		user.setId(id);
		user.setNickname(nickname);
		user.setName(name);
		user.setPassword(password);
		user.setRowId(rowId);
		user.setRealname(realname);
		
		int a = adminDao.admin_update_user(user);
		if(a == 1) {
			return "success";
		}
		return null;
	}
	
	/**
	 * 更新留言表
	 */
	@RequestMapping("/admin_update_wordMessage")
	@ResponseBody
	public String admin_update_wordMessage(){
		WordMessage wordMessage = new WordMessage();
		
		int id = Integer.parseInt(request.getParameter("id"));
		String create_user = request.getParameter("create_user");
		String create_time = request.getParameter("create_time");
		String message = request.getParameter("message");
		
		wordMessage.setId(id);
		wordMessage.setCreate_user(create_user);
		wordMessage.setCreate_time(create_time);
		wordMessage.setMessage(message);
		
		int a = adminDao.admin_update_wordMessage(wordMessage);
		if(a == 1) {
			return "success";
		}
		return null;
	}
	
	/**
	 * 根据ID删除文章表信息
	 */
	@RequestMapping("/admin_delete_article")
	@ResponseBody
	public String admin_delete_article(){
		String id = request.getParameter("id");
		int a = adminDao.admin_delete_article(id);
		if(a == 1) {
			return "success";
		}
		return null;
	}
	
	/**
	 * 根据ID删除评价表信息
	 */
	@RequestMapping("/admin_delete_comment")
	@ResponseBody
	public String admin_delete_comment(){
		String id = request.getParameter("id");
		int a = adminDao.admin_delete_comment(id);
		if(a == 1) {
			return "success";
		}
		return null;
	}
	
	/**
	 * 根据ID删除个人照片表信息
	 */
	@RequestMapping("/admin_delete_myPhoto")
	@ResponseBody
	public String admin_delete_myPhoto(){
		String id = request.getParameter("id");
		int a = adminDao.admin_delete_myPhoto(id);
		if(a == 1) {
			return "success";
		}
		return null;
	}
	
	/**
	 * 根据ID删除个人文字表信息
	 */
	@RequestMapping("/admin_delete_myTest")
	@ResponseBody
	public String admin_delete_myTest(){
		String id = request.getParameter("id");
		int a = adminDao.admin_delete_myTest(id);
		if(a == 1) {
			return "success";
		}
		return null;
	}
	
	/**
	 * 根据ID删除权限表信息
	 */
	@RequestMapping("/admin_delete_permission")
	@ResponseBody
	public String admin_delete_permission(){
		String id = request.getParameter("id");
		int a = adminDao.admin_delete_permission(id);
		if(a == 1) {
			return "success";
		}
		return null;
	}
	
	/**
	 * 根据ID删除角色表信息
	 */
	@RequestMapping("/admin_delete_role")
	@ResponseBody
	public String admin_delete_role(){
		String id = request.getParameter("id");
		int a = adminDao.admin_delete_role(id);
		if(a == 1) {
			return "success";
		}
		return null;
	}
	
	/**
	 * 根据ID删除用户表信息
	 */
	@RequestMapping("/admin_delete_user")
	@ResponseBody
	public String admin_delete_user(){
		String id = request.getParameter("id");
		int a = adminDao.admin_delete_user(id);
		if(a == 1) {
			return "success";
		}
		return null;
	}
	
	/**
	 * 根据ID删除留言表信息
	 */
	@RequestMapping("/admin_delete_wordMessage")
	@ResponseBody
	public String admin_delete_wordMessage(){
		String id = request.getParameter("id");
		int a = adminDao.admin_delete_wordMessage(id);
		if(a == 1) {
			return "success";
		}
		return null;
	}
	
	/**
	 * 查询所有评价信息
	 * @return
	 */
	@RequestMapping("/admin_select_comment")
	@ResponseBody
	public String admin_select_comment(){
		return json(adminDao.admin_select_comment());
	}
	
	public String json(List<?> list){
		JSONArray json = null;
		if(list.size()>0){
				json = JSONArray.fromObject(list);
		}
		 StringBuffer sb = new StringBuffer(json.toString());
		 sb.insert(0, "{\"current\": 1,\"rowCount\": 10,\"rows\": ");
		 sb.append(",\"total\": "+list.size()+"}");
		 return sb.toString();
	 }
		
		/**
		 * 缓存所有文章的方法，要先执行一下在进入系统，需要管理员手动执行
		 */
		@RequestMapping("admin_redis")
		public String admin_redis() {
			return userService.admin_redis();
		}
		
		/**
		 * 清空缓存的方法
		 */
		@RequestMapping("admin_clear")
		public String admin_clear() {
			return userService.admin_clear();
		}
	
}
