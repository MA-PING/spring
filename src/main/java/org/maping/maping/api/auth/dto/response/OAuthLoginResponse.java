package org.maping.maping.api.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.maping.maping.common.utills.jwt.dto.JwtDto;

@Data
@Builder
public class OAuthLoginResponse {
    @Schema(description = "신규 가입 여부", example = "true - 신규 / false - 기존")
    private boolean isNewMember;

    @Schema(description = "JWT")
    private JwtDto jwtDto;
}
