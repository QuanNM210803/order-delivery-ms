package com.odms.auth.dto.request;

import com.odms.auth.constant.Message;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = Message.LOGIN_USERNAME_REQUIRE)
    private String username;

    @NotBlank(message = Message.LOGIN_PASSWORD_REQUIRE)
    private String password;
}
