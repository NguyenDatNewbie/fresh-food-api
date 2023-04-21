package com.freshfood.API_FreshShop.Repository;

import com.freshfood.API_FreshShop.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    Category findCategoriesByName(String name);

    @Query("select c  from Category c where c.parent=null")
    List<Category> getAllParent();

    @Query("select c from Category c where c.parent.id=?1")
    List<Category> getChildrenByParent(Long parent_id);
}
