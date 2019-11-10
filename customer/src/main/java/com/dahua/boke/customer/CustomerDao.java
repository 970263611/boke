package com.dahua.boke.customer;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.dahua.boke.entity.Follow;
import com.dahua.boke.entity.LeaveMes;
import com.dahua.boke.entity.Share;
import com.dahua.boke.entity.Tag;

@Mapper
@Repository
public interface CustomerDao {

	//置顶
	@Update("update article set top = '1' where id = #{0}")
	void top(String id);
	//取消置顶
	@Update("update article set top = '0' where id = #{0}")
	void untop(String id);
	//删除
	@Update("update article set isdelete = '1' where id = #{0}")
	void isdel(String id);
	//关注
	@Insert("insert into follow (parentId,childId,createTime) values (#{parentId},#{childId},#{createTime})")
	void follow(Follow follow);
	//存储标签
	@Insert("insert into tag (userId,nickname,tag) values (#{userId},#{nickname},#{tag})")
	void tagSave(Tag map);
	//分享文章
	@Insert("insert into share (id,url,nickname,imgName,createTime) values (#{id},#{url},#{nickname},#{imgName},#{createTime})")
	void shareSave(Share map);
	//留言存储
	@Insert("insert into leaveMes (fromuser,touser,message) values (#{fromuser},#{touser},#{message})")
	void leaveMesSave(LeaveMes map);

}
