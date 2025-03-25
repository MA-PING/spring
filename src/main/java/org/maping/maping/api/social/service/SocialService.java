package org.maping.maping.api.social.service;

import org.maping.maping.api.social.dto.request.AddFavoriteRequest;
import org.maping.maping.api.social.dto.request.DeleteFavoriteRequest;
import org.maping.maping.api.social.dto.response.FavoriteCharacterResponse;

import java.util.List;

public interface SocialService {
    void addFavorite(Long userId, AddFavoriteRequest request);

    List<FavoriteCharacterResponse> getFavorites(Long userId);

    void deleteFavorite(Long userId, DeleteFavoriteRequest request);
}
