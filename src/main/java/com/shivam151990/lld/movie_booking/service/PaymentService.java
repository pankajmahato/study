package com.shivam151990.lld.movie_booking.service;

import java.util.UUID;

public interface PaymentService {
    boolean processPayment(String userId, double amount, UUID bookingId);
}
