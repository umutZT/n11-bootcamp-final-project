package com.bootcamp.stockservice.repository;

import com.bootcamp.stockservice.entity.ReservationStatus;
import com.bootcamp.stockservice.entity.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {

    List<StockReservation> findByOrderId(Long orderId);

    List<StockReservation> findByOrderIdAndStatus(Long orderId, ReservationStatus status);
}
