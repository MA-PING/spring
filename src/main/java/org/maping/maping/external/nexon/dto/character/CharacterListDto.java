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
public class CharacterListDto {
    /**
     * 메이플스토리 계정 목록
     */
    @JsonProperty("account_list")
    private List<CharacterListAccountDTO> accountList;
}
