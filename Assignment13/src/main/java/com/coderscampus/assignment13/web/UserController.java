package com.coderscampus.assignment13.web;

import java.util.ArrayList;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.coderscampus.assignment13.domain.User;
import com.coderscampus.assignment13.ResourceNotFoundException;
import com.coderscampus.assignment13.domain.Account;
import com.coderscampus.assignment13.domain.Address;
import com.coderscampus.assignment13.service.AccountService;
import com.coderscampus.assignment13.service.AddressService;
import com.coderscampus.assignment13.service.UserService;

@Controller
public class UserController {
	
	private UserService userService;
	private AccountService accountService;
	private AddressService addressService;
	
	public UserController(UserService userService, AccountService accountService, AddressService addressService) {
		super();
		this.userService = userService;
		this.accountService = accountService;
		this.addressService = addressService;
	}

	@GetMapping("/")
	public String index() {
		return "redirect:/users";
	}
	
	@GetMapping("/error")
	public String eror() {
		return "error/404";
	}
	  
	@GetMapping("/details")
    public ResponseEntity<Set<User>> getUsersWithDetails() {
        Set<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
	
	@GetMapping("/register")
	public String getCreateUser(ModelMap model) {
	    User user = new User();
	    user.setAddress(new Address());
	    model.put("user", user);
	    return "register";
	}

	@GetMapping("/users")
	public String getAllUsers(ModelMap model) {
	    Set<User> users = userService.findAll();
	    model.put("users", users);
	    if (users.size() == 1) {
	        model.put("user", users.iterator().next());
	    }
	    return "users";
	}

	@PostMapping("/users/{userId}/accounts/{accountId}/save")
	public String saveAccount(@PathVariable Long userId, 
	                          @PathVariable Long accountId, 
	                          @ModelAttribute Account account) {
	    User user = userService.findById(userId); 
	    account.setUser(user); 
	    accountService.saveAccountForUser(user, account);
	    return "redirect:/users/" + userId;
	}

	@GetMapping("/users/{userId}/accounts")
	public String createAccountForm(@PathVariable("userId") Long userId, Model model, @ModelAttribute Account account) {
	    Long lastAccountId = accountService.getLastAccountId();
	    Long nextAccountId = (lastAccountId != null) ? lastAccountId + 1 : 1; 
	    Account newAccount = new Account();
	    newAccount.setAccountId(nextAccountId);
	    model.addAttribute("userId", userId);
	    model.addAttribute("account", newAccount); 
	    return "account";
	}
	
	@GetMapping("/users/{userId}/accounts/{accountId}")
	public String showAccountDetails(@PathVariable Long userId, @PathVariable Long accountId, Model model) {
		try {
	    User user = userService.findById(userId);
	    Account account = accountService.findByAccountId(accountId);
	    model.addAttribute("user", user);
	    model.addAttribute("userId", userId);
	    model.addAttribute("account", account);
	    return "account";
		} catch (ResourceNotFoundException e) {
			return "error/404";
		}
	}
	
	@PostMapping("/register")
	public String postCreateUser (User user) {
		userService.saveUser(user);
		return "redirect:/register";
	}
	
	@PostMapping("/users/{userId}/update")
	public String updateUser(@PathVariable Long userId, @ModelAttribute User user) {
	    User existingUser = userService.findById(userId);
	    existingUser.setUsername(user.getUsername());
	    existingUser.setName(user.getName());
	    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
	        existingUser.setPassword(user.getPassword());
	    }
	    Address address = user.getAddress();
	    if (address != null) {
	        address.setUser(existingUser);
	        addressService.updateAddress(address); 
	    }
	    userService.saveUser(existingUser);
	    return "redirect:/users";
	}

	@GetMapping("/users/{userId}")
	public String getOneUser(ModelMap model, @PathVariable Long userId) {  
	    try {
	    	User user = userService.findById(userId);
	    	if (user.getAddress() == null) {
		        Address newAddress = new Address();
		        newAddress.setUser(user); 
		        user.setAddress(newAddress);
		    }
		    if (user.getAccounts() == null || user.getAccounts().isEmpty()) {
		        user.setAccounts(new ArrayList<>());
		    }
		    model.addAttribute("user", user);
		    model.addAttribute("accounts", user.getAccounts());
		    model.addAttribute("address", user.getAddress());
		    return "userDetails";
	    } catch (ResourceNotFoundException e) {
	    	return "error/user-not-found";
	    }
	}
	    
	@PostMapping("/users/{userId}")
	public String postOneUser (@PathVariable Long userId, @ModelAttribute User user) {
		user.setUserId(userId); 
		userService.saveUser(user);
		return "redirect:/users/";
	}
	
	@PostMapping("/users/{userId}/delete")
	public String deleteOneUser (@PathVariable Long userId) {
		userService.deleteUser(userId);
		return "redirect:/users";
	}
	
	@PostMapping("/users/{userId}/accounts")
	public String createOrUpdateAccount(@PathVariable Long userId, @ModelAttribute Account account) {
	    if (account.getAccountId() == null) {  
	        accountService.createAccountForUser(userId, account.getAccountName());
	    } else {
	        accountService.saveOrUpdateAccount(userId, account);
	    }
	    return "redirect:/users/" + userId;
	}

	@PostMapping("/users/save")
	public String saveUser(@ModelAttribute User user) {
	    userService.saveUser(user);
	    return "redirect:/users";
	}

	@GetMapping("/users/{userId}/accounts/{accountId}/info")
	public String getOneAccount(ModelMap model, @PathVariable Long userId, @PathVariable Long accountId) {
	    Account account = accountService.findByAccountId(accountId);
	    if (account == null) {
	        return "redirect:/users/" + userId + "/accounts/new"; 
	    }
	    model.addAttribute("account", account);
	    model.addAttribute("userId", userId);
	    return "account";
	}

	@PostMapping("/users/{userId}/accounts/{accountId}")
	public String postOneAccount(@PathVariable Long userId, @PathVariable(required = false) Long accountId, @ModelAttribute Account account) {
	    if (accountId == null || account.getAccountId() == null) { 
	        Account newAccount = accountService.createAccountForUser(userId, account.getAccountName());
	        newAccount.setAccountName(account.getAccountName());
	        accountService.saveOrUpdateAccount(userId, newAccount);
	        return "redirect:/users/" + userId + "/accounts/" + newAccount.getAccountId();
	    } else {
	        Account existingAccount = accountService.findByAccountId(accountId);
	        if (existingAccount != null) {
	            existingAccount.setAccountName(account.getAccountName());
	            accountService.saveOrUpdateAccount(userId,existingAccount);

	        }
	        return "redirect:/users/" + userId + "/accounts/" + accountId;
	    }
	}
}