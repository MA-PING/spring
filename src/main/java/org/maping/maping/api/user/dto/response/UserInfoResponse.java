package org.maping.maping.api.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponse {
    private Long userId;
    private String userName;
    private String email;
    private String userApiInfo;
}
