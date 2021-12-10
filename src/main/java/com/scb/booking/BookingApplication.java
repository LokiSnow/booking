package com.scb.booking;


import com.scb.booking.server.BookingServer;

/**
 * @author Loki
 * @date 2021-12-08
 */
public class BookingApplication {

    public static void main(String[] args) {
        //set room number
        if (args.length == 1) {
            System.setProperty("roomNumber", args[0]);
            System.out.println("roomNumber was set to " + args[0]);
        }
        //start booking server
        new BookingServer().start();
    }
}
