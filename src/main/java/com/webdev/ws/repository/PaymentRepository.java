package com.webdev.ws.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webdev.ws.model.PaymentModel;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentModel, Integer>{

}
