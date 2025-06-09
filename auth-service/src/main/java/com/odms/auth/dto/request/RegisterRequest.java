package com.odms.auth.dto.request;

import com.odms.auth.dto.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Tên đăng nhập là bắt buộc")
    @Size(min = 5, max = 20, message = "Tên đăng nhập phải từ 5 đến 20 ký tự")
    private String username;

    @NotBlank(message = "Mật khẩu là bắt buộc")
    @Size(min = 6, max = 20, message = "Mật khẩu phải từ 6 đến 20 ký tự")
    private String password;

    @NotBlank(message = "Tên đầy đủ là bắt buộc")
    private String fullName;

    @NotBlank(message = "Số điện thoại là bắt buộc")
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải gồm 10 chữ số")
    private String phone;

    @NotBlank(message = "Email là bắt buộc")
    @Email(message = "Email không hợp lệ")
    private String email;

    private String address;

    private RoleName roleName;

}
