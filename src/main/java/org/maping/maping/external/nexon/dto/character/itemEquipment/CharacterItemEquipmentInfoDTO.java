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
public class CharacterItemEquipmentInfoDTO {
    /**
     * 장비 부위 명
     */
    @JsonProperty("item_equipment_part")
    private String itemEquipmentPart;

    /**
     * 장비 슬롯 위치
     */
    @JsonProperty("item_equipment_slot")
    private String itemEquipmentSlot;

    /**
     * 장비 명
     */
    @JsonProperty("item_name")
    private String itemName;

    /**
     * 장비 아이콘
     */
    @JsonProperty("item_icon")
    private String itemIcon;

    /**
     * 장비 설명
     */
    @JsonProperty("item_description")
    private String itemDescription;

    /**
     * 장비 외형
     */
    @JsonProperty("item_shape_name")
    private String itemShapeName;

    /**
     * 장비 외형 아이콘
     */
    @JsonProperty("item_shape_icon")
    private String itemShapeIcon;

    /**
     * 전용 성별
     */
    @JsonProperty("item_gender")
    private String itemGender;

    /**
     * 장비 최종 옵션 정보
     */
    @JsonProperty("item_total_option")
    private CharacterItemEquipmentTotalOptionDTO itemTotalOption;

    /**
     * 장비 기본 옵션 정보
     */
    @JsonProperty("item_base_option")
    private CharacterItemEquipmentBaseOptionDTO itemBaseOption;

    /**
     * 잠재능력 등급
     */
    @JsonProperty("potential_option_grade")
    private String potentialOptionGrade;

    /**
     * 에디셔널 잠재능력 등급
     */
    @JsonProperty("additional_potential_option_grade")
    private String additionalPotentialOptionGrade;

    /**
     * 잠재능력 첫 번째 옵션
     */
    @JsonProperty("potential_option_1")
    private String potentialOption1;

    /**
     * 잠재능력 두 번째 옵션
     */
    @JsonProperty("potential_option_2")
    private String potentialOption2;

    /**
     * 잠재능력 세 번째 옵션
     */
    @JsonProperty("potential_option_3")
    private String potentialOption3;

    /**
     * 에디셔널 잠재능력 첫 번째 옵션
     */
    @JsonProperty("additional_potential_option_1")
    private String additionalPotentialOption1;

    /**
     * 에디셔널 잠재능력 두 번째 옵션
     */
    @JsonProperty("additional_potential_option_2")
    private String additionalPotentialOption2;

    /**
     * 에디셔널 잠재능력  세 번째 옵션
     */
    @JsonProperty("additional_potential_option_3")
    private String additionalPotentialOption3;

    /**
     * 착용 레벨 증가
     */
    @JsonProperty("equipment_level_increase")
    private long equipmentLevelIncrease;

    /**
     * 장비 특별 옵션 정보
     */
    @JsonProperty("item_exceptional_option")
    private CharacterItemEquipmentExceptionalOptionDTO itemExceptionalOption;

    /**
     * 장비 추가 옵션 정보
     */
    @JsonProperty("item_add_option")
    private CharacterItemEquipmentAddOptionDTO itemAddOption;

    /**
     * 성장 경험치
     */
    @JsonProperty("growth_exp")
    private long growthExp;

    /**
     * 성장 레벨
     */
    @JsonProperty("growth_level")
    private long growthLevel;

    /**
     * 업그레이드 횟수
     */
    @JsonProperty("scroll_upgrade")
    private String scrollUpgrade;

    /**
     * 가위 사용 가능 횟수 (교환 불가 장비, 가위 횟수가 없는 교환 가능 장비는 255)
     */
    @JsonProperty("cuttable_count")
    private String cuttableCount;

    /**
     * 황금 망치 재련 적용 (1:적용, 이외 미 적용)
     */
    @JsonProperty("golden_hammer_flag")
    private String goldenHammerFlag;

    /**
     * 복구 가능 횟수
     */
    @JsonProperty("scroll_resilience_count")
    private String scrollResilienceCount;

    /**
     * 업그레이드 가능 횟수
     */
    @JsonProperty("scroll_upgradeable_count")
    private String scrollUpgradeableCount;

    /**
     * 소울 명
     */
    @JsonProperty("soul_name")
    private String soulName;

    /**
     * 소울 옵션
     */
    @JsonProperty("soul_option")
    private String soulOption;

    /**
     * 장비 기타 옵션 정보
     */
    @JsonProperty("item_etc_option")
    private CharacterItemEquipmentEtcOptionDTO itemEtcOption;

    /**
     * 강화 단계
     */
    @JsonProperty("starforce")
    private String starforce;

    /**
     * 놀라운 장비 강화 주문서 사용 여부 (0:미사용, 1:사용)
     */
    @JsonProperty("starforce_scroll_flag")
    private String starforceScrollFlag;

    /**
     * 장비 스타포스 옵션 정보
     */
    @JsonProperty("item_starforce_option")
    private CharacterItemEquipmentStarforceOptionDTO itemStarforceOption;

    /**
     * 특수 반지 레벨
     */
    @JsonProperty("special_ring_level")
    private long specialRingLevel;

    /**
     * 장비 유효 기간(KST)
     */
    @JsonProperty("date_expire")
    private String dateExpire;
}
