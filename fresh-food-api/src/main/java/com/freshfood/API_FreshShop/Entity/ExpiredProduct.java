package com.freshfood.API_FreshShop.Entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name="expired_product")
public class ExpiredProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expired_id")
    Long id;

    @ManyToOne()
    @JoinColumn(name = "inventory_id")
    Inventory inventory;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
