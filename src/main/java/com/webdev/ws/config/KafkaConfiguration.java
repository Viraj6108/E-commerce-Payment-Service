package com.webdev.ws.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.web.client.RestTemplate;

import com.webdev.ws.commands.PaymentProceedCommand;
import com.webdev.ws.errors.NotRetryableException;
import com.webdev.ws.errors.RetryableException;
import com.webdev.ws.events.PaymentProcessEvent;

@Configuration
public class KafkaConfiguration {

	@Autowired
	Environment env;
	
	@Bean
	ConsumerFactory<String, Object> consumerFactory()
	{
		Map<String, Object> config = new HashMap<>();
		
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("spring.kafka.consumer.bootstrap-servers"));
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
		config.put(JsonDeserializer.TRUSTED_PACKAGES, env.getProperty("spring.kafka.consumer.properties.spring.json.trusted-packages"));
		config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
		config.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("spring.kafka.consumer.group-id"));
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, env.getProperty("spring.kafka.consumer.auto-offset-reset"));
		return new DefaultKafkaConsumerFactory<>(config);
	}
	
	@Bean
	ConcurrentKafkaListenerContainerFactory<String, PaymentProcessEvent>concurrentKafkaListenerContainerFactory(KafkaTemplate<String, Object>kafkaTemplate
			,ConsumerFactory<String, Object>consumerFactory)
	{
		DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
		FixedBackOff backOff = new FixedBackOff(5000L,3);
		DefaultErrorHandler handler = new DefaultErrorHandler(recoverer,backOff);
		handler.addNotRetryableExceptions(NotRetryableException.class);
		handler.addRetryableExceptions(RetryableException.class);
		
		ConcurrentKafkaListenerContainerFactory<String, PaymentProcessEvent>factory = new  ConcurrentKafkaListenerContainerFactory<>();
		factory.setCommonErrorHandler(handler);
		factory.setConsumerFactory(consumerFactory);
		
		return factory;
	}
	
	@Bean
	RestTemplate restTemplate()
	{
		return new RestTemplate();
	}
	
	@Bean
	ProducerFactory<String, Object> producerFactory()
	{
		Map<String, Object> config = new HashMap<>();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9096");
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		
		return new DefaultKafkaProducerFactory<>(config);
	}
	
	@Bean
	KafkaTemplate<String, Object> kafkaTemplate()
	{
		return new KafkaTemplate<>(producerFactory());
	}
}
