package com.webdev.ws.handler;


import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.webdev.ws.errors.NotRetryableException;
import com.webdev.ws.events.PaymentFailedEvent;
import com.webdev.ws.events.PaymentProcessEvent;
import com.webdev.ws.events.PaymentSuccessfulEvent;
import com.webdev.ws.model.PaymentModel;
import com.webdev.ws.repository.PaymentRepository;

@Component
@KafkaListener(topics = "payment-command", groupId = "payment-group", containerFactory = "concurrentKafkaListenerContainerFactory")
public class PaymentHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;

	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	RestTemplate restTemplate;
	@Value("${payment.successful.event}")
	private String PAYMENT_EVENT;
	
	private PaymentRepository repository;
	public PaymentHandler(RestTemplate restTemplate,
			PaymentRepository repository, KafkaTemplate<String, Object> kafkaTemplate)
	{
		this.restTemplate = restTemplate;
		this.repository = repository;
		this.kafkaTemplate = kafkaTemplate;
		
	}
	

	@KafkaHandler
	public void handlePaymetProcessCommand(@Payload PaymentProcessEvent command)
	{	 
		logger.info("At handler method");
		try {
		FakePaymentDetails details =new FakePaymentDetails(20000,"INR","CARD","475864898886","12/30","123");
		ResponseEntity<String> result = restTemplate.getForEntity("http://localhost:8082/response/200",String.class);
		logger.info("result"+result.getStatusCode());
		if(!result.getStatusCode().equals(null))
		{
			PaymentModel model = new PaymentModel();
			model.setAmount(details.getAmount());
			model.setPaymentMethod(details.getMethod());
			model.setTransactionId(UUID.randomUUID());
			repository.save(model);
			PaymentSuccessfulEvent event  = new PaymentSuccessfulEvent(model.getTransactionId(),
					command.getOrderId(),command.getProductId());
			kafkaTemplate.send(PAYMENT_EVENT,event);
			logger.info("Payment successfull, Sent to topic"+PAYMENT_EVENT);
		}
		}catch(DataAccessException e)
		{
			PaymentFailedEvent failedEvent = new PaymentFailedEvent(command.getProductId()
					,command.getOrderId(),command.getQuantity());
			kafkaTemplate.send(PAYMENT_EVENT,failedEvent);
			throw new NotRetryableException("Not able to update the payment try again later");
		}
	}
	@KafkaHandler(isDefault = true)
    public void handleUnknown(Object unknown) {
        System.out.println("Unknown type: " + unknown.getClass());
    }

}
