package com.coderscampus.assignment13.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coderscampus.assignment13.ResourceNotFoundException;
import com.coderscampus.assignment13.domain.Account;
import com.coderscampus.assignment13.domain.Address;
import com.coderscampus.assignment13.domain.User;
import com.coderscampus.assignment13.repository.AccountRepository;
import com.coderscampus.assignment13.repository.AddressRepository;
import com.coderscampus.assignment13.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final AccountRepository accountRepo;
    private final AddressRepository addressRepo;

    @Autowired
    public UserService(UserRepository userRepo, AccountRepository accountRepo, AddressRepository addressRepo) {
        this.userRepo = userRepo;
        this.accountRepo = accountRepo;
        this.addressRepo = addressRepo;
    }

    public User findById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow();
    }

    public Set<User> findAll() {
        return userRepo.findAllUsersWithAccountsAndAddress(); 
    }

    public List<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Transactional
    public void saveUser(User user) {
        userRepo.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> 
            new ResourceNotFoundException("User not found for ID: " + userId));
        
        List<Account> accounts = new ArrayList<>(user.getAccounts()); 
        for (Account account : accounts) {
            account.setUser(null); 
            accountRepo.delete(account); 
        }
        user.getAccounts().clear(); 
      
        if (user.getAddress() != null) {
            addressRepo.delete(user.getAddress());
            user.setAddress(null);
        }

        userRepo.delete(user);
    }

    @Transactional
    public void updateAddress(Address updatedAddress) {
        if (updatedAddress.getUser() == null || updatedAddress.getUser().getUserId() == null) {
            throw new IllegalArgumentException("Address must be linked to a valid user.");
        }

        Long userId = updatedAddress.getUser().getUserId();
        User user = findById(userId);

        Address existingAddress = user.getAddress();
        if (existingAddress == null) {
            updatedAddress.setUser(user);
            user.setAddress(updatedAddress);
            addressRepo.save(updatedAddress);
        } else {
            existingAddress.setAddressLine1(updatedAddress.getAddressLine1());
            existingAddress.setAddressLine2(updatedAddress.getAddressLine2());
            existingAddress.setCity(updatedAddress.getCity());
            existingAddress.setRegion(updatedAddress.getRegion());
            existingAddress.setZipCode(updatedAddress.getZipCode());
            existingAddress.setCountry(updatedAddress.getCountry());
            addressRepo.save(existingAddress);
        }
    }

    
    @Transactional
    public Account createAccountForUser(Long userId, String accountName) {
        User user = findById(userId); 
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
        userRepo.save(user);       
    }

    @Transactional
    public void updateUserAccounts(Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        List<Account> accounts = user.getAccounts();
        accounts.add(new Account());
    }

    @Transactional
    public void saveOrUpdateAccount(Long userId, Account account) {
        User user = findById(userId); 
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

    public Set<User> findAllUsersWithAccountsAndAddress() {
        return userRepo.findAllUsersWithAccountsAndAddress();
    }
}
