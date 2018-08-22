package com.text.user.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;

import com.text.entity.Article;
import com.text.entity.Comment;
import com.text.entity.MyPhoto;
import com.text.entity.User;
import com.text.entity.WordMessage;

@Mapper
public interface UserDao {
	
	/**
	 * 向数据库新增用户信息
	 * @return
	 */
	@Insert("insert into user(nickname,name,password)value(#{nickname},#{name},#{password})")
	void saveUser(User user);

	/**
	 * 用户登录方法，返回用户的昵称
	 * @param user
	 * @return
	 */
	@Select("select nickname from user where name = #{name} and password = #{password}")
	@Results({  
		@Result(property="nickname",column="nickname",javaType=String.class,jdbcType=JdbcType.VARCHAR)
	})  
	String selectNickname(User user);

	/**
	 * 查询所有的文章方法
	 * @return
	 */
	@Select("SELECT * FROM article where isdelete = '0' and top = '0' ORDER BY create_time DESC")
	List<Article> select_article_untop();
	
	/**
	 * 查询置顶的文章方法
	 * @return
	 */
	@Select("SELECT * FROM article where isdelete = '0' and top = '1' ORDER BY create_time DESC")
	List<Article> select_article_top();

	/**
	 * 添加一条文章信息
	 * @param article
	 * @return
	 */
	@Options(useGeneratedKeys=true, keyProperty="id")
	@Insert("insert into article (create_user,create_time,title,content,lead,type) values (#{create_user},#{create_time},#{title},#{content},#{lead},#{type})")
	int saveArticle(Article article);

	/**
	 * 根据id查询文章所有的信息
	 * @param id
	 * @return
	 */
	@Select("select * from article where id = #{0}")
	Article toSingle(String id);

	/**
	 * 根据用户名查询用户所有的信息
	 * @param username
	 * @return
	 */
	@Select("select * from user where name = #{0}")
	User se_user(String username);

	/**
	 * 通过用户名获取角色
	 * @param username
	 * @return
	 */
	@Select("select roleName from role a LEFT JOIN user b on a.id = b.rowId where b.name = #{0}")
	Set<String> getRoles(String username);

	/**
	 * 通过用户名获取权限
	 * @param username
	 * @return
	 */
	@Select("select permissionName from permission a LEFT JOIN role b on a.rowId = b.id LEFT JOIN user c on b.id = c.rowId where c.name = #{0}")
	Set<String> getPermissions(String username);

	/**
	 * 根据关联id查询文章的所有评价
	 */
	@Select("select * from comment where a_id = #{0}")
	List<Comment> select_message(String id);

	/**
	 * 提交评价
	 */
	@Insert("insert into comment (create_user,create_time,message,a_id) values (#{create_user},#{create_time},#{message},#{a_id})")
	int comment_insert(Comment comment);

	/**
	 * 提交留言信息
	 * @param wordMessage
	 * @return
	 */
	@Insert("insert into wordMessage (create_user,create_time,message) values (#{create_user},#{create_time},#{message})")
	int words_mess(WordMessage wordMessage);

	/**
	 * 用户注册
	 * @param user
	 * @return
	 */
	@Insert("insert into user (nickname,name,password,realname,createTime) values (#{nickname},#{name},#{password},#{realname},#{createTime})")
	int userAdd(User user);

	/**
	 * 查询新注册用户输入的用户名是否重名
	 * @param user
	 * @return
	 */
	@Select("select id from user where name = #{name}")
	String user_seName(User user);

	/**
	 * 根据用户昵称获取起最近所发的文章（3条）
	 * @param nickname
	 * @return
	 */
	@Select("select * from article where create_user = #{0} GROUP BY(create_time) DESC limit 0,3")
	List<Article> select_article_mine(String nickname);
	
	/**
	 * 根据用户昵称获取所有所发的文章
	 * @param nickname
	 * @return
	 */
	@Select("select * from article where create_user = #{0}")
	List<Article> select_article_user_all(String nickname);

