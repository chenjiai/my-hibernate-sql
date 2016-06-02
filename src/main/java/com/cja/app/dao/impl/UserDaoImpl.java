package com.cja.app.dao.impl;

import org.springframework.stereotype.Service;

import com.cja.app.dao.BaseDao;
import com.cja.app.dao.UserDao;
import com.cja.app.model.User;

@Service("userDao")
public class UserDaoImpl extends BaseDao implements UserDao {

	public User addUser(User user) {
		super.getHibernateTemplate().saveOrUpdate(user);
		return user;
	}

	public User updateUser(User user) {
		super.getHibernateTemplate().saveOrUpdate(user);
		return user;
	}

	public void deleteUser(User user) {
		super.getHibernateTemplate().delete(user);
	}

}
