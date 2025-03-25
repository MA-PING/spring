package org.maping.maping.api.character.service;

import org.maping.maping.api.character.dto.request.OcidRequest;
import org.maping.maping.api.character.dto.response.AutocompleteResponse;
import org.maping.maping.api.character.dto.response.CharacterListResponse;
import org.maping.maping.api.character.dto.response.CharacterResponse;
import org.maping.maping.external.nexon.dto.character.CharacterInfoDTO;

import java.util.List;

public interface CharacterService {
    public CharacterInfoDTO getCharacterInfo(String characterName);
    public List<AutocompleteResponse> getAutocomplete(String characterName);
    public CharacterListResponse getApiCharacterList(OcidRequest apiKey);
    public CharacterListResponse getCharacterList(Long userId);
    public CharacterInfoDTO getRefreshCharacterInfo(String characterName);

    CharacterResponse getApiCheck(String apiKey);
}
