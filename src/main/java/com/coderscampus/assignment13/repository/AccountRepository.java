package com.coderscampus.assignment13.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coderscampus.assignment13.domain.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByAccountName(String accountName);
       
    @Query("SELECT a FROM Account a WHERE a.user.userId = :userId")
    List<Account> findAccountsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT MAX(a.accountId) FROM Account a")
    Long findMaxAccountId();
    
}
