package com.coderscampus.assignment13.domain;

import javax.persistence.*;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;
    @Column(length = 100)
    private String accountName;
    @ManyToOne
    @JoinColumn(name = "user_id") 
    private User user;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", accountName=" + accountName + ", user=" + user + "]";
	}

}
