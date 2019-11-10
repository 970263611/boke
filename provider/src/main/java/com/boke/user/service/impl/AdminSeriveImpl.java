package com.boke.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahua.boke.entity.Article;
import com.dahua.boke.entity.Comment;
import com.dahua.boke.entity.MyTest;
import com.dahua.boke.entity.Permission;
import com.dahua.boke.entity.Role;
import com.dahua.boke.entity.User;
import com.dahua.boke.entity.WordMessage;
import com.dahua.boke.service.AdminSerive;
import com.boke.user.dao.AdminDao;

@Service
public class AdminSeriveImpl implements AdminSerive{

	@Autowired
	private AdminDao adminDao;

	@Override
	public int admin_select_comment_totalSize() {
		return adminDao.admin_select_comment_totalSize();
	}

	@Override
	public List<?> admin_select_comment(int i, int parseInt) {
		return adminDao.admin_select_comment(i,parseInt);
	}

	@Override
	public int admin_delete_user(String id) {
		return adminDao.admin_delete_user(id);
	}

	@Override
	public int admin_delete_wordMessage(String id) {
		return adminDao.admin_delete_wordMessage(id);
	}

	@Override
	public int admin_delete_role(String id) {
		return adminDao.admin_delete_role(id);
	}

	@Override
	public int admin_delete_permission(String id) {
		return adminDao.admin_delete_permission(id);
	}

	@Override
	public int admin_delete_myTest(String id) {
		return adminDao.admin_delete_myTest(id);
	}

	@Override
	public int admin_delete_myPhoto(String id) {
		return adminDao.admin_delete_myPhoto(id);
	}

	@Override
	public int admin_delete_comment(String id) {
		return adminDao.admin_delete_comment(id);
	}

	@Override
	public int admin_delete_article(String id) {
		return adminDao.admin_delete_article(id);
	}

	@Override
	public int admin_update_wordMessage(WordMessage wordMessage) {
		return adminDao.admin_update_wordMessage(wordMessage);
	}

	@Override
	public int admin_update_user(User user) {
		return adminDao.admin_update_user(user);
	}

	@Override
	public int admin_update_role(Role role) {
		return adminDao.admin_update_role(role);
	}

	@Override
	public int admin_update_permission(Permission permission) {
		return adminDao.admin_update_permission(permission);
	}

	@Override
	public int admin_update_myTest(MyTest myTest) {
		return adminDao.admin_update_myTest(myTest);
	}

	@Override
	public int admin_update_comment(Comment comment) {
		return adminDao.admin_update_comment(comment);
	}

	@Override
	public int admin_update_article(Article article) {
		return adminDao.admin_update_article(article);
	}

	@Override
	public List<?> admin_select_ip(int i, int parseInt) {
		return adminDao.admin_select_ip(i,parseInt);
	}

	@Override
	public int admin_select_ip_totalSize() {
		return adminDao.admin_select_ip_totalSize();
	}

	@Override
	public int admin_select_myphoto_totalSize() {
		return adminDao.admin_select_myphoto_totalSize();
	}

	@Override
	public int admin_select_mytest_totalSize() {
		return adminDao.admin_select_mytest_totalSize();
	}

	@Override
	public List<?> admin_select_mytest(int i, int parseInt) {
		return adminDao.admin_select_mytest(i,parseInt);
	}

	@Override
	public int admin_select_permission_totalSize() {
		return adminDao.admin_select_permission_totalSize();
	}

	@Override
	public List<?> admin_select_permission(int i, int parseInt) {
		return adminDao.admin_select_permission(i,parseInt);
	}

	@Override
	public int admin_select_role_totalSize() {
		return adminDao.admin_select_role_totalSize();
	}

	@Override
	public List<?> admin_select_role(int i, int parseInt) {
		return adminDao.admin_select_role(i,parseInt);
	}

	@Override
	public List<?> admin_select_wordMessage(int i, int parseInt) {
		return adminDao.admin_select_wordMessage(i,parseInt);
	}

	@Override
	public int admin_select_wordMessage_totalSize() {
		return adminDao.admin_select_wordMessage_totalSize();
	}

	@Override
	public int admin_select_article_totalSize() {
		return adminDao.admin_select_article_totalSize();
	}

	@Override
	public List<?> admin_select_article(int i, int parseInt) {
		return adminDao.admin_select_article(i,parseInt);
	}

	@Override
	public int admin_select_user_totalSize() {
		return adminDao.admin_select_user_totalSize();
	}

	@Override
	public List<?> admin_select_user(int i, int parseInt) {
		return adminDao.admin_select_user(i,parseInt);
	}

	@Override
	public List<?> admin_select_myphoto(int i, int parseInt) {
		return adminDao.admin_select_myphoto(i,parseInt);
	}
}
