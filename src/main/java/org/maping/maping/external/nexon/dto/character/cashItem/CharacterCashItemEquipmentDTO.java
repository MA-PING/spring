package org.maping.maping.external.nexon.dto.character.cashItem;
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
public class CharacterCashItemEquipmentDTO {
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
     * 캐릭터 외형 모드(0:일반 모드, 1:제로인 경우 베타, 엔젤릭버스터인 경우 드레스 업 모드)
     */
    @JsonProperty("character_look_mode")
    private String characterLookMode;

    /**
     * 적용 중인 캐시 장비 프리셋 번호
     */
    @JsonProperty("preset_no")
    private long presetNo;

    /**
     * 장착 중인 캐시 장비
     */
    @JsonProperty("cash_item_equipment_base")
    private List<CharacterCashItemEquipmentPresetDTO> cashItemEquipmentBase;

    /**
     * 1번 코디 프리셋
     */
    @JsonProperty("cash_item_equipment_preset_1")
    private List<CharacterCashItemEquipmentPresetDTO> cashItemEquipmentPreset1;

    /**
     * 2번 코디 프리셋
     */
    @JsonProperty("cash_item_equipment_preset_2")
    private List<CharacterCashItemEquipmentPresetDTO> cashItemEquipmentPreset2;

    /**
     * 3번 코디 프리셋
     */
    @JsonProperty("cash_item_equipment_preset_3")
    private List<CharacterCashItemEquipmentPresetDTO> cashItemEquipmentPreset3;

    /**
     * 제로인 경우 베타, 엔젤릭버스터인 경우 드레스 업 모드에서 장착 중인 캐시 장비
     */
    @JsonProperty("additional_cash_item_equipment_base")
    private List<CharacterCashItemEquipmentPresetDTO> additionalCashItemEquipmentBase;

    /**
     * 제로인 경우 베타, 엔젤릭버스터인 경우 드레스 업 모드의 1번 코디 프리셋
     */
    @JsonProperty("additional_cash_item_equipment_preset_1")
    private List<CharacterCashItemEquipmentPresetDTO> additionalCashItemEquipmentPreset1;

    /**
     * 제로인 경우 베타, 엔젤릭버스터인 경우 드레스 업 모드의 2번 코디 프리셋
     */
    @JsonProperty("additional_cash_item_equipment_preset_2")
    private List<CharacterCashItemEquipmentPresetDTO> additionalCashItemEquipmentPreset2;

    /**
     * 제로인 경우 베타, 엔젤릭버스터인 경우 드레스 업 모드의 3번 코디 프리셋
     */
    @JsonProperty("additional_cash_item_equipment_preset_3")
    private List<CharacterCashItemEquipmentPresetDTO> additionalCashItemEquipmentPreset3;
}
