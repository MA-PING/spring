package org.maping.maping.api.user.service;

import org.maping.maping.api.auth.dto.request.PasswordRequest;
import org.maping.maping.api.user.dto.request.UserApiRequest;
import org.maping.maping.api.user.dto.response.UserInfoResponse;
import org.maping.maping.common.response.BaseResponse;
import org.maping.maping.model.user.UserInfoJpaEntity;

public interface UserService {

    void deleteUser(Long userId);

    UserInfoResponse getUserInfo(Long userId);

    void updateNickname(Long userId, String newNickname);

    void updatePassword(Long userId, PasswordRequest passwordRequest);

    void postUserApi(Long userId, UserApiRequest request);

    void deleteUserApi(Long userId);

    BaseResponse<String> setOriginalCharacter(Long userId, String ocid);
}
