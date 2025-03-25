package org.maping.maping.common.utills.users.oauth.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class GoogleUserInfoResponse {
    @JsonProperty("sub")
    private String id; // 구글의 유저 고유 ID

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("picture")
    private String picture; // 프로필 이미지 URL
}
