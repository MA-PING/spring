package org.maping.maping.api.character.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
@Setter
@ToString
public class FavoriteResponse {
    /**
     * 캐릭터 명
     */
    @JsonProperty("character_name")
    private String characterName;

    /**
     * 월드 이미지
     */
    @JsonProperty("world_img")
    private String worldImage;

    /**
     * 캐릭터 직업
     */
    @JsonProperty("character_class")
    private String characterClass;

    /**
     * 캐릭터 레벨
     */
    @JsonProperty("character_level")
    private long characterLevel;

    /**
     * 캐릭터 외형 이미지
     */
    @JsonProperty("image")
    private String characterImage;

    @JsonProperty("guild")
    private String guild;
}
