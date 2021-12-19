package com.neo.io.raw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class RawServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        // 浏览器直接访问: localhost:8880
        ServerSocket serverSocket = new ServerSocket(8880);
        Socket clientSocket = serverSocket.accept();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
