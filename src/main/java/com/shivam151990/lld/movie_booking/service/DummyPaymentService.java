package com.shivam151990.lld.movie_booking.service;

import java.util.UUID;

public class DummyPaymentService implements PaymentService {

    @Override
    public boolean processPayment(String userId, double amount, UUID bookingId) {
        System.out.println("Processing payment of ₹" + amount + " for user " + userId);
        return true;
    }
}
