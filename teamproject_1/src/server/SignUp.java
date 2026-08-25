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
        String html = Files.readString(Path.of("src/html/signup.html"));
        SimpleHttpServer.sendResponse(exchange, 200, SimpleHttpServer.TYPE_HTML, html);

        String method = exchange.getRequestMethod();
        if(!method.equals("POST")) {
            handlePost(exchange);
        }
        else{
            exchange.getResponseHeaders().set("Allow","POST");
            SimpleHttpServer.sendResponse(exchange, 405,
                    SimpleHttpServer.TYPE_TEXT, "지원하지 않는 메서드 입니다");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException{
        String requestBody = SimpleHttpServer.readRequestBody(exchange);
        System.out.println("회원가입 요청 : " + requestBody);



    }
}
