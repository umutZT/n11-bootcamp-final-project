package com.bootcamp.stockservice.service;

import com.bootcamp.stockservice.client.ProductClient;
import com.bootcamp.stockservice.dto.MessageResponse;
import com.bootcamp.stockservice.dto.ReservationResponse;
import com.bootcamp.stockservice.dto.ReserveStockItem;
import com.bootcamp.stockservice.dto.ReserveStockRequest;
import com.bootcamp.stockservice.dto.ReserveStockResponse;
import com.bootcamp.stockservice.entity.ReservationStatus;
import com.bootcamp.stockservice.entity.StockReservation;
import com.bootcamp.stockservice.exception.ReservationNotFoundException;
import com.bootcamp.stockservice.repository.StockReservationRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockReservationService {

    private static final Logger log = LoggerFactory.getLogger(StockReservationService.class);

    private final StockReservationRepository reservationRepository;
    private final ProductClient productClient;

    public StockReservationService(StockReservationRepository reservationRepository,
                                   ProductClient productClient) {
        this.reservationRepository = reservationRepository;
        this.productClient = productClient;
    }

    @Transactional
    public ReserveStockResponse reserveStock(ReserveStockRequest request) {
        Long orderId = request.getOrderId();
        List<StockReservation> successful = new ArrayList<>();

        for (ReserveStockItem item : request.getItems()) {
            try {
                productClient.decreaseStock(item.getProductId(), item.getQuantity());
            } catch (FeignException e) {
                log.warn("decreaseStock failed for product {} (order {}): status={}, body={}",
                        item.getProductId(), orderId, e.status(), e.contentUTF8());
                rollback(successful, orderId);
                String reason = e.status() == 409
                        ? "Insufficient stock for product " + item.getProductId()
                        : "Stock decrease failed for product " + item.getProductId() + ": " + e.getMessage();
                return new ReserveStockResponse(orderId, mapToResponses(successful), false, reason);
            } catch (Exception e) {
                log.error("Unexpected error reserving product {} (order {}): {}",
                        item.getProductId(), orderId, e.getMessage());
                rollback(successful, orderId);
                return new ReserveStockResponse(orderId, mapToResponses(successful), false,
                        "Unexpected failure: " + e.getMessage());
            }

            StockReservation reservation = new StockReservation(
                    orderId, item.getProductId(), item.getQuantity(), ReservationStatus.PENDING);
            successful.add(reservationRepository.save(reservation));
        }

        return new ReserveStockResponse(orderId, mapToResponses(successful), true, null);
    }

    private void rollback(List<StockReservation> successful, Long orderId) {
        for (StockReservation reservation : successful) {
            try {
                productClient.increaseStock(reservation.getProductId(), reservation.getQuantity());
            } catch (Exception e) {
                log.error("Rollback increaseStock failed for product {} (order {}): {}",
                        reservation.getProductId(), orderId, e.getMessage());
            }
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
        }
    }

    @Transactional
    public MessageResponse confirmReservations(Long orderId) {
        List<StockReservation> pending = reservationRepository
                .findByOrderIdAndStatus(orderId, ReservationStatus.PENDING);
        if (pending.isEmpty()) {
            throw new ReservationNotFoundException("No pending reservations for order " + orderId);
        }
        for (StockReservation reservation : pending) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);
        }
        return new MessageResponse(pending.size() + " reservations confirmed for order " + orderId);
    }

    @Transactional
    public MessageResponse cancelReservations(Long orderId) {
        List<StockReservation> pending = reservationRepository
                .findByOrderIdAndStatus(orderId, ReservationStatus.PENDING);
        if (pending.isEmpty()) {
            return new MessageResponse("No pending reservations to cancel for order " + orderId);
        }
        for (StockReservation reservation : pending) {
            try {
                productClient.increaseStock(reservation.getProductId(), reservation.getQuantity());
            } catch (Exception e) {
                log.error("Compensation increaseStock failed for product {} (order {}): {}",
                        reservation.getProductId(), orderId, e.getMessage());
            }
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
        }
        return new MessageResponse(pending.size() + " reservations cancelled for order "
                + orderId + ", stock restored");
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getByOrderId(Long orderId) {
        return reservationRepository.findByOrderId(orderId).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    private List<ReservationResponse> mapToResponses(List<StockReservation> reservations) {
        return reservations.stream().map(ReservationResponse::from).toList();
    }
}
