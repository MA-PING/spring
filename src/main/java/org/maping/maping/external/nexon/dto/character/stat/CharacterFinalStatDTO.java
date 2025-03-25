package org.maping.maping.external.nexon.dto.character.stat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CharacterFinalStatDTO {

    /**
     * 스탯 명
     */
    @JsonProperty("stat_name")
    private String statName;

    /**
     * 스탯 값
     */
    @JsonProperty("stat_value")
    private String statValue;
}

