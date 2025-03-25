package org.maping.maping.api.character.service;

import lombok.extern.slf4j.Slf4j;
import org.maping.maping.api.character.dto.response.AutocompleteResponse;
import org.maping.maping.model.search.CharacterSearchJpaEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Component
public class CharacterConverter implements Converter<CharacterSearchJpaEntity, AutocompleteResponse> {
    public AutocompleteResponse convert(CharacterSearchJpaEntity entity) {
        AutocompleteResponse response = new AutocompleteResponse();
        response.setCharacterName(entity.getCharacterName());
        response.setWorldImage(entity.getWorldImg());
        response.setCharacterClass(entity.getCharacterClass());
        response.setCharacterLevel(entity.getCharacterLevel());
        response.setCharacterImage(entity.getImage());
        return response;
    }
}
