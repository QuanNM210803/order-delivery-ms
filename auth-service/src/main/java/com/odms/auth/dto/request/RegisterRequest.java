package com.odms.auth.dto.request;

import com.odms.auth.constant.Message;
import com.odms.auth.enums.RoleName;
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

    @NotBlank(message = Message.REGISTER_USERNAME_REQUIRE)
    @Size(min = 5, max = 20, message = Message.REGISTER_USERNAME_INVALID)
    private String username;

    @NotBlank(message = Message.REGISTER_PASSWORD_REQUIRE)
    @Size(min = 6, max = 20, message = Message.REGISTER_PASSWORD_INVALID)
    private String password;

    @NotBlank(message = Message.REGISTER_FULL_NAME_REQUIRE)
    private String fullName;

    @NotBlank(message = Message.REGISTER_PHONE_REQUIRE)
    @Pattern(regexp = "^\\d{10}$", message = Message.REGISTER_PHONE_INVALID)
    private String phone;

    @NotBlank(message = Message.REGISTER_EMAIL_REQUIRE)
    @Email(message = Message.REGISTER_EMAIL_INVALID)
    private String email;

    private String address;

    private RoleName roleName;

}
