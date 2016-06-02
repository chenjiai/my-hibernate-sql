package com.cja.app.dao;

import com.cja.app.model.Account;

public interface AccountDao {
	Account addAccount(Account account);
	Account updateAccount(Account account);
	void deleteAccount(Account account);
}
