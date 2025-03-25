package org.maping.maping.api.social.service;

import org.maping.maping.api.social.dto.request.AddFavoriteRequest;
import org.maping.maping.api.social.dto.request.DeleteFavoriteRequest;
import org.maping.maping.api.social.dto.response.FavoriteCharacterResponse;
import org.maping.maping.model.search.CharacterSearchJpaEntity;
import org.maping.maping.model.search.FavoriteJpaEntity;
import org.maping.maping.model.user.UserInfoJpaEntity;
import org.maping.maping.repository.search.CharacterSearchRepository;
import org.maping.maping.repository.search.FavoriteRepository;
import org.maping.maping.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialServiceImpl implements SocialService {
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final CharacterSearchRepository characterSearchRepository;

    public void addFavorite(Long userId, AddFavoriteRequest request) {
        UserInfoJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        CharacterSearchJpaEntity character = characterSearchRepository.findById(request.getCharacterId())
                .orElseThrow(() -> new IllegalArgumentException("해당 캐릭터가 존재하지 않습니다."));

        FavoriteJpaEntity favorite = FavoriteJpaEntity.builder()
                .user(user)
                .character(character)  // 여기서 CharacterSearchJpaEntity를 사용
                .build();

        favoriteRepository.save(favorite);
    }

    public List<FavoriteCharacterResponse> getFavorites(Long userId) {
        // 유저 존재 여부 확인
        UserInfoJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        // 유저에 해당하는 즐겨찾기 목록 가져오기
        List<FavoriteJpaEntity> favorites = favoriteRepository.findAllByUser(user);

        // 즐겨찾기 목록을 FavoriteCharacterResponse로 변환하여 반환
        return favorites.stream()
                .map(fav -> {
                    CharacterSearchJpaEntity c = fav.getCharacter();  // FavoriteJpaEntity에서 CharacterSearchJpaEntity 사용
                    return FavoriteCharacterResponse.builder()
                            .characterName(c.getCharacterName() != null ? c.getCharacterName() : "")
                            .characterLevel(c.getCharacterLevel())
                            .worldName(c.getWorldName() != null ? c.getWorldName() : "")
                            .characterClass(c.getCharacterClass() != null ? c.getCharacterClass() : "")
                            .image(c.getImage() != null ? c.getImage() : "")
                            .worldImg(c.getWorldImg() != null ? c.getWorldImg() : "")
                            .guild(c.getGuild() != null ? c.getGuild() : "")
                            .build();
                })
                .collect(Collectors.toList());  // toList() 대신 collect 사용
    }

    public void deleteFavorite(Long userId, DeleteFavoriteRequest request) {
        UserInfoJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        CharacterSearchJpaEntity character = characterSearchRepository.findById(request.getCharacterId())
                .orElseThrow(() -> new IllegalArgumentException("해당 캐릭터가 존재하지 않습니다."));

        // 즐겨찾기에서 해당 캐릭터를 가진 항목을 찾음
        FavoriteJpaEntity favorite = favoriteRepository.findByUserAndCharacter(user, character)
                .orElseThrow(() -> new IllegalArgumentException("해당 캐릭터는 즐겨찾기에 없습니다."));

        // 해당 즐겨찾기 삭제
        favoriteRepository.delete(favorite);
    }
}
