package com.coderscampus.assignment13.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import com.coderscampus.assignment13.domain.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("SELECT MAX(a.accountId) FROM Account a")
    Long findMaxAccountId();
    
}
