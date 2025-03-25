package org.maping.maping.api.social.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteCharacterResponse {
    private String characterName;
    private Long characterLevel;
    private String worldName;
    private String characterClass;
    private String image;
    private String worldImg;
    private String guild;
}