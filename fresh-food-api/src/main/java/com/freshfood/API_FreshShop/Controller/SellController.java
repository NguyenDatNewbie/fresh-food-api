package com.freshfood.API_FreshShop.Controller;

import com.freshfood.API_FreshShop.Repository.InventoryRepository;
import com.freshfood.API_FreshShop.Service.IPaymentService;
import com.freshfood.API_FreshShop.Service.Impl.PaymentService;
import com.freshfood.API_FreshShop.Entity.*;
import com.freshfood.API_FreshShop.Repository.OrderItemRepository;
import com.freshfood.API_FreshShop.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/cart/{user_id}/{productId}/{quantity}")
    ResponseEntity<ResponseObject> addItemCart(@PathVariable Long user_id,@PathVariable int quantity, @PathVariable Long productId){
        Orders order = orderRepository.findByUser(user_id);
        if(order==null)
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed","Không tìm thấy user","")
            );
        try {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrders(order);
            orderItem.setQuantity(quantity);
            List<Inventory> list = inventoryRepository.getProductInInventory(productId);
            if(list.size()>0) {
                Inventory inventory= list.get(0);
                orderItem.setInventory(inventory);
            }
            else ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObject("success","Không tìm thấy sản phẩm "+productId.toString() +"trong kho","")
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObject("success","Thêm vào giỏ hàng thành công",repository.save(orderItem))
            );
        }
        catch(Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed", ex.getMessage(), "")
            );
        }
    }

    @PutMapping("/cart")
    List<OrderItem> updateCartItem(@RequestBody OrderItem orderItem, @RequestBody Orders orders)
    {
        repository.save(orderItem);
        return repository.getByOrder(orders);
    }

//     Xử lý thanh toán
    @PostMapping("/payment")
    Orders payment(@RequestBody Orders orders){
        IPaymentService service = new PaymentService();
        List<OrderItem> orderItem = repository.getByOrder(orders);
        orders.setComplete(true);
        orders.setTotal_price(service.totalPrice(orderItem));
        return orderRepository.save(orders);
    }
}
