package org.maping.maping.api.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String userName;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
