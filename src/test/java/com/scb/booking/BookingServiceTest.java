package com.scb.booking;


import com.scb.booking.model.Booking;
import com.scb.booking.service.BookingService;
import com.scb.booking.service.BookingServiceImpl;
import org.apache.commons.lang3.time.StopWatch;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Loki
 * @date 2021-12-08
 */
public class BookingServiceTest {

    private BookingService bookingService;
    private int roomNumber;

    @Before
    public void init(){
        roomNumber = 100;
        System.setProperty("roomNumber", roomNumber+"");
        bookingService = new BookingServiceImpl();
    }

    /**
     * store a booking. A booking consists of a guest name, a room number, and a date.  
     */
    @Test
    public void saveBookingTest(){
        String name = "loki";
        int roomNo = new Random().nextInt(roomNumber);
        String bookingDate = new DateTime().toString("yyyyMMdd");
        bookingService.saveBooking(name, String.valueOf(roomNo), bookingDate);
        List<Integer> rooms = bookingService.findAvailableRooms(bookingDate);
        assert !rooms.contains(roomNo);
        List<Booking> bookings = bookingService.findGuestBookings(name);
        assert bookings != null && bookings.get(0).getRoomNo() == roomNo;
    }

    public Booking randomBooking() {
        int roomNo = new Random().nextInt(roomNumber);
        String name = "loki" + new Random().nextInt(roomNumber);
        String bookingDate = DateTime.now().plusDays(new Random().nextInt(100)).toString("yyyyMMdd");
        return new Booking(name, roomNo, bookingDate);
    }

    /**
     * find the available rooms on a given date before save booking in multi-thread mode
     * @return
     */
    @Test
    public void findAvailableRooms() throws Exception {
        StopWatch watch = new StopWatch();
        watch.start();
        int threads = 100000;
        CountDownLatch latch = new CountDownLatch(threads);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
        for (int i = 0; i < threads; i++) {
            scheduler.schedule(() -> {
                Booking booking = randomBooking();//just random out a booking, no matter the room is available or not
                try {
                    List<Integer> availableRooms = bookingService.findAvailableRooms(booking.getBookingDate());
                    if (!availableRooms.isEmpty()) booking.setRoomNo(new Random().nextInt(availableRooms.size()));
                    boolean saved = bookingService.saveBooking(booking.getGuestName(), booking.getRoomNo().toString(), booking.getBookingDate());
                    if (availableRooms.contains(booking.getRoomNo()) != saved) {
                        //System.out.println(booking.getBookingDate() + " : " + Arrays.toString(availableRooms));
                        //System.out.println(booking + " booked "+ (saved ? "success" : "failed"));
                        //System.out.println(booking.getBookingDate() + " : " + Arrays.toString(bookingService.findAvailableRooms(booking.getBookingDate())));
                        throw new RuntimeException("save booking result is not match the result of query available rooms ");
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    System.out.println(booking + " failed:" + e.getMessage());
                }
                latch.countDown();
            }, new Random().nextInt(100), TimeUnit.MILLISECONDS);

        }

        latch.await();
        scheduler.shutdown();
        System.out.println("==>cost " + watch.formatTime());
    }

}
