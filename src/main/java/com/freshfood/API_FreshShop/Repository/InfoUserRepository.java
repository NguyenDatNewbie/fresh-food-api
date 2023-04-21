package com.freshfood.API_FreshShop.Repository;

import com.freshfood.API_FreshShop.Entity.Account;
import com.freshfood.API_FreshShop.Entity.InfoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


public interface InfoUserRepository extends JpaRepository<InfoUser, Account> {
    @Query("select i from InfoUser i where i.account =?1")
    InfoUser findByAccount(Account account);

    @Query("select i from InfoUser i where i.account.id=?1")
    InfoUser findOne(Long account_id);
}
