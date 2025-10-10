package com.webdev.ws.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="payment-details")
public class PaymentModel {
	@Id
	@GeneratedValue
	private int id;
	private UUID transactionId;
	private double amount;
	private String paymentMethod;
	
	
	
	
	public PaymentModel() {
		
	}
	
	public PaymentModel(int id, UUID transactionId, double amount, String paymentMethod) {
		super();
		this.id = id;
		this.transactionId = transactionId;
		this.amount = amount;
		this.paymentMethod = paymentMethod;
		
	}

	public UUID getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(UUID transactionId) {
		this.transactionId = transactionId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

}
