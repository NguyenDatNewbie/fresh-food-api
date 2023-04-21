package com.freshfood.API_FreshShop.Repository;

import com.freshfood.API_FreshShop.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("select a from Account a where a.username = ?1")
    Account findByUsername(String username);

    @Query("select a from Account  a where  a.username= ?1 and a.password=?2")
    Account findByUsernamePass(String username, String password);
}
