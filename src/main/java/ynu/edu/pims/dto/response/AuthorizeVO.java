package ynu.edu.pims.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class AuthorizeVO {
    Long id;
    String username;
    String role;
    String token;
    Date expire;
}
