package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Login implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        // GET 요청
        if (method.equals("GET")) {
            handleGet(exchange);
        } else if (method.equals("POST")) {
            handlePost(exchange);
        } else {
            exchange.getResponseHeaders()
                    .set("Allow", "GET, POST");
            SimpleHttpServer.sendResponse(exchange, 405,
                    SimpleHttpServer.TYPE_TEXT, "지원하지 않는 메서드 입니다");
        }
    }

    // GET 시 - 로그인  페이지
    private void handleGet(HttpExchange exchange) throws IOException {
        String html = Files.readString(Path.of("src/html/login.html"));
        SimpleHttpServer.sendResponse(exchange, 200,
                SimpleHttpServer.TYPE_TEXT, html);

    }

    // POST 시 - 로그인 전송
    private void handlePost(HttpExchange exchange) throws IOException {
        // Http 요청 바디 읽기
        String requestBody = SimpleHttpServer.readRequestBody(exchange);
        System.out.println("로그인 : " + requestBody);

        // form 데이터
        Map<String, String> formData = parseFormData(requestBody);

        // html에서 설정한 값 읽기
        // 아이디와 비밀번호 가져오기
        String id = formData.get("id");
        String pw = formData.get("pw");

        // 검증
        if (id == null || pw == null|| id.isBlank() || pw.isBlank()) {
            SimpleHttpServer.sendResponse(exchange,400,SimpleHttpServer.TYPE_TEXT,"아이디와 비밀번호를 입력해주세요.");
            return;
        }

        // 아이디 중복 검사
        if(UserApiHandler.existsId(id)){
            SimpleHttpServer.sendResponse(exchange, 409, SimpleHttpServer.TYPE_TEXT, "이미 존재하는 아이디입니다.");
            return;
        }

        // 로그인 성공 시
        System.out.println("로그인 성공");

        // 로그인 실패 시
        if(loginUser ==null){
            SimpleHttpServer.sendResponse(exchange, 400, SimpleHttpServer.TYPE_TEXT, "로그인 실패");
            return;
        }

        // 로그인 성공하면 8080으로 이동
        exchange.getResponseHeaders().set("Location", "/");
        exchange.sendResponseHeaders(300, -1);
        exchange.close();
    }
}
