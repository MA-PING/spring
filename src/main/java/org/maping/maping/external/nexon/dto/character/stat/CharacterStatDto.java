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
public class CharacterStatDto {
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
     * 현재 스탯 정보
     */
    @JsonProperty("final_stat")
    private List<CharacterFinalStatDTO> finalStat;

    /**
     * 잔여 AP
     */
    @JsonProperty("remain_ap")
    private long remainAp;
}
