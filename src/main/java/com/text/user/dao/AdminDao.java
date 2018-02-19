package com.text.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.text.entity.Article;
import com.text.entity.Comment;
import com.text.entity.MyPhoto;
import com.text.entity.MyTest;
import com.text.entity.Permission;
import com.text.entity.Role;
import com.text.entity.User;
import com.text.entity.WordMessage;

@Mapper
public interface AdminDao {

	/**
	 * 查询所有用户信息
	 * @return
	 */
	@Select("select * from user")
	public List<User> admin_select_user();

	/**
	 * 查询所有文章信息
	 * @return
	 */
	@Select("select * from article")
	public List<Article> admin_select_article();

	/**
	 * 查询所有留言信息
	 * @return
	 */
	@Select("select * from wordMessage")
	public List<WordMessage> admin_select_wordMessage();

	
	/**
	 * 查询所有角色信息
	 * @return
	 */
	@Select("select * from role")
	public List<Role> admin_select_role();

	
	/**
	 * 查询所有权限信息
	 * @return
	 */
	@Select("select * from permission")
	public List<Permission> admin_select_permission();

	
	/**
	 * 查询所有用户个人中心文字信息
	 * @return
	 */
	@Select("select * from mytest")
	public List<MyTest> admin_select_mytest();

	/**
	 * 查询所有用户个人中心图片信息
	 * @return
	 */
	@Select("select * from myphoto")
	public List<MyPhoto> admin_select_myphoto();

	/**
	 * 查询所有评价信息
	 * @return
	 */
	@Select("select * from comment")
	public List<Comment> admin_select_comment();

	/**
	 * 更新文章表信息
	 * @return
	 */
	@Update("update article set id=#{id},title=#{title},content=#{content},lead=#{lead},create_user=#{create_user},create_time=#{create_time},type=#{type} where id=#{id}")
	public int admin_update_article(Article article);
	
	/**
	 * 更新评价表信息
	 * @return
	 */
	@Update("update comment set id=#{id},create_user=#{create_user},create_time=#{create_time},a_id={a_id},message=#{message} where id=#{id}")
	public int admin_update_comment(Comment comment);

	/**
	 * 更新个人照片表信息
	 * @return
	 */
	@Update("update myphoto set id=#{id},user_id=#{user_id},photo_1=#{photo_1},photo_1_test=#{photo_1_test},photo_2=#{photo_2},photo_2_test=#{photo_2_test},photo_3=#{photo_3},photo_3_test=#{photo_3_test} where id=#{id}")
	public int admin_update_myPhoto(MyPhoto myPhoto);
	
	/**
	 * 更新个人文字表信息
	 * @return
	 */
	@Update("update mytest set id=#{id},test_1=#{test_1},test_2=#{test_2},user_id=#{user_id} where id=#{id}")
	public int admin_update_myTest(MyTest myTest);
	
	/**
	 * 更新权限表信息
	 * @return
	 */
	@Update("update permission set id=#{id},permissionName=#{permissionName},rowId=#{rowId} where id=#{id}")
	public int admin_update_permission(Permission permission);
	
	/**
	 * 更新角色表信息
	 * @return
	 */
	@Update("update role set id=#{id},roleName=#{roleName} where id=#{id}")
	public int admin_update_role(Role role);
	
	/**
	 * 更新用户表信息
	 * @return
	 */
	@Update("update user set id=#{id},nickname=#{nickname},name=#{name},password=#{password},rowId=#{rowId},realname=#{realname} where id=#{id}")
	public int admin_update_user(User user);
	
	/**
	 * 更新留言表信息
	 * @return
	 */
	@Update("update wordmessage set id=#{id},create_user=#{create_user},create_time=#{create_time},message=#{message} where id=#{id}")
	public int admin_update_wordMessage(WordMessage wordMessage);
	
	/**
	 * 删除文章表信息
	 */
	@Delete("delete from article where id=#{0}")
	public int admin_delete_article(String id);
	
	/**
	 * 删除评价表信息
	 */
	@Delete("delete from comment where id=#{0}")
	public int admin_delete_comment(String id);
	
	/**
	 * 删除个人照片表信息
	 */
	@Delete("delete from myphoto where id=#{0}")
	public int admin_delete_myPhoto(String id);
	
	/**
	 * 删除个人文字表信息
	 */
	@Delete("delete from mytest where id=#{0}")
	public int admin_delete_myTest(String id);
	
	/**
	 * 删除权限表信息
	 */
	@Delete("delete from permission where id=#{0}")
	public int admin_delete_permission(String id);
	
	/**
	 * 删除角色表信息
	 */
	@Delete("delete from role where id=#{0}")
	public int admin_delete_role(String id);
	
	/**
	 * 删除用户表信息
	 */
	@Delete("delete from user where id=#{0}")
	public int admin_delete_user(String id);
	
	/**
	 * 删除留言表信息
	 */
	@Delete("delete from wordmessage where id=#{0}")
	public int admin_delete_wordMessage(String id);
}
