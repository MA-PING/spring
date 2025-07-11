package org.maping.maping.api.character.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.maping.maping.external.nexon.dto.character.CharacterInfoDTO;
import org.maping.maping.external.nexon.dto.character.CharacterListDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CharacterListResponse {
    private List<CharacterList> characterList;
    private CharacterInfoDTO characterInfo;
}
