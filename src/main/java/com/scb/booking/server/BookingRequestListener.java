package com.scb.booking.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.scb.booking.BaseResponse;
import com.scb.booking.Constant;
import com.scb.booking.service.BookingServiceManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.scb.booking.Constant.CHARSET;

/**
 * simple client connection read and write process, imitate RPC call:
 * request message form is "methodName:args1,args2,args3...";
 * response used JSON open source
 * @author Loki
 * @date 2021-12-09
 */
public class BookingRequestListener implements Runnable{
    private Logger logger = Logger.getLogger("booking");

    private SocketChannel client;

    public BookingRequestListener(SocketChannel client){
        this.client = client;
    }

    @Override
    public void run() {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(512);
            client.read(byteBuffer);
            client.shutdownInput();
            //request ==> methodName:args1,args2,args3
            String request = new String(byteBuffer.array(), CHARSET).trim();
            if (request.equals("")) return;
            System.out.println("request==>" + request);
            String[] splited = request.split(Constant.SEPARATOR_METHOD);
            if (splited.length != 2) return;
            String methodName = splited[0];
            String[] args = splited[1].split(Constant.SEPARATOR_ARGS);
            //invoke service
            BaseResponse response = BookingServiceManager.invoke(methodName, args);
            //process response
            if (response != null) {
                String json = JSON.toJSONString(response, SerializerFeature.WriteClassName);
                System.out.println("response==>" + json);
                client.write(ByteBuffer.wrap(json.getBytes(CHARSET)));

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "Client process error!", e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private byte[] processResponse(Object result) {
        byte[] response = null;
        try {
            if (result instanceof String) {
                response = ((String) result).getBytes(CHARSET);
            } else if (result instanceof int[]) {
                response = Arrays.toString((int[]) result).getBytes(CHARSET);
            }else if (result instanceof List) {
                response = result.toString().getBytes(CHARSET);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("response process failed");
        }
        return response;
    }

}
