package com.webdev.ws.handler;

public class FakePaymentDetails {

	private double amount;
	private String currency;
	private String method;
	private String cardNumber;
	private String expiry;
	private String cvv;
	public FakePaymentDetails(double amount, String currency, String method, String cardNumber, String expiry,
			String cvv) {
		
		this.amount = amount;
		this.currency = currency;
		this.method = method;
		this.cardNumber = cardNumber;
		this.expiry = expiry;
		this.cvv = cvv;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getExpiry() {
		return expiry;
	}
	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}
	public String getCvv() {
		return cvv;
	}
	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

}
