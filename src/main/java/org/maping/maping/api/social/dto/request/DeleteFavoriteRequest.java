package org.maping.maping.api.social.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteFavoriteRequest {
    private Long characterId;  // characterId 필드 추가
}