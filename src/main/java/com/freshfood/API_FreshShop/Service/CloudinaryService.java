package com.freshfood.API_FreshShop.Service;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import javax.annotation.Resource;

import com.freshfood.API_FreshShop.Entity.InfoUser;
import com.freshfood.API_FreshShop.Entity.Product;
import com.freshfood.API_FreshShop.Repository.AccountRepository;
import com.freshfood.API_FreshShop.Repository.InfoUserRepository;
import com.freshfood.API_FreshShop.Repository.ProductRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Documentation: https://cloudinary.com/documentation/java_integration -
 * https://cloudinary.com/documentation/image_transformations
 *
 * @author Francesco Galgani
 */
@Service
public class CloudinaryService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Cloudinary cloudinary = Singleton.getCloudinary();
    private final String LINK_URL_USER = "https://res.cloudinary.com/dfgichnzh/image/upload/v1681925175/";
    private final String LINK_URL_PRODUCT = "https://res.cloudinary.com/dfgichnzh/image/upload/v1681926791/";

    @Autowired
    InfoUserRepository repository;

    @Autowired
    ProductRepository productRepository;

    public InfoUser uploadAvatar(Long id, MultipartFile file) {
        InfoUser  user = repository.findOne(id);
        if (user != null)
        {
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("public_id","FreshFood/user/"+user.getEmail()));
                String publicId = uploadResult.get("public_id").toString();
                user.setAvatar(LINK_URL_USER+publicId);
                return user;
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                return null;
            }
        }
        else {
            return null;
        }
    }

    public Product uploadProduct(Long id, MultipartFile file) {
        Product product = productRepository.findOne(id);
        if (product != null)
        {
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("public_id","FreshFood/product/"+product.getName()));
                String publicId = uploadResult.get("public_id").toString();
                product.setImage(LINK_URL_PRODUCT+publicId);
                return product;
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                return null;
            }
        }
        else {
            return null;
        }
    }

    public ResponseEntity<ByteArrayResource> downloadImg(String publicId, int width, int height, boolean isAvatar) {

        logger.info("Requested to download the image file: " + publicId);

        // Generates the URL
        String format = "jpg";
        Transformation transformation = new Transformation().width(width).height(height).crop("fill");
        if (isAvatar) {
            // transformation = transformation.gravity("face").radius("max");
            transformation = transformation.radius("max");
            format = "png";
        }
        String cloudUrl = cloudinary.url().secure(true).format(format)
                .transformation(transformation)
                .publicId(publicId)
                .generate();

        logger.debug("Generated URL of the image to be downloaded: " + cloudUrl);

        try {
            // Get a ByteArrayResource from the URL
            URL url = new URL(cloudUrl);
            InputStream inputStream = url.openStream();
            byte[] out = org.apache.commons.io.IOUtils.toByteArray(inputStream);
            ByteArrayResource resource = new ByteArrayResource(out);

            // Creates the headers
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("content-disposition", "attachment; filename=image.jpg");
            responseHeaders.add("Content-Type", "image/jpeg");

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .contentLength(out.length)
                    // .contentType(MediaType.parseMediaType(mimeType))
                    .body(resource);

        } catch (Exception ex) {
            logger.error("FAILED to download the file: " + publicId);
            return null;
        }
    }
}
