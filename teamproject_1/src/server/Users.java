package server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    private int number;     // 등록 순서
    private String name;    // 이름
    private String id;      // 아이디
    private String pw;      // 비밀번호
    private String email;   // 이메일

    public Users(String name,String id, String pw, String email) {
        this.name = name;
        this.id = id;
        this.pw = pw;
        this.email = email;
    }
}