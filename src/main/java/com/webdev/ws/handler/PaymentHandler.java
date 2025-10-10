package com.webdev.ws.handler;


import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.webdev.ws.events.PaymentProcessEvent;
import com.webdev.ws.model.PaymentModel;
import com.webdev.ws.repository.PaymentRepository;

@Component
@KafkaListener(topics = "payment-command", groupId = "payment-group", containerFactory = "concurrentKafkaListenerContainerFactory")
public class PaymentHandler {

	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	RestTemplate restTemplate;
	
	private PaymentRepository repository;
	public PaymentHandler(RestTemplate restTemplate,
			PaymentRepository repository)
	{
		this.restTemplate = restTemplate;
		this.repository = repository;
		
	}
	

	@KafkaHandler
	public void handlePaymetProcessCommand(@Payload PaymentProcessEvent command)
	{	 
		logger.info("At handler method");
		FakePaymentDetails details =new FakePaymentDetails(20000,"INR","CARD","475864898886","12/30","123");
		ResponseEntity<FakePaymentDetails>result = restTemplate.getForEntity("http://localhost:8082/response/post",FakePaymentDetails.class, details);
		logger.info("result"+result.getStatusCode());
		if(!result.getStatusCode().equals(null))
		{
			PaymentModel model = new PaymentModel();
			model.setAmount(details.getAmount());
			model.setPaymentMethod(details.getMethod());
			model.setTransactionId(UUID.randomUUID());
			repository.save(model);
			
		}
		
	}
	@KafkaHandler(isDefault = true)
    public void handleUnknown(Object unknown) {
        System.out.println("Unknown type: " + unknown.getClass());
    }

}
