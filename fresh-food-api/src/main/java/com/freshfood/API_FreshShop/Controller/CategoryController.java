package com.freshfood.API_FreshShop.Controller;

import com.freshfood.API_FreshShop.Entity.Category;
import com.freshfood.API_FreshShop.Entity.ResponseObject;
import com.freshfood.API_FreshShop.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fresh_shop/category")
public class CategoryController {
    @Autowired
    CategoryRepository repository;

    @GetMapping("")
    List<Category> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    Category getById(@PathVariable Long id) {
        return repository.findOne(id);
    }

    @GetMapping("/{name}")
    Category getByName(@PathVariable String name) {
        return repository.findCategoriesByName(name);
    }

// comment
    @PostMapping(path = "/new", consumes = {MediaType.ALL_VALUE})
    ResponseEntity<ResponseObject> insert(@RequestBody Category category) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success", "Category create success", repository.save(category))
            );
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed", ex.getMessage(), "")
            );
        }
    }

    @PutMapping("/new")
    ResponseEntity<ResponseObject> update(@RequestBody Category category) {
        Category cate = repository.findOne(category.getId());
        if (cate == null) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed", "Category này không tồn tại", "")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new ResponseObject("success", "Cập nhật category thành công", repository.save(category))
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> delete(@PathVariable Long id) {
        if (!repository.exists(id))
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed", "Category không tồn tại", "")
            );
        repository.delete(id);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new ResponseObject("success", "Category trên đã được xóa", "")
        );
    }
}
