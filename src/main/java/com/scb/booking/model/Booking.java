package com.scb.booking.model;

import java.io.Serializable;

/**
 * @author Loki
 * @date 2021-12-08
 */
public class Booking implements Serializable {
    private String guestName;
    private Integer roomNo;
    private String bookingDate;

    public Booking() {

    }

    public Booking(String guestName, Integer roomNo, String bookingDate){
        this.guestName = guestName;
        this.roomNo = roomNo;
        this.bookingDate = bookingDate;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public Integer getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(Integer roomNo) {
        this.roomNo = roomNo;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    @Override
    public String toString() {
        return bookingDate + " : " + roomNo + " - " + guestName;
    }
}
