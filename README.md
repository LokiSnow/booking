# Hotel Booking Manager 

> version: `1.0-SNAPSHOT`

## Requirement

```
Your task is to implement a simple hotel booking manager in Java, 
as a microservice API. The number of rooms should be configurable, 
and it should expose the following methods:
1.A method to store a booking. A booking consists of a guest name, a room number, and a date.  
2.A method to find the available rooms on a given date.  
3.A method to find all the bookings for a given guest. 
Guidance
1.Use only in-memory data structures; do not use a database.  
2.Do not use any framework or libraries in your solution.  
3.Provide tests with your solution (you may use libraries for the tests).  
4.Your solution should build with Maven or Gradle.  
5.Do not need to take into account the booking cancellation and guest check out.  
6.Test cases as comprehensive as possible.
7.Please share github link of your solution with us. 
Extra credit
Make your solution thread-safe. 
```

## System Analysis
This micro service just uses core java api, `BookingService` interface only has 3 services,
So this system only designed a starter, a communication layer, a service layer.

Communication layer: 
```
`BookingServer` uses nio like `nginx`, distribute new thread pool task `BookingRequestListener` 
while having client readable event. 
Request Message form:  "methodName:args1,args2,args3..."
Response Message form: Used JSON to wrap a unified `BaseResponse`
```

Service layer: 
```
`BookingServiceManager` keeps a singlaton `BookingService`,and initialized services while class loading.
`BookingService` uses ConcurrentHashMap to save in-memory data, and uses `BitSet` to mark avilable rooms.
it's a query friendly solution that will sacrifice a bit saving efficiency.
```

A simple calling flow:
```
                BookingApplication
                        |
                  BookingServer
                        |
              BookingRequestListener
                        |
               BookingServiceManager
                        |
                  BookingService

```

## Startup command

```bash
the optional parameter is room number, default is 100.
java -jar booking-1.0.jar 1000
```
