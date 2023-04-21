package com.freshfood.API_FreshShop.Controller;

import com.freshfood.API_FreshShop.Entity.InfoUser;
import com.freshfood.API_FreshShop.Entity.Product;
import com.freshfood.API_FreshShop.Repository.InfoUserRepository;
import com.freshfood.API_FreshShop.Repository.ProductRepository;
import com.freshfood.API_FreshShop.Service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/fresh_shop/upload_images")
public class UploadImageController {

    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    InfoUserRepository repository;

    @Autowired
    ProductRepository productRepository;


    @PostMapping("/fileupload/{option}/{id}")
    public ResponseEntity<?> uploadImageToFIleSystem(@RequestParam("file") MultipartFile file,
                                                     @PathVariable Long id,
                                                     @PathVariable String option) throws IOException {
        if(option.compareTo("user") == 0) {
            InfoUser infoUser = cloudinaryService.uploadAvatar(id,file);
            if(infoUser!=null)
            return ResponseEntity.status(HttpStatus.OK)
                    .body(repository.save(infoUser));
        }
        else if(option.compareTo("product")==0) {
            Product product = cloudinaryService.uploadProduct(id,file);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(productRepository.save(product));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("option không hợp lệ");
    }

}