	/**
	 * 查询新注册用户输入的昵称是否重名
	 * @param user
	 * @return
	 */
	@Select("select id from user where nickname = #{nickname}")
	String user_seNickname(User user);

	/**
	 * 保存用户的铭言和格言
	 */
	@Insert("insert into mytest (user_id,test_1,test_2) values (#{id},#{test1},#{test2})")
	int add_myworld_test(@Param("id") int id, @Param("test1") String test1,@Param("test2") String test2);

	/**
	 * 查询用户的铭言
	 */
	@Select("select test_1 from mytest where user_id = #{0}")
	String select_myworld_test1(int id);

	/**
	 * 查询用户的格言
	 */
	@Select("select test_2 from mytest where user_id = #{0}")
	String select_myworld_test2(int id);

	/**
	 * 更新用户的铭言和格言
	 */
	@Update("update mytest set test_1 = #{test1},test_2 = #{test2} where user_id = #{id}")
	int update_myworld_test(@Param("id") int id, @Param("test1") String test1, @Param("test2") String test2);

	/**
	 * 查询用户的照片信息
	 * @param originalFilename
	 * @return
	 */
	@Select("select * from myphoto where user_id = #{0} ORDER BY(update_time) DESC limit 0,3")
	List<MyPhoto> select_all(int id);
	
	/**
	 * 查询最新照片信息
	 * @param originalFilename
	 * @return
	 */
	@Select("SELECT a.*, b.nickname FROM myphoto a LEFT join user b on a.user_id = b.id ORDER BY(a.update_time) DESC LIMIT 0,4")
	List<MyPhoto> select_all_four();

	/**
	 * 保存用户上传图片
	 * @param originalFilename
	 */
	@Insert("insert into myphoto (user_id,photo,photo_test,update_time) values (#{user_id},#{originalFilename},#{text},#{date})")
	void fileUp(@Param("user_id") int user_id,@Param("originalFilename") String originalFilename,@Param("text") String text,@Param("date") String date);
	
	/**
	 * 查询文章条数，便于前端分页
	 */
	@Select("select count(id) from article")
	int pageNum();

	/**
	 * 缓存所有文章的方法，要先执行一下在进入系统，需要管理员手动执行
	 */
	@Select("select * from article where isdelete = '0'")
	List<Article> admin_redis();

	/**
	 * 根据用户id查询用户所有信息
	 */
	@Select("select * from user where id = #{0}")
	User select_user(Integer userId);

	/**
	 * 根据用户名和密码获取user对象
	 * @return
	 */
	@Select("select * from user where name=#{name} and password = #{password}")
	User finduser(User user);

	/**
	 * 将用户上传图片的byte传入数据库存储
	 * @param id
	 * @param nickname
	 * @param fileName
	 * @param image
	 */
	@Insert("insert into user_photo_byte (user_id,user_nickname,photo_name,photo_byte) values (#{id},#{nickname},#{fileName},#{image})")
	int create_imgByte(@Param("id") int id,@Param("nickname") String nickname,@Param("fileName") String fileName,@Param("image") byte[] image);

	/**
	 * 查询用户所有上传的照片
	 * @param nickname
	 * @return
	 */
	@Select("select a.*,b.nickname from myphoto a LEFT JOIN user b on a.user_id =  b.id where b.nickname = #{0} ORDER BY(a.update_time) desc")
	List<MyPhoto> select_photo_user_all(String nickname);

	@Insert("insert into login_ip (LoginIP,loginTime,userId,userName) values (#{LoginIP},#{loginTime},#{userId},#{userName})")
	void saveIP(@Param("LoginIP") String ip,@Param("loginTime") String time,@Param("userId") int userId,@Param("userName") String userName);

	/**
	 * 通过文章id查询文章信息
	 * @param articleId
	 * @return
	 */
	@Select("select * from article where id = #{0}")
	Article getArticleById(String articleId);

}
