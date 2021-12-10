package com.scb.booking.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * a simplified communication layer using nio,
 *
 * @author Loki
 * @date 2021-12-09
 */
public class BookingServer extends Thread{

    private static final int SERVER_PORT = 8089;
    private static final Object DISTRIBUTED = new Object();

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public void run() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(SERVER_PORT));
            // epoll_create fd5
            Selector selector = Selector.open();
            //epoll: epoll_ctl(epoll_fd5, Add, fd6,EPOLLIN)
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                //select() ==> epoll_wait(fd5) or select poll
                int n = selector.select();
                if (n == 0) continue;

                Set<SelectionKey> keys = selector.selectedKeys();
                for(Iterator<SelectionKey> it = keys.iterator(); it.hasNext();) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isAcceptable()){
                        ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                        SocketChannel sc = channel.accept();
                        sc.configureBlocking(false);
                        //register READ event
                        sc.register(selector, SelectionKey.OP_READ);
                        System.out.println("accept a client : " + sc.socket().getInetAddress().getHostName());
                    } else if (key.isReadable() && key.attachment() == null) {
                        executorService.execute(new BookingRequestListener((SocketChannel) key.channel()));
                        //mark that this client connection has been distributed
                        key.attach(DISTRIBUTED);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Booking server caught error" + e.getMessage());
            e.printStackTrace();
        }

    }

}
