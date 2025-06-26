package org.maping.maping.external.nexon.dto.union;

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
public class UnionRankingDTO {

    @JsonProperty("date")
    private String date;

    @JsonProperty("ranking")
    private int ranking;

    @JsonProperty("character_name")
    private String characterName;

    @JsonProperty("world_name")
    private String worldName;

    @JsonProperty("class_name")
    private String className;

    @JsonProperty("sub_class_name")
    private String subClassName;

    @JsonProperty("union_level")
    private int unionLevel;

    @JsonProperty("union_power")
    private int unionPower;


}
