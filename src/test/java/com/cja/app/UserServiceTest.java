package com.cja.app;

import javax.annotation.Resource;

import org.junit.Test;

import com.cja.app.service.UserService;

public class UserServiceTest extends BaseTest {
	
	@Resource(name="userService")
	private UserService userServce;
	@Test
	public void addUser(){
		userServce.addUser();
	}
}
