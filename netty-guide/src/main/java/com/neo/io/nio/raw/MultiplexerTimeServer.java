package com.neo.io.nio.raw;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Iterator;

class MultiplexerTimeServer implements Runnable {
    private Selector selector;
    private ServerSocketChannel acceptorSrv;
    private volatile boolean stop;

    public static void main(String[] args) {
        MultiplexerTimeServer multiplexerTimeServer = new MultiplexerTimeServer(9090);
        new Thread(multiplexerTimeServer).start();
    }

    public MultiplexerTimeServer(int port) {
        try {
            // 1.打开ServerSocketChannel, Selector
            selector = Selector.open();
            acceptorSrv = ServerSocketChannel.open();

            // 2.socket监听端口，设置为no-block
            // todo acceptorSrv.bind() vs acceptorSrv.socket().bind()
            acceptorSrv.configureBlocking(false);
            acceptorSrv.socket().bind(new InetSocketAddress(port), 1024);

            // 3.将ServerSocketChannel注册到Reactor线程监控的多路复用器，监听Accept
            acceptorSrv.register(selector, acceptorSrv.validOps());

            System.out.println("the time server is start in port: " + port);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        try {
            while (!stop) {
                selector.select(1000);
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    try {
                        handleEvent(key);
                    } catch (IOException e) {
                        // 如果出错了不cancel, 当client断开，server由于异常没有消费掉断开事件，会一直触发（水平触发）
                        key.cancel();
                        key.channel().close();
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleEvent(SelectionKey key) throws IOException {
        if (key.isValid()) {
            if (key.isAcceptable()) {
                ServerSocketChannel serverChan = (ServerSocketChannel) key.channel();
                SocketChannel clientChan = serverChan.accept();
                clientChan.configureBlocking(false);
                System.out.println("client connect: " + clientChan);
                clientChan.register(selector, SelectionKey.OP_READ);
            }

            if (key.isReadable()) {
                SocketChannel clientChan = (SocketChannel) key.channel();
                ByteBuffer inBuf = ByteBuffer.allocate(1024);
                // 客户端断开不是返回-1
                // Method threw 'java.io.IOException' exception: 远程主机强迫关闭了一个现有的连接。
                int read = clientChan.read(inBuf);
                if (read > 0) {
                    // 写模式转读模式
                    inBuf.flip();
                    byte[] bytes = new byte[inBuf.remaining()];
                    inBuf.get(bytes);
                    String req = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("receive req: " + req);
                    if (req.contains("now")) {
                        String resp = LocalDateTime.now().toString();
                        doWrite(clientChan, resp);
                    }
                // 客户端断开不是返回-1 ?
                } else if (read < 0) {
                    System.out.println("client disconnect: " + clientChan);
                    key.cancel();
                    clientChan.close();
                } else {

                }
            }
        }
    }

    private void doWrite(SocketChannel clientChan, String resp) throws IOException {
        byte[] bytes = resp.getBytes(StandardCharsets.UTF_8);
        ByteBuffer outBuf = ByteBuffer.allocate(bytes.length);
        outBuf.put(bytes);
        outBuf.flip();
        // todo 这里可能写半包，加上writable事件监听
        clientChan.write(outBuf);
    }
}
