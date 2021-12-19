package com.neo.io.bio.oneThreadPerConn;

import java.net.ServerSocket;
import java.net.Socket;

class TimeServer {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("the time server is start in port: " + port);
            while (true) {
                Socket client = server.accept();
                System.out.println("client connect: " + client);
                // 一个连接一个线程
                new Thread(new TimeServerHandler(client)).start();
            }
        }
    }
}
