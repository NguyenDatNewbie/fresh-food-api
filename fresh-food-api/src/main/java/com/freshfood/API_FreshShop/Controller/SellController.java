package com.freshfood.API_FreshShop.Controller;

import com.freshfood.API_FreshShop.Repository.InfoUserRepository;
import com.freshfood.API_FreshShop.Repository.InventoryRepository;
import com.freshfood.API_FreshShop.Service.IPaymentService;
import com.freshfood.API_FreshShop.Service.Impl.PaymentService;
import com.freshfood.API_FreshShop.Entity.*;
import com.freshfood.API_FreshShop.Repository.OrderItemRepository;
import com.freshfood.API_FreshShop.Repository.OrderRepository;
import com.mysql.cj.log.Log;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/fresh_shop/sell")
public class SellController {
    @Autowired
    OrderItemRepository repository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    InfoUserRepository infoUserRepository;

    @GetMapping("/cart/{user_id}")
    ResponseEntity<ResponseObject> getCart(@PathVariable Long user_id){
        Orders order = orderRepository.findByUser(user_id);
        if(order==null)
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed","Không tìm thấy user","")
            );
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("success","Xem giỏ hàng thành công",repository.getByOrder(order))
        );
    }

    @PostMapping("check")
    ResponseEntity<ResponseObject> checkProductInInventory(@RequestBody ProductQuantity productQuantity){
        List<Long> idInventory = new ArrayList<>();
        List<Integer> quantityInventory = new ArrayList<>();

        List<Long> idProductFail = new ArrayList<>();
        List<Integer> quantityProduct  = new ArrayList<>();
        Boolean isTrue = true;
        for(int i = 0;i<productQuantity.getId().size();i++){
            List<Inventory> check = inventoryRepository.getProductInInventory(productQuantity.getId().get(i),productQuantity.getQuantity().get(i));
            if(check.size()>0)
            {
                idInventory.add(check.get(0).getId());
                quantityInventory.add(productQuantity.getQuantity().get(i));
            }
            else{
                isTrue=false;
                idProductFail.add(productQuantity.getId().get(i));
                quantityProduct.add(productQuantity.getQuantity().get(i));
            }
        }
        ProductQuantity result = new ProductQuantity();
        if(isTrue==true){
            result.setId(idInventory);
            result.setQuantity(quantityInventory);
            return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("success", "Sản phẩm và số lượng đủ trong kho", result)
            );
        }
        result.setId(idProductFail);
        result.setQuantity(quantityProduct);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("failed", "Sản phẩm và số lượng này kho không thể đáp ứng", result)
        );
    }

//    @PostMapping("/cart/{user_id}/{productId}/{quantity}")
//    ResponseEntity<ResponseObject> addItemCart(@RequestBody Long user_id,@PathVariable int quantity, @PathVariable Long productId){
//        Orders order = orderRepository.findByUser(user_id);
//        if(order==null)
//            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
//                    new ResponseObject("failed","Không tìm thấy user","")
//            );
//        try {
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrders(order);
//            orderItem.setQuantity(quantity);
//            List<Inventory> list = inventoryRepository.getProductInInventory(productId);
//            if(list.size()>0) {
//                Inventory inventory= list.get(0);
//                orderItem.setInventory(inventory);
//            }
//            else ResponseEntity.status(HttpStatus.CREATED).body(
//                    new ResponseObject("success","Không tìm thấy sản phẩm "+productId.toString() +"trong kho","")
//            );
//            return ResponseEntity.status(HttpStatus.CREATED).body(
//                    new ResponseObject("success","Thêm vào giỏ hàng thành công",repository.save(orderItem))
//            );
//        }
//        catch(Exception ex) {
//            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
//                    new ResponseObject("failed", ex.getMessage(), "")
//            );
//        }
//    }

    @PutMapping("/cart")
    List<OrderItem> updateCartItem(@RequestBody OrderItem orderItem, @RequestBody Orders orders)
    {
        repository.save(orderItem);
        return repository.getByOrder(orders);
    }

//     Xử lý thanh toán
    @PostMapping("/payment/{user_id}/{phone}/{address}/{totalPrice}")
    Orders payment(@RequestBody ProductQuantity list,
                   @PathVariable Long user_id, @PathVariable String phone,@PathVariable String address,@PathVariable BigDecimal totalPrice){
        List<Long> inventories = list.getId();
        List<Integer> quantity = list.getQuantity();
        Orders orders = orderRepository.findByUser(user_id);
        addInventoryToOrderItem(orders,inventories,quantity);
        orders.setAddress(address);
        orders.setPhone(phone);
        orders.setComplete(true);
        orders.setTotal_price(totalPrice);
//        orders.setPaymentComplete();
        long millis=System.currentTimeMillis();   java.sql.Date date=new java.sql.Date(millis);
        orders.setPaymentComplete(date);

        Orders newOrder = new Orders();
        InfoUser user = infoUserRepository.findOne(user_id);
        newOrder.setUser(user);
        newOrder.setComplete(false);
        orderRepository.save(newOrder);
        return orderRepository.save(orders);
    }

    void addInventoryToOrderItem(Orders orders,List<Long> inventories,List<Integer> quantityList){
        for(int i =0;i<inventories.size();i++) {
            Inventory inventory = inventoryRepository.findOne(inventories.get(i));
            Integer quantity = quantityList.get(i);
            List<OrderItem> orderItems = repository.checkExits(orders, inventory);
            OrderItem orderItem = new OrderItem();
            if(orderItems.size()>0){
                orderItem = orderItems.get(0);
                orderItem.setQuantity(quantity);
            }
            else {
                orderItem.setOrders(orders);
                orderItem.setInventory(inventory);
                orderItem.setQuantity(quantity);
            }
            repository.save(orderItem);
            int temp = inventory.getQuantity()-quantity;
            if(temp==0)
                inventoryRepository.delete(inventory.getId());
            else
            {
                inventory.setQuantity(temp);
                inventoryRepository.save(inventory);
            }
        }
    }
}
