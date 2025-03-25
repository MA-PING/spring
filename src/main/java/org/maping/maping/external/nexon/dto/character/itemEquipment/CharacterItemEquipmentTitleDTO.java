package org.maping.maping.external.nexon.dto.character.itemEquipment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CharacterItemEquipmentTitleDTO {
    /**
     * 칭호 장비 명
     */
    @JsonProperty("title_name")
    private String titleName;

    /**
     * 칭호 아이콘
     */
    @JsonProperty("title_icon")
    private String titleIcon;

    /**
     * 칭호 설명
     */
    @JsonProperty("title_description")
    private String titleDescription;

    /**
     * 칭호 유효 기간 (KST)
     */
    @JsonProperty("date_expire")
    private String dateExpire;

    /**
     * 칭호 옵션 유효 기간 (KST)
     */
    @JsonProperty("date_option_expire")
    private String dateOptionExpire;

    /**
     * 외형 설정에 등록한 칭호 장비 명
     */
    @JsonProperty("title_shape_name")
    private String titleShapeName;

    /**
     * 외형 설정에 등록한 칭호 아이콘
     */
    @JsonProperty("title_shape_icon")
    private String titleShapeIcon;

    /**
     * 외형 설정에 등록한 칭호 설명
     */
    @JsonProperty("title_shape_description")
    private String titleShapeDescription;
}
