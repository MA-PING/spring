package org.maping.maping.external.nexon.dto.character.cashItem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CharacterCashItemEquipmentPresetDTO {
    /**
     * 캐시 장비 부위 명
     */
    @JsonProperty("cash_item_equipment_part")
    private String cashItemEquipmentPart;

    /**
     * 캐시 장비 슬롯 위치
     */
    @JsonProperty("cash_item_equipment_slot")
    private String cashItemEquipmentSlot;

    /**
     * 캐시 장비 명
     */
    @JsonProperty("cash_item_name")
    private String cashItemName;

    /**
     * 캐시 장비 아이콘
     */
    @JsonProperty("cash_item_icon")
    private String cashItemIcon;

    /**
     * 캐시 장비 설명
     */
    @JsonProperty("cash_item_description")
    private String cashItemDescription;

    /**
     * 캐시 장비 옵션 목록
     */
    @JsonProperty("cash_item_option")
    private List<CharacterCashItemEquipmentOptionDTO> cashItemOption;

    /**
     * 캐시 장비 유효 기간 (KST)
     */
    @JsonProperty("date_expire")
    private String dateExpire;

    /**
     * 캐시 장비 옵션 유효 기간 (KST, 시간 단위 데이터로 분은 일괄 0으로 표기)
     */
    @JsonProperty("date_option_expire")
    private String dateOptionExpire;

    /**
     * 캐시 장비 라벨 정보
     */
    @JsonProperty("cash_item_label")
    private String cashItemLabel;

    /**
     * 캐시 장비 컬러링프리즘 정보
     */
    @JsonProperty("cash_item_coloring_prism")
    private CharacterCashItemEquipmentColoringPrismDTO cashItemColoringPrism;

    /**
     * 아이템 장착 가능 성별
     */
    @JsonProperty("item_gender")
    private String itemGender;

    /**
     * 스킬명
     */
    @JsonProperty("skills")
    private List<String> skills;

}
