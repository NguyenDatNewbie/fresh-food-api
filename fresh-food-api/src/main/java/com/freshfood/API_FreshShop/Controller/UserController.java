package com.freshfood.API_FreshShop.Controller;

import com.freshfood.API_FreshShop.Entity.Account;
import com.freshfood.API_FreshShop.Entity.InfoUser;
import com.freshfood.API_FreshShop.Entity.Orders;
import com.freshfood.API_FreshShop.Entity.ResponseObject;
import com.freshfood.API_FreshShop.Repository.AccountRepository;
import com.freshfood.API_FreshShop.Repository.InfoUserRepository;
import com.freshfood.API_FreshShop.Repository.OrderRepository;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/fresh_shop/user")
public class UserController {
    @Autowired
    AccountRepository repository;

    @Autowired
    InfoUserRepository infoUserRepository;

    @Autowired
    OrderRepository orderRepository;
    @GetMapping("/{id}")
    Account getAccount(@PathVariable Long id){
        return repository.findOne(id);
    }
    @PostMapping(path="/new",consumes = {MediaType.ALL_VALUE})
    ResponseEntity<ResponseObject> createAccount(@RequestParam String username,@RequestParam String password,@RequestParam String name,
                                                 @RequestParam String email){
    String message="";

    Account account = repository.findByUsername(username);
    if(account!=null)
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new ResponseObject("failed","username đã tồn tại","")
        );
    account = new Account();
    account.setUsername(username);
    account.setPassword(password);
    account.setRole(false); // Default user
    Account checkAccount = repository.save(account);
    if(checkAccount!=null) {
        InfoUser infoUser = new InfoUser();
        infoUser.setEmail(email);
        infoUser.setName(name);
        infoUser.setAccount(checkAccount);
        try {
            InfoUser result = infoUserRepository.save(infoUser);
            Orders orders = new Orders();
            orders.setComplete(false);
            orders.setUser(result);
            orderRepository.save(orders);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success", "Tạo tài khoản thành công", result));
        }
        catch(Exception exception){
            repository.delete(checkAccount.getId());
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed", "Khởi tạo không thành công", "")
            );
        }
    }

    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
            new ResponseObject("failed", "Khởi tạo không thành công", "")
    );
    }

    @PostMapping(path="/login",consumes = {MediaType.ALL_VALUE})
    ResponseEntity<ResponseObject> login(@RequestParam("username") String username,@RequestParam("password") String password){
        Account a = repository.findByUsernamePass(username,password);
        if(a != null){
           return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success","Đăng nhập thành công",infoUserRepository.findByAccount(a))
            );
        }
        return  ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new ResponseObject("failed","Tài khoản hoặc mật khẩu của bạn không đúng","")
        );
    }

    @PutMapping(path = "/changePassword",consumes = {MediaType.ALL_VALUE})
    ResponseEntity<ResponseObject> changePass(@RequestParam Long id,@RequestParam String newPass){
        Account account = repository.findOne(id);
        if(account.getPassword()==newPass)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("failed","Mật khẩu mới trùng mật khẩu cũ","")
            );
        account.setPassword(newPass);
        repository.save(account);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("success","Đổi mât khẩu thành công",infoUserRepository.findByAccount(account))
        );
    }

    @PutMapping(path = "/new",consumes = {MediaType.ALL_VALUE})
    InfoUser changeNameAndEmail(@RequestParam Long id, @RequestParam String name, @RequestParam String email){
        InfoUser infoUser = infoUserRepository.findOne(id);
        infoUser.setName(name);
        infoUser.setEmail(email);
        return infoUserRepository.save(infoUser);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> delete(@PathVariable Long id){
        Account account = repository.findOne(id);
        if(account==null)
            return  ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed","Không tìm thấy tài khoản trên","")
            );
        try {
            infoUserRepository.delete(account);
            repository.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success","Xóa tài khoản thành công","")
            );
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("failed", ex.getMessage(), "")
            );
        }
    }
}
