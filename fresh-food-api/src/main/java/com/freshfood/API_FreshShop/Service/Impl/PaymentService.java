package com.freshfood.API_FreshShop.Service.Impl;

import com.freshfood.API_FreshShop.Entity.Inventory;
import com.freshfood.API_FreshShop.Repository.InfoUserRepository;
import com.freshfood.API_FreshShop.Service.IPaymentService;
import com.freshfood.API_FreshShop.Entity.OrderItem;
import com.freshfood.API_FreshShop.Entity.Product;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

public class PaymentService implements IPaymentService {
    @Override
    public BigDecimal totalPrice(List<OrderItem> list){
        BigDecimal total = new BigDecimal(0);
        for(int i=0; i<list.size();i++)
        {
            Product product = list.get(i).getInventory().getProduct();
            BigDecimal quantity = new BigDecimal(list.get(i).getQuantity());
            BigDecimal nhan = product.getPrice().multiply(quantity);
            if(product.getPromotion()>0)
                nhan = nhan.multiply(new BigDecimal(product.getPromotion()));
            total = total.add(nhan);
        }
        return total;
    }


}
