package org.maping.maping.api.social.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddFavoriteRequest {
    @NotNull(message = "characterId는 필수입니다.")
    private Long characterId;
}
