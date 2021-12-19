package com.neo.io.bio.oneThreadPerConn;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class TimeClient {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        try (Socket client = new Socket("localhost", port)) {
            System.out.println("the time client connect to server success");

            // 【坑】client没有手动flush, 输出流也没有设置为autoFlush, 导致server收不到消息
            try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {

                out.println("now is ?");
                System.out.println("send req success");
                System.out.println(in.readLine());
            }
        }
    }
}
