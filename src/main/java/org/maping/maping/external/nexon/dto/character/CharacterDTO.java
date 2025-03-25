package org.maping.maping.external.nexon.dto.character;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CharacterDTO {
    /**
     * 캐릭터 식별자
     */
    @JsonProperty("ocid")
    private String ocid;
}
