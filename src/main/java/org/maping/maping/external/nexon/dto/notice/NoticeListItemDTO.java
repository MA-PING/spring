package org.maping.maping.external.nexon.dto.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class NoticeListItemDTO {
    /**
     * 공지 제목
     */
    @JsonProperty("title")
    private String title;

    /**
     * 공지 링크
     */
    @JsonProperty("url")
    private String url;

    /**
     * 공지 식별자
     */
    @JsonProperty("notice_id")
    private int noticeId;

    /**
     * 공지 등록일
     */
    @JsonProperty("date")
    private String date;

}
