package org.maping.maping.external.nexon.dto.character.matrix;

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
public class CharacterVMatrixDTO {
    /**
     * 조회 기준일 (KST)
     */
    @JsonProperty("date")
    private String date;

    /**
     * 캐릭터 직업
     */
    @JsonProperty("character_class")
    private String characterClass;

    /**
     * V 코어 정보를 나타내는 DTO 클래스입니다.
     */
    @JsonProperty("character_v_core_equipment")
    private List<CharacterVMatrixCoreEquipmentDTO> characterVCoreEquipment;

    /**
     * 캐릭터 잔여 매트릭스 강화 포인트
     */
    @JsonProperty("character_v_matrix_remain_slot_upgrade_point")
    private Long characterVMatrixRemainSlotUpgradePoint;

}
