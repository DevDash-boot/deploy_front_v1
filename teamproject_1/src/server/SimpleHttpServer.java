package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class SimpleHttpServer {

    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10; // 미리 생성해 두는 스레드 개수를 의미한다

    static final String TYPE_HTML = "text/html; charset=UTF-8";
    static final String TYPE_TEXT = "text/plain; charset=UTF-8";
    static final String TYPE_JSON = "application/json; charset=UTF-8";

    static final List<Users> users = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // 1. HTTP 서버 객체 생성
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // 2. 경로와 담당 핸들러 연결
        // - http://localhost:8080/
        server.createContext("/", new HomeHandler());
        // - http://localhost:8080/api/users
        server.createContext("/api/users", new UserApiHandler());
        // 로그인/회원가입
        server.createContext("/api/login", new Login());
        server.createContext("/api/signup", new SignUp());

        // 3. 요청을 처리할 스레드 풀 지정 (http 서버는 멀티 스레드 프로그램이라서 미리 생성할 스레드를 지정한다)
        server.setExecutor(Executors.newFixedThreadPool(THREAD_POOL_SIZE));

        // 4. 서버 시작 (main 은 여기서 끝나고, 서버는 별 도 스레드에서 계속 돈다)
        server.start();

        System.out.println(">> HTTP 서버 시작 <<");

    } // end of main

    /////////////////////////////////////////
    // 공통 메서드 정의
    ////////////////////////////////////////

    // 응답을 내보낸다
    static void sendResponse(HttpExchange exchange, int statusCode, String contentType, String bodyText)
            throws IOException {
        // 1. 보낼 데이터 (매개 변수 bodyText)
        // 2. 문자열을 바이트 배열로 바꾼다.
        byte[] bodyBytes = bodyText.getBytes(StandardCharsets.UTF_8);
        // 3. 응답의 종류를 헤더에 적는다 ( 응답 HTTP 메세지 )
        exchange.getResponseHeaders().set("Content-Type", contentType);
        // 4. 상태 코드와 본문 길이를 설정하며 헤더를 실제로 내보난다.
        exchange.sendResponseHeaders(statusCode, bodyBytes.length);
        // 5. 헤더가 나간 다음에야 본문 통로가 열린다.
        // getResonseBody() OutputStream 이므로 문자열이 아니라 바이트를 쓰고 있다.
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bodyBytes);
        }
    }

    // JSON 응답하는 경우는 다른 핸들러에서도 사용할 수 있어서 여기서 로직을 작성한다.
    static void sendJson(HttpExchange exchange, int statusCode, Object data) throws IOException {
        // new Gson().toJson(data) --> 자바 객체를 ---> JSON 문자열로 변환
        sendResponse(exchange, statusCode, TYPE_JSON, new Gson().toJson(data));
    }

    // 요청 본문을 문자열로 읽는 기능
    static String readRequestBody(HttpExchange exchange) throws IOException {
        StringBuffer requestBody =  new StringBuffer();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {

            String line;
            while (  (line = reader.readLine()) != null ) {
                requestBody.append(line);
            }
        }
       return requestBody.toString();
    }

} // end of class
