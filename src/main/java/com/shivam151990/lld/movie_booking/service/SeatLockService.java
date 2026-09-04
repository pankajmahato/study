package com.shivam151990.lld.movie_booking.service;

import com.shivam151990.lld.movie_booking.model.Seat;
import com.shivam151990.lld.movie_booking.model.SeatLock;
import com.shivam151990.lld.movie_booking.model.SeatStatus;
import com.shivam151990.lld.movie_booking.model.Show;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SeatLockService {

    private final Map<Show, Map<Seat, SeatLock>> seatLocks;
    private final Map<Show, Map<Seat, SeatStatus>> seatStatuses;

    public SeatLockService() {
        seatLocks = new ConcurrentHashMap<>();
        seatStatuses = new ConcurrentHashMap<>();
    }

    private Map<Seat, SeatStatus> getSeatStatusMap(Show show) {
        return seatStatuses.computeIfAbsent(show, k -> new ConcurrentHashMap<>());
    }

    public boolean lockSeats(Show show, String userId, List<Seat> seats) {
        Map<Seat, SeatStatus> statusMap = getSeatStatusMap(show);
        synchronized (this) {
            for (Seat seat : seats) {
                SeatStatus status = statusMap.getOrDefault(seat, SeatStatus.AVAILABLE);
                if (status != SeatStatus.AVAILABLE) {
                    return false;
                }
            }
            for (Seat seat : seats) {
                statusMap.put(seat, SeatStatus.TEMPORARILY_UNAVAILABLE);
                Map<Seat, SeatLock> showSeatLocks = seatLocks.computeIfAbsent(show, k -> new ConcurrentHashMap<>());
                showSeatLocks.put(seat, new SeatLock(seat, show, userId, LocalDateTime.now()));
            }
            return true;
        }
    }

    public boolean isSeatLocked(Show show, Seat seat) {
        Map<Seat, SeatLock> showSeatLocks = seatLocks.getOrDefault(show, Collections.emptyMap());
        SeatLock lock = showSeatLocks.get(seat);
        return lock != null && !lock.isExpired();
    }

    public void unlockSeats(Show show, List<Seat> seats) {
        Map<Seat, SeatStatus> statusMap = getSeatStatusMap(show);
        synchronized (this) {
            for (Seat seat : seats) {
                Map<Seat, SeatLock> showSeatLocks = seatLocks.get(show);
                if (showSeatLocks != null) {
                    showSeatLocks.remove(seat);
                }
                statusMap.put(seat, SeatStatus.AVAILABLE);
            }
        }
    }

    public void markSeatsAsBooked(Show show, List<Seat> seats) {
        Map<Seat, SeatStatus> statusMap = getSeatStatusMap(show);
        synchronized (this) {
            for (Seat seat : seats) {
                statusMap.put(seat, SeatStatus.PERMANENTLY_BOOKED);
                Map<Seat, SeatLock> showSeatLocks = seatLocks.get(show);
                if (showSeatLocks != null) {
                    showSeatLocks.remove(seat);
                }
            }
        }
    }

    public boolean validateLock(Show show, Seat seat, String userId) {
        Map<Seat, SeatLock> showSeatLocks = seatLocks.getOrDefault(show, Collections.emptyMap());
        SeatLock lock = showSeatLocks.get(seat);
        return lock != null && lock.getUserId().equals(userId) && !lock.isExpired();
    }
}
