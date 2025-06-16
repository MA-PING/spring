package org.maping.maping.api.character.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CharacterResponse {
    private String characterName;

    private String worldName;

    private String world;

    private String characterClass;

    private long characterLevel;

    private String characterGuildName;

    private String characterImage;
}
