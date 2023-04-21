package com.freshfood.API_FreshShop.Entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
public class InfoUser{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "account_id",nullable = false)
    private Account account;
    private String name;

    @Column(nullable = false,unique = true)
    private String email;

    private String avatar;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = this.id;
    }

    public Long getAccount() {
        return account.getId();
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
