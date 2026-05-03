package com.bootcamp.orderservice.repository;

import com.bootcamp.orderservice.entity.Order;
import com.bootcamp.orderservice.entity.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUsernameOrderByCreatedAtDesc(String username);

    Optional<Order> findByIdAndUsername(Long id, String username);

    List<Order> findBySagaStatus(SagaStatus status);
}
