package com.freshfood.API_FreshShop.Entity;


import javax.persistence.*;
import javax.sound.sampled.Line;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Orders {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="order_id")
	Long id;
	@Column()
	String address;
	@Column()
	String phone;
	@Column()
	int status;
	@Column()
	BigDecimal total_price;
	@Column()
	Boolean complete;
	@Column()
	Date paymentComplete;

	@ManyToOne()
	@JoinColumn(name = "user_id",nullable = false)
	InfoUser user;

	public Boolean getComplete() {
		return complete;
	}

	public void setComplete(Boolean complete) {
		this.complete = complete;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InfoUser getUser() {
		return user;
	}

	public void setUser(InfoUser user) {
		this.user = user;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public BigDecimal getTotal_price() {
		return total_price;
	}

	public void setTotal_price(BigDecimal total_price) {
		this.total_price = total_price;
	}

	public Date getPaymentComplete() {
		return paymentComplete;
	}

	public void setPaymentComplete(Date paymentComplete) {
		this.paymentComplete = paymentComplete;
	}
}
