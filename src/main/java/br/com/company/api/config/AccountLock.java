package br.com.company.api.config;

import java.util.concurrent.locks.ReentrantLock;

public class AccountLock extends ReentrantLock {

	private static final long serialVersionUID = 2524883331307318217L;
	
	private String accountLockId;

	public AccountLock(String accountLockId) {
		this.accountLockId = accountLockId;
	}

	public String getAccountLockId() {
		return accountLockId;
	}

	public void setAccountLockId(String accountLockId) {
		this.accountLockId = accountLockId;
	}
}
