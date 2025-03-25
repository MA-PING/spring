package org.maping.maping.api.auth.service;

import org.maping.maping.api.auth.dto.response.OAuthLoginResponse;

public interface OAuthService {
    OAuthLoginResponse naverLogin(String code, String state);

    OAuthLoginResponse googleLogin(String code);
}
