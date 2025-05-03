package org.maping.maping.external.nexon.dto.character.android;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CharacterAndroidCashItemEquipmentDTO {
    /**
     * 안드로이드 캐시 아이템 부위 명
     */
    @JsonProperty("cash_item_equipment_part")
    private String cashItemEquipmentPart;

    /**
     * 안드로이드 캐시 아이템 슬롯 위치
     */
    @JsonProperty("cash_item_equipment_slot")
    private String cashItemEquipmentSlot;

    /**
     * 안드로이드 캐시 아이템 명
     */
    @JsonProperty("cash_item_name")
    private String cashItemName;

    /**
     * 안드로이드 캐시 아이템 아이콘
     */
    @JsonProperty("cash_item_icon")
    private String cashItemIcon;

    /**
     * 안드로이드 캐시 아이템 설명
     */
    @JsonProperty("cash_item_description")
    private String cashItemDescription;

    /**
     * 안드로이드 캐시 아이템 옵션
     */
    @JsonProperty("cash_item_option")
    private List<CharacterAndroidCashItemEquipmentOptionDTO> cashItemOption;

    /**
     * 안드로이드 캐시 아이템 유효 기간 (KST)
     */
    @JsonProperty("date_expire")
    private String dateExpire;

    /**
     * 안드로이드 캐시 아이템 옵션 유효 기간 (KST)
     */
    @JsonProperty("date_option_expire")
    private String dateOptionExpire;

    /**
     * 안드로이드 캐시 아이템 라벨 정보 (스페셜라벨, 레드라벨, 블랙라벨, 마스터라벨)
     */
    @JsonProperty("cash_item_label")
    private String cashItemLabel;

    /**
     * 안드로이드 캐시 아이템 컬러링프리즘 정보
     */
    @JsonProperty("cash_item_coloring_prism")
    private CharacterAndroidCashItemEquipmentColoringPrismDTO cashItemColoringPrism;
}
