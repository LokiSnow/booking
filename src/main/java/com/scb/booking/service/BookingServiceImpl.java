package com.scb.booking.service;


import com.scb.booking.model.Booking;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * This implementation uses ConcurrentHashMap to save in-memory data,
 * it's a query friendly solution that will sacrifice a bit saving efficiency
 * @author Loki
 * @date 2021-12-08
 */
public class BookingServiceImpl implements BookingService {
    /** key:bookingDate, value: the bit map of available rooms **/
    private static Map<String, BitSet> roomsBit = new ConcurrentHashMap<>();
    /** key:guest name, value: all the bookings of the guest **/
    private static Map<String, List<Booking>> bookings = new ConcurrentHashMap<>();
    /** the lock to ensure thread-safe **/
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    private int roomNumber = 100;

    /**
     * initialed for blank booking date query
     */
    private List<Integer> fullRooms;

    public BookingServiceImpl(){
        String roomNumber = System.getProperty("roomNumber");
        if (roomNumber != null && !roomNumber.equals("")){
            this.roomNumber = Integer.valueOf(roomNumber);
        }
        afterPropertiesSet();
    }

    public void afterPropertiesSet(){
        assert roomNumber > 0;
        fullRooms = new ArrayList<>(roomNumber);
        for (int i = 0; i < roomNumber; i++) {
            fullRooms.add(i,i);
        }

    }
    /**
     * store a booking. A booking consists of a guest name, a room number, and a date.  
     * @param guestName
     * @param strRoomNo
     * @param bookingDate
     * @return
     */
    @Override
    public Boolean saveBooking(String guestName, String strRoomNo, String bookingDate){
        assert Objects.nonNull(guestName);
        assert Objects.nonNull(strRoomNo);
        assert Objects.nonNull(bookingDate);
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            //check if it's available room
            Integer roomNo = Integer.valueOf(strRoomNo);
            if (roomsBit.get(bookingDate) != null && !roomsBit.get(bookingDate).get(roomNo)) return false;
            //save booking to map
            Booking booking = new Booking(guestName, roomNo, bookingDate);
            if (!bookings.containsKey(guestName)) {
                bookings.put(guestName, new ArrayList<>());
            }
            bookings.get(guestName).add(booking);

            //set bit set for querying available rooms, can be published as asynchronous task
            recordRoomsBit(roomNo, bookingDate);
            return true;
        }catch (Exception e) {//throw out friendly business exceptions
            e.printStackTrace();
            throw new IllegalStateException("Please try again later.");
        }finally {
            writeLock.unlock();
        }
    }

    /**
     * find the available rooms on a given date
     * @param bookingDate
     * @return
     */
    @Override
    public List<Integer> findAvailableRooms(String bookingDate) {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            //blank booking date, just return all the room no.
            if (!roomsBit.containsKey(bookingDate)) return fullRooms;
            return roomsBit.get(bookingDate).stream().boxed().collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    /**
     * find all the bookings for a given guest
     * @param guestName
     * @return
     */
    @Override
    public List<Booking> findGuestBookings(String guestName) {
        return bookings.get(guestName);
    }

    /**
     * use lazy mode to load bit map, in case of too much blank booking date fill into roomsBit
     * @param roomNo
     * @param bookingDate
     */
    private void recordRoomsBit(Integer roomNo, String bookingDate) {
        if (!roomsBit.containsKey(bookingDate)) {
            BitSet rooms = new BitSet(roomNumber);
            rooms.flip(0, roomNumber);
            roomsBit.put(bookingDate, rooms);
        }
        roomsBit.get(bookingDate).set(roomNo, false);
    }

}
