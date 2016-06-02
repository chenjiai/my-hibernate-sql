package com.cja.app.dao;

import com.cja.app.model.User;

public interface UserDao {
	User addUser(User user);
	User updateUser(User user);
	void deleteUser(User user);
}
