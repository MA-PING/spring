package org.maping.maping.api.auth.service;
import org.maping.maping.api.auth.dto.request.NicknameCheckRequest;
import org.maping.maping.api.auth.dto.request.UserRegistrationRequest;
import org.maping.maping.common.utills.jwt.dto.JwtDto;

public interface  AuthService {

    boolean isValidPassword(String password);

    boolean isValidNickname(String nickname);

    boolean isDuplicateNickname(NicknameCheckRequest request);

    void registerUser(UserRegistrationRequest registrationDto);

    JwtDto login(String email, String password);

}
