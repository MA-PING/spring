package org.maping.maping.external.nexon.dto.character.itemEquipment;

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
public class CharacterItemEquipmentDTO {
    /**
     * 조회 기준일 (KST, 일 단위 데이터로 시, 분은 일괄 0으로 표기)
     */
    @JsonProperty("date")
    private String date;

    /**
     * 캐릭터 성별
     */
    @JsonProperty("character_gender")
    private String characterGender;

    /**
     * 캐릭터 직업
     */
    @JsonProperty("character_class")
    private String characterClass;

    /**
     * 적용 중인 장비 프리셋 번호
     */
    @JsonProperty("preset_no")
    private Integer presetNo;

    /**
     * 장비 정보
     */
    @JsonProperty("item_equipment")
    private List<CharacterItemEquipmentInfoDTO> itemEquipment;

    /**
     * 1번 프리셋 장비 정보
     */
    @JsonProperty("item_equipment_preset_1")
    private List<CharacterItemEquipmentInfoDTO> itemEquipmentPreset1;

    /**
     * 2번 프리셋 장비 정보
     */
    @JsonProperty("item_equipment_preset_2")
    private List<CharacterItemEquipmentInfoDTO> itemEquipmentPreset2;

    /**
     * 3번 프리셋 장비 정보
     */
    @JsonProperty("item_equipment_preset_3")
    private List<CharacterItemEquipmentInfoDTO> itemEquipmentPreset3;

    /**
     * 칭호 정보
     */
    @JsonProperty("title")
    private CharacterItemEquipmentTitleDTO title;

    /**
     * 외형 설정에 등록한 훈장 외형 정보
     */
    @JsonProperty("medal_shape")
    private CharacterItemEquipmentMedalShapeDTO medalShape;

    /**
     * 에반 드래곤 장비 정보 (에반인 경우 응답)
     */
    @JsonProperty("dragon_equipment")
    private List<CharacterItemEquipmentDragonInfoDTO> dragonEquipment;

    /**
     * 메카닉 장비 정보 (메카닉인 경우 응답)
     */
    @JsonProperty("mechanic_equipment")
    private List<CharacterItemEquipmentMechanicInfoDTO> mechanicEquipment;

}
