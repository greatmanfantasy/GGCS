package com.example.ggcs_main;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

// Thread class for connect and send/receive the data to server
// how to use
// 1. create instance
// 2. set (instance).send_byte_buffer ~
// 3. start thread by "(instance).start();"
// 4. processing receive data from server by "new String((instance).receive_byte);"
// 5. close socket and stream by "(instance).close_socket_and_stream();"
public class TCPClient extends Thread {

    // 서버의 IP & Port - 매번 IP, Port 확인 후 수정할 것! // 루프백 사용시 에러나는 경우 있음...
    String server_ip = "104.14.5.105";
    int server_port = 7000;
    public static final int MAX_RECEIVE_BYTE_BUFFER_SIZE = 200;

    Socket socket = null; // 서버와 연결할 소켓
    public InputStream input_stream; // 서버로부터 받을 데이터들을 위한 stream
    public OutputStream output_stream; // 서버로 보낼 데이터들을 위한 stream

    // 서버로 보낼 바이트 버퍼
    // -> 사용할 곳에서 TCPClient 인스턴스 생성 후, (인스턴스).sendByteBuffer 이용하여 .allocate(), .order(), .put() 등으로 설정해놓고 사용해야 함
    ByteBuffer send_byte_buffer = null;
    // 서버로부터 받을 byte array
    // -> 사용할 곳에서 new String((인스턴스).receiveByte, 0, (인스턴스).receive_byte_length, "UTF-8") 하여 String 변환 후 사용
    byte[] receive_byte = new byte[MAX_RECEIVE_BYTE_BUFFER_SIZE];
    int receive_byte_length = 0; // server로부터 receive 한 데이터의 길이

    // thread 실행하면 서버와 소켓 연결하고 소켓의 InputStream, OutputStream을 따옴
    public void run() {
        try {
            // make a empty socket first, and connect after setReuseAddress setting (for 이전에 연결된 소켓이 해당 포트를 점유하고 있어도 바인딩하기 위해)
            socket = new Socket();
            InetSocketAddress isa = new InetSocketAddress(server_ip, server_port);
            socket.setReuseAddress(true);
            socket.connect(isa);

            input_stream = socket.getInputStream();
            output_stream = socket.getOutputStream();
            // above this line, init the client socket

            // part for send data to server
            output_stream.write(send_byte_buffer.array());
            output_stream.flush();

            // part for receive data from server
            while((receive_byte_length = input_stream.read(receive_byte)) == 0) {
                continue;
            }

        // 에러 발생시 예외처리
        } catch (Exception e) {
            e.printStackTrace();
            // close the socket and stream
            try {
                socket.close();
                input_stream.close();
                output_stream.close();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    // must have call this method at the end of TCPClient instance using
    public void close_socket_and_stream() {
        try {
            this.socket.close();
            this.output_stream.close();
            this.input_stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}