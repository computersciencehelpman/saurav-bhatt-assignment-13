package com.coderscampus.assignment13.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coderscampus.assignment13.ResourceNotFoundException;
import com.coderscampus.assignment13.domain.Account;
import com.coderscampus.assignment13.domain.User;
import com.coderscampus.assignment13.repository.AccountRepository;

@Service
public class AccountService {

	private final AccountRepository accountRepo;
	private final UserService userService;
	
	public AccountService(AccountRepository accountRepo, UserService userService) {
		super();
		this.accountRepo = accountRepo;
		this.userService = userService;
	}

	@Transactional
    public Account createAccountForUser(Long userId, String accountName) {
        User user = userService.findById(userId); 
        Account account = new Account();
        account.setAccountName(accountName);
        account.setUser(user); 

        user.getAccounts().add(account);  
        return accountRepo.save(account); 
    }

    @Transactional
    public void saveAccountForUser(User user, Account account) {
       
        account.setUser(user); 
        user.getAccounts().add(account);

        accountRepo.save(account); 
        userService.saveUser(user);       
    }

    @Transactional
    public void saveOrUpdateAccount(Long userId, Account account) {
        User user = userService.findById(userId); 
        Optional<Account> existingAccountOpt = user.getAccounts().stream()
                .filter(a -> a.getAccountId().equals(account.getAccountId()))
                .findFirst();
        
        if (existingAccountOpt.isPresent()) {
            Account existingAccount = existingAccountOpt.get();
            existingAccount.setAccountName(account.getAccountName());
        } else {
            account.getUser();
            user.getAccounts().add(account);
        }
        
        accountRepo.save(account);
    }

    public Long getLastAccountId() {
        return accountRepo.findMaxAccountId(); 
    }

    public Account findByAccountId(Long accountId) {
        return accountRepo.findById(accountId).orElseThrow(() -> 
            new ResourceNotFoundException("Account not found for ID: " + accountId));
    }

}
