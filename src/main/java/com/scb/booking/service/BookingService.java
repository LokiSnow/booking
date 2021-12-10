package com.scb.booking.service;

import com.scb.booking.model.Booking;

import java.util.List;

/**
 * @author Loki
 * @date 2021-12-08
 */
public interface BookingService {
    /**
     * store a booking. A booking consists of a guest name, a room number, and a date.  
     * @param guestName
     * @param roomNo
     * @param bookingDate
     * @return
     */

    Boolean saveBooking(String guestName, String roomNo, String bookingDate);

    /**
     * find the available rooms on a given date
     * @param bookingDate
     * @return
     */
    List<Integer> findAvailableRooms(String bookingDate);

    /**
     * find all the bookings for a given guest
     * @param guestName
     * @return
     */
    List<Booking> findGuestBookings(String guestName);
}
