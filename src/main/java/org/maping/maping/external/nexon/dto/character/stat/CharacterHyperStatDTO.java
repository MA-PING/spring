package org.maping.maping.external.nexon.dto.character.stat;

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
public class CharacterHyperStatDTO {

    /**
     * 조회 기준일 (KST, 일 단위 데이터로 시, 분은 일괄 0으로 표기)
     */
    @JsonProperty("date")
    private String date;

    /**
     * 캐릭터 직업
     */
    @JsonProperty("character_class")
    private String characterClass;

    /**
     * 적용 중인 프리셋 번호
     */
    @JsonProperty("use_preset_no")
    private String usePresetNo;

    /**
     * 사용 가능한 최대 하이퍼 스탯 포인트
     */
    @JsonProperty("use_available_hyper_stat")
    private long useAvailableHyperStat;

    /**
     * 프리셋 1번 하이퍼 스탯 정보
     */
    @JsonProperty("hyper_stat_preset_1")
    private List<CharacterHyperStatPresetDTO> hyperStatPreset1;

    /**
     * 프리셋 1번 하이퍼 스탯 잔여 포인트
     */
    @JsonProperty("hyper_stat_preset_1_remain_point")
    private long hyperStatPreset1RemainPoint;

    /**
     * 프리셋 2번 하이퍼 스탯 정보
     */
    @JsonProperty("hyper_stat_preset_2")
    private List<CharacterHyperStatPresetDTO> hyperStatPreset2;

    /**
     * 프리셋 2번 하이퍼 스탯 잔여 포인트
     */
    @JsonProperty("hyper_stat_preset_2_remain_point")
    private long hyperStatPreset2RemainPoint;

    /**
     * 프리셋 3번 하이퍼 스탯 정보
     */
    @JsonProperty("hyper_stat_preset_3")
    private List<CharacterHyperStatPresetDTO> hyperStatPreset3;

    /**
     * 프리셋 3번 하이퍼 스탯 잔여 포인트
     */
    @JsonProperty("hyper_stat_preset_3_remain_point")
    private long hyperStatPreset3RemainPoint;

}
