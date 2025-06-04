package com.odms.auth.dto.request;

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
    @NotBlank(message = "Yêu cầu tên đăng nhập")
    private String username;

    @NotBlank(message = "Yêu cầu mật khẩu")
    private String password;
}
