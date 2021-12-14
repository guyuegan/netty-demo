package com.gyg.bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeClient {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        try (Socket client = new Socket("localhost", port)) {
            System.out.println("the time client connect to server success");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    PrintWriter out = new PrintWriter(client.getOutputStream())) {

                out.println("now is ?");
                System.out.println("send req success");
                System.out.println(in.readLine());
            }
        }
    }
}
