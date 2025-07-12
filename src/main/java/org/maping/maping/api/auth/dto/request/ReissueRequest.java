package org.maping.maping.api.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ReissueRequest {
    @Schema(description = "Access token", example = "Bearer token(Access Token)")
    private String accessToken;
}