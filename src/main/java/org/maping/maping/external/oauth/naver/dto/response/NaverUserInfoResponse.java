package org.maping.maping.external.oauth.naver.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class NaverUserInfoResponse {

    @JsonProperty("response")
    private NaverAccount response;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NaverAccount {

        // 네이버 계정 이름
        @JsonProperty("name")
        private String name;

        // 네이버 계정 이메일
        @JsonProperty("email")
        private String email;
    }
}
