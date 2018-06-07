package com.text.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.text.entity.Article;
import com.text.entity.Comment;
import com.text.entity.LoginIP;
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
	 * @param j 
	 * @param i 
	 * @return
	 */
	@Select("select * from user limit #{i},#{j}")
	public List<User> admin_select_user(@Param("i")int i,@Param("j") int j);
	
	@Select("select COUNT(id) from user")
	public int admin_select_user_totalSize();

	/**
	 * 查询所有文章信息
	 * @param j 
	 * @param i 
	 * @return
	 */
	@Select("select * from article limit #{i},#{j}")
	public List<Article> admin_select_article(@Param("i")int i,@Param("j") int j);
	
	@Select("select COUNT(id) from article")
	public int admin_select_article_totalSize();

	/**
	 * 查询所有留言信息
	 * @param j 
	 * @param i 
	 * @return
	 */
	@Select("select * from wordMessage limit #{i},#{j}")
	public List<WordMessage> admin_select_wordMessage(@Param("i")int i,@Param("j") int j);
	
	@Select("select COUNT(id) from wordMessage")
	public int admin_select_wordMessage_totalSize();

	
	/**
	 * 查询所有角色信息
	 * @param j 
	 * @param i 
	 * @return
	 */
	@Select("select * from role limit #{i},#{j}")
	public List<Role> admin_select_role(@Param("i")int i,@Param("j") int j);
	
	@Select("select COUNT(id) from role")
	public int admin_select_role_totalSize();

	
	/**
	 * 查询所有权限信息
	 * @param j 
	 * @param i 
	 * @return
	 */
	@Select("select * from permission limit #{i},#{j}")
	public List<Permission> admin_select_permission(@Param("i")int i,@Param("j") int j);
	
	@Select("select COUNT(id) from permission")
	public int admin_select_permission_totalSize();

	
	/**
	 * 查询所有用户个人中心文字信息
	 * @param j 
	 * @param iint 
	 * @return
	 */
	@Select("select * from mytest limit #{i},#{j}")
	public List<MyTest> admin_select_mytest(@Param("i")int i,@Param("j") int j);
	
	@Select("select COUNT(id) from mytest")
	public int admin_select_mytest_totalSize();

	/**
	 * 查询所有用户个人中心图片信息
	 * @param j 
	 * @param i 
	 * @return
	 */
	@Select("select * from myphoto limit #{i},#{j}")
	public List<MyPhoto> admin_select_myphoto(@Param("i")int i,@Param("j") int j);
	
	@Select("select COUNT(id) from myphoto")
	public int admin_select_myphoto_totalSize();

	/**
	 * 查询所有评价信息
	 * @param j 
	 * @param i 
	 * @return
	 */
	@Select("select * from comment limit #{i},#{j}")
	public List<Comment> admin_select_comment(@Param("i")int i,@Param("j") int j);
	
	@Select("select COUNT(id) from comment")
	public int admin_select_comment_totalSize();
	
	/**
	 * 查询所有登陆ip
	 * @param j 
	 * @param i 
	 * @return
	 */
	@Select("select * from login_ip limit #{i},#{j}")
	public List<LoginIP> admin_select_ip(@Param("i")int i,@Param("j") int j);
	
	@Select("select COUNT(id) from login_ip")
	public int admin_select_ip_totalSize();
	
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
