package com.scb.booking.service;

import com.scb.booking.BaseResponse;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * as here is only one simple service, just use singleton instance,
 * and initialized services while class loading
 * @author Loki
 * @date 2021-12-09
 */
public class BookingServiceManager {
    private static ConcurrentHashMap<String, Method> services = new ConcurrentHashMap<>(3);
    private volatile static BookingService service = null;

    static {
        Method[] methods = BookingService.class.getDeclaredMethods();
        Arrays.stream(methods).forEach(method -> services.put(method.getName(), method));
    }

    /**
     * invoke service method, and wrap result with unified BaseResponse,
     * business service can throw business validation error directly,
     * here will wrap it into resultMsg of BaseResponse
     * @param methodName
     * @param args
     * @return
     */
    public static BaseResponse invoke(String methodName, Object[] args) {
        BaseResponse response = new BaseResponse();
        try {
            Method method = services.get(methodName);
            response.withContent(method.invoke(getService(), args));
        } catch (Exception e) {
            response.withResultMsg(e.getMessage());
            e.printStackTrace();
            System.out.println("service invoke failed.");
        }
        return response;
    }

    /**
     * use inner class to get singleton service instance
     * @return
     */
    private static BookingService getService(){
        if (service == null) {
            synchronized (BookingService.class) {
                if (service == null) {
                    service = new BookingServiceImpl();
                }
            }
        }
        return service;
    }

}
