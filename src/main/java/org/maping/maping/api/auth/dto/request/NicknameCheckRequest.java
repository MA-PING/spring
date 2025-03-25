package org.maping.maping.api.auth.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NicknameCheckRequest {
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;
}
