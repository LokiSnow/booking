package com.scb.booking;

import com.scb.booking.model.Booking;
import com.scb.booking.service.BookingServiceManager;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * test service manage
 * @author Loki
 * @date 2021-12-09
 */
public class BookingManagerTest {
    private int roomNumber;
    @Before
    public void init(){
        roomNumber = 1000;
        System.setProperty("roomNumber", roomNumber+"");
    }

    /**
     * store a booking. A booking consists of a guest name, a room number, and a date.  
     */
    @Test
    public void saveBookingTest(){
        String name = "loki";
        int roomNo = new Random().nextInt(roomNumber);
        String bookingDate = new DateTime().toString("yyyyMMdd");
        String[] args = new String[]{name, String.valueOf(roomNo), bookingDate};
        BookingServiceManager.invoke("saveBooking", new String[]{name, String.valueOf(roomNo), bookingDate});
        List<Integer> rooms = (List<Integer>)BookingServiceManager.invoke("findAvailableRooms",new String[]{bookingDate}).getContent();
        assert !Arrays.asList(rooms).contains(roomNo);
        List<Booking> bookings = (List<Booking>)BookingServiceManager.invoke("findGuestBookings",new String[]{name});
        assert bookings != null && bookings.get(0).getRoomNo() == roomNo;
    }
}
