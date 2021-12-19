package com.neo.io.bio.threadPoolForConn;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

class TimeServerHandler implements Runnable {
    private Socket client;

    public TimeServerHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {

        // todo PrintWriter vs BufferedWriter
        // todo OutputStreamWriter need or no need
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true)) {

            while (true) {
                String req = in.readLine();
                System.out.println("receive req: " + req);
                // todo 为什么client断开连接会发送一个null?
                if (req == null) {
                    System.out.println("client disconnect: " + client);
                    break;
                }
                if (req.contains("now")) {
                    out.println(LocalDateTime.now());
                } else {
                    out.println("what do you want?");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
