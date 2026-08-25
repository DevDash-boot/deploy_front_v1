package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Login implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String html = Files.readString(Path.of("src/html/login.html"));

        SimpleHttpServer.sendResponse(exchange, 200, SimpleHttpServer.TYPE_HTML, html);
        // 로그인에서 필요한 기능
        // 1. 회원가입에 저장된 list 정보 들고 오기
        // get으로 SimpleHttpServer의 list를 가져오고
        // 2. 아이디 유무
        // 만약 exchange.getRequestBody();에서 id가 ~~라면 확인
        // 3. 비밀번호 유무
        // 만약 exchange.getRequestBody();에서 pw가 ~~라면 확인
        // 4. 아이디 - 비밀번호가 일치하는지
        // 5. 틀렸을 때 에러 메시지 출력, 일치하면 로그인 되어 메인 홈으로 이동
    }
}
