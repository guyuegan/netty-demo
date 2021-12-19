package com.neo.io.bio.threadPoolForConn;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class TimeServer {

    static int coreSize = Runtime.getRuntime().availableProcessors();
    static ExecutorService executor = new ThreadPoolExecutor(coreSize, coreSize*2, 5,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>(10000));

    public static void main(String[] args) throws Exception {
        int port = 8080;
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("the time server is start in port: " + port);
            while (true) {
                Socket client = server.accept();
                System.out.println("client connect: " + client);
                // 一个连接一个线程
                executor.execute(new TimeServerHandler(client));
            }
        }
    }
}
