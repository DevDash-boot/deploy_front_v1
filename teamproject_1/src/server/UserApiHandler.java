package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserApiHandler implements HttpHandler {

    // 실제로는 DB에 저장하나, 현재는 리스트로 대신한다.
    // 서버를 껏다 켜면 내용이 사라지기 때문에 초기화 블록이 있으면 좋다.
    private static final List<Users> userList = new ArrayList<Users>();

    // 회원 목록 가져오기
    public static List<Users> getUserList() {
        return userList;
    }

    // 아이디 중복 확인
    public static boolean existsId(String id) {
        for (Users user : userList) {
            if (user.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    // 들어오는 순서대로 부여할 번호
    private static int nextNumber = 1;

    static {
        addUsers(new Users("홍길동", "1111", "1111", "a@naver.com"));
        addUsers(new Users("김철수", "2222", "2222", "b@naver.com"));
    }

    // 회원 가입용
    public static synchronized void addUser(Users user) {
        user.setNumber(nextNumber);
        nextNumber++;
        userList.add(user);
        return user.getNumber();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try{
            String method = exchange.getRequestMethod();
            if(method.equals("GET")) {
                handleGet(exchange);
            } else if (method.equals("POST")) {
                handlePost(exchange);
            } else{
                // 405 를 보낼 때는 어떤 메서드가 되는지 Allow 헤더로 알려주는 것이 규칙이다.
                exchange.getResponseHeaders().set("Allow", "GET, POST");
                SimpleHttpServer.sendResponse(exchange, 405,
                    SimpleHttpServer.TYPE_TEXT, "지원하지 않는 메서드 입니다");
        }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<Users> copyUserList(){
        return new ArrayList<>(userList);
    }

    // get 요청
    private void handleGet(HttpExchange exchange) throws IOException {
        SimpleHttpServer.sendJson(exchange, 200, copyUserList());
    }

    // post 요청
    private void handlePost(HttpExchange exchange) throws IOException {
        // 1. HTTP 요청 바디를 읽어야 한다.
                String requestBody = SimpleHttpServer.readRequestBody(exchange);
        System.out.println("POST 요청 [api/users] 받은 본문 확인 : " + requestBody);

        // 2. JSON 문자열을 User 객체로 변환한다.
        // 주의
        Users user;
        try {
            user = new Gson().fromJson(requestBody, Users.class);
        } catch (JsonSyntaxException e) {
            SimpleHttpServer.sendResponse(exchange, 400,
                    SimpleHttpServer.TYPE_TEXT, "JSON 형식이 올바르지 않습니다");
            return;
        }

        // 3. 검증
        if (user == null || user.getName() == null || user.getName().isBlank()
                || user.getId() == null || user.getId().isBlank()) {
            SimpleHttpServer.sendResponse(exchange, 400,
                    SimpleHttpServer.TYPE_TEXT , "name과 id는 반드시 있어야 합니다");
            return;
        }

        // 아이디 중복 확인
        if (existsId(user.getId())) {
            SimpleHttpServer.sendResponse(exchange, 400, SimpleHttpServer.TYPE_TEXT, "이미 존재하는 아이디입니다");
            return;
        }


        // 이메일이 없다면 빈자리 출
        if (user.getEmail() == null) {
            user.setEmail("");
        }

        //저장 처리 number 값은 고정값이 아니라서 저장 결과를 다시 돌려 준다.
        addUsers(user);

        // 등록 성공은 200 대신 201 Created 로 응답을 한다.
        SimpleHttpServer.sendJson(exchange, 201, user);

    }

} // end of class
