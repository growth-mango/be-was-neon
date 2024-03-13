package webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;

    public static void main(String args[]) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);
            Executor executor = Executors.newFixedThreadPool(50); // 50개의 스레드를 관리할 수 있는 스레드 풀 생성한다. -> 이 갯수가 적당한지는 모르겠음...

            while (true) {
                Socket connection = listenSocket.accept();
                executor.execute(new RequestHandler(connection)); // RequestHandler 를 스레드 풀에 제출 한다. // execute() vs submit()
            }
        } catch (IOException e){
            logger.error(e.getMessage());
        }
    }
}
