package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// 회원가입에서 필요한 기능
// 아이디, 비밀번호 작성 시 list에 저장
// 아이디 중복 방지(등록된 id가 있다면~)
// get으로 SimpleHttpServer의 list를 가져오고
// post로 id와 pw, name, email을 전송해준다.    UserApiHandler 참고

// 회원가입 완료 후 로그인 페이지로 이동

public class SignUp implements HttpHandler {

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

    // GET 시 - 회원가입 페이지
    private void handleGet(HttpExchange exchange) throws IOException {
        String html = Files.readString(Path.of("src/html/signup.html"));
        SimpleHttpServer.sendResponse(exchange, 200,
                SimpleHttpServer.TYPE_TEXT, html);

    }

    // POST 시 - 회원가입 전송
    private void handlePost(HttpExchange exchange) throws IOException {
        // Http 요청 바디 읽기
        String requestBody = SimpleHttpServer.readRequestBody(exchange);
        System.out.println("회원가입 요청 : " + requestBody);

        // form 데이터
        Map<String, String> formData = parseFormData(requestBody);

        // html에서 설정한 값 읽기
        String name = formData.get("name");
        String id = formData.get("id");
        String pw = formData.get("pw");
        String email = formData.get("email");

        // 검증
        if (name == null || id == null || pw == null || email == null
                || name.isBlank() || id.isBlank() || pw.isBlank() || email.isBlank()) {

            SimpleHttpServer.sendResponse(exchange, 400,
                    SimpleHttpServer.TYPE_TEXT, "모두 입력해주세요");

            return;
        }

        // 아이디 중복 검사
        if(UserApiHandler.existsId(id)){
            SimpleHttpServer.sendResponse(exchange, 409, SimpleHttpServer.TYPE_TEXT, "이미 존재하는 아이디입니다.");
            return;
        }

        // 리스트에 정보 저장
        UserApiHandler.addUser(user);
        System.out.println("회원가입 완료" + user);

        // 회원가입 완료하면 로그인 페이지로 이동하기
        exchange.getResponseHeaders().set("Location", "/api/login");
        exchange.sendResponseHeaders(300, -1);
        exchange.close();
    }
}
