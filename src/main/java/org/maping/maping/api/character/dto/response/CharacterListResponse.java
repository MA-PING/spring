package org.maping.maping.api.character.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.maping.maping.external.nexon.dto.character.CharacterInfoDTO;
import org.maping.maping.external.nexon.dto.character.CharacterListDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CharacterListResponse {
    private CharacterListDto characterList;
    private CharacterInfoDTO characterInfo;
}
