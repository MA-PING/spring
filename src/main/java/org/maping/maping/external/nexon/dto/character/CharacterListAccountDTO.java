package org.maping.maping.external.nexon.dto.character;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CharacterListAccountDTO {
    /**
     * 메이플스토리 계정 식별자
     */
    @JsonProperty("account_id")
    private String accountId;

    /**
     * 캐릭터 목록
     */
    @JsonProperty("character_list")
    private List<CharacterListAccountCharacterDTO> characterList;
}
