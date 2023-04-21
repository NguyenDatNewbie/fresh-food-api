package com.freshfood.API_FreshShop.Entity;


import java.util.*;

import javax.persistence.*;

@Entity
@Table
public class Category {
    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64)
    private String name;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParent() {
        if(parent!=null)
            return parent.getId();
        return (long) -1;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public int getChildren() {
        return children.size();
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public void addChild(Category children) {
        this.children.add(children);
    }
}
