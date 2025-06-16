package org.maping.maping.api.character.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@ToString
public class AutocompleteResponse {
    /**
     * 캐릭터 명
     */
    @JsonProperty("characterName")
    private String characterName;

    /**
     * 월드 이미지
     */
    @JsonProperty("world")
    private String world;

    /**
     * 캐릭터 직업
     */
    @JsonProperty("className")
    private String characterClass;

    /**
     * 캐릭터 레벨
     */
    @JsonProperty("level")
    private long characterLevel;

    /**
     * 캐릭터 외형 이미지
     */
    @JsonProperty("image")
    private String characterImage;
}
