package com.cja.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_account")
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_account", updatable = false)
	public Integer accountId;
	
	@Column(name = "account_name")
	public String accountName;

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

}
