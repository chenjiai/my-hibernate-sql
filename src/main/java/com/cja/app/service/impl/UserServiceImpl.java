package com.cja.app.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cja.app.dao.AccountDao;
import com.cja.app.dao.UserDao;
import com.cja.app.model.Account;
import com.cja.app.model.User;
import com.cja.app.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Resource(name="userDao")
	private UserDao userDao;
	@Resource(name="accountDao")
	private AccountDao accountDao;
	
	@Transactional(propagation=Propagation.REQUIRED)
	public void addUser() {
		User user = new User();
		user.setUserName("cja1");
		user.setAge(32);
		user.setAmount(new BigDecimal(99999999.99));
		user.setStatus("1");
		user.setCreateTime(new Date());
		user.setUpdateTime(new Date());
		userDao.addUser(user);
		
		Account account = new Account();
		account.setAccountName("测试账户名称");
		accountDao.addAccount(account);
	}

}
