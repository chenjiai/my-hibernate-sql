package com.cja.app.dao.impl;

import org.springframework.stereotype.Service;

import com.cja.app.dao.AccountDao;
import com.cja.app.dao.BaseDao;
import com.cja.app.model.Account;

@Service("accountDao")
public class AccountDaoImpl extends BaseDao implements AccountDao {

	public Account addAccount(Account account) {
		super.getHibernateTemplate().saveOrUpdate(account);
		return account;
	}

	public Account updateAccount(Account account) {
		super.getHibernateTemplate().saveOrUpdate(account);
		return account;
	}

	public void deleteAccount(Account account) {
		super.getHibernateTemplate().delete(account);
	}

}
