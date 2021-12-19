package com.gyg.nio;
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

public class MultiplexerTimeClient implements Runnable {
    private Selector selector;
    private SocketChannel client;
    private volatile boolean stop;

    public static void main(String[] args) {
        MultiplexerTimeClient multiplexerTimeClient = new MultiplexerTimeClient("127.0.0.1", 9090);
        new Thread(multiplexerTimeClient).start();
    }

    public MultiplexerTimeClient(String ip, int port) {
        try {
            // 1.打开SocketChannel, Selector
            selector = Selector.open();
            client = SocketChannel.open();

            // 2.client连接server，设置为no-block
            client.configureBlocking(false);
            boolean connect = client.connect(new InetSocketAddress(ip, port));

            // 3.client连接成功，则注册read监听, 否则注册connect监听
            if (connect) {
                System.out.println("connect at startup success");
                client.register(selector, SelectionKey.OP_READ);
                doWrite();
            } else {
                client.register(selector, SelectionKey.OP_CONNECT);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void doWrite() throws IOException {
        // ByteBuffer.wrap执行完后，就是处于read模式，所以不需要flip
        ByteBuffer outBuf = ByteBuffer.wrap("now".getBytes(StandardCharsets.UTF_8));
        client.write(outBuf);
        if (!outBuf.hasRemaining()) {
            System.out.println("send req success");
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
                    handleEvent(key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleEvent(SelectionKey key) {
        try {
            if (key.isValid()) {
                SocketChannel clientChan = (SocketChannel) key.channel();
                if (key.isConnectable()) {
                    System.out.println("connect after register to selector success");
                    if (clientChan.finishConnect()) {
                        clientChan.register(selector, SelectionKey.OP_READ);
                        doWrite();
                    } else {
                        System.out.println("connect fail");
                    }
                }

                if (key.isReadable()) {
                    ByteBuffer inBuf = ByteBuffer.allocate(1024);
                    int read = clientChan.read(inBuf);
                    if (read > 0) {
                        // 写模式转读模式
                        inBuf.flip();
                        byte[] bytes = new byte[inBuf.remaining()];
                        inBuf.get(bytes);
                        String resp = new String(bytes, StandardCharsets.UTF_8);
                        System.out.println("receive resp: " + resp);
                        stop();
                    } else if (read < 0) {
                        System.out.println("server disconnect");
                        key.cancel();
                        clientChan.close();
                    } else {

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
