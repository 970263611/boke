package com.dahua.boke.service;

import java.util.List;

import com.dahua.boke.entity.Article;
import com.dahua.boke.entity.Comment;
import com.dahua.boke.entity.MyTest;
import com.dahua.boke.entity.Permission;
import com.dahua.boke.entity.Role;
import com.dahua.boke.entity.User;
import com.dahua.boke.entity.WordMessage;

public interface AdminSerive {

	int admin_select_comment_totalSize();

	List<?> admin_select_comment(int i, int parseInt);

	int admin_delete_user(String id);

	int admin_delete_wordMessage(String id);

	int admin_delete_role(String id);

	int admin_delete_permission(String id);

	int admin_delete_myTest(String id);

	int admin_delete_myPhoto(String id);

	int admin_delete_comment(String id);

	int admin_delete_article(String id);

	int admin_update_wordMessage(WordMessage wordMessage);

	int admin_update_user(User user);

	int admin_update_role(Role role);

	int admin_update_permission(Permission permission);

	int admin_update_myTest(MyTest myTest);

	int admin_update_comment(Comment comment);

	int admin_update_article(Article article);

	List<?> admin_select_ip(int i, int parseInt);

	int admin_select_ip_totalSize();

	int admin_select_myphoto_totalSize();

	int admin_select_mytest_totalSize();

	List<?> admin_select_mytest(int i, int parseInt);

	int admin_select_permission_totalSize();

	List<?> admin_select_permission(int i, int parseInt);

	int admin_select_role_totalSize();

	List<?> admin_select_role(int i, int parseInt);

	List<?> admin_select_wordMessage(int i, int parseInt);

	int admin_select_wordMessage_totalSize();

	int admin_select_article_totalSize();

	List<?> admin_select_article(int i, int parseInt);

	int admin_select_user_totalSize();

	List<?> admin_select_user(int i, int parseInt);

	List<?> admin_select_myphoto(int i, int parseInt);

}
