package org.maping.maping.external.nexon.dto.character;
import lombok.*;
import org.maping.maping.external.nexon.dto.character.ability.CharacterAbilityDTO;
import org.maping.maping.external.nexon.dto.character.android.CharacterAndroidEquipmentDTO;
import org.maping.maping.external.nexon.dto.character.itemEquipment.CharacterItemEquipmentDTO;
import org.maping.maping.external.nexon.dto.character.matrix.CharacterHexaMatrixDTO;
import org.maping.maping.external.nexon.dto.character.matrix.CharacterHexaMatrixStatDTO;
import org.maping.maping.external.nexon.dto.character.matrix.CharacterVMatrixDTO;
import org.maping.maping.external.nexon.dto.character.skill.CharacterLinkSkillDTO;
import org.maping.maping.external.nexon.dto.character.skill.CharacterSkillDTO;
import org.maping.maping.external.nexon.dto.character.stat.CharacterHyperStatDTO;
import org.maping.maping.external.nexon.dto.character.stat.CharacterStatDto;
import org.maping.maping.external.nexon.dto.character.symbol.CharacterSymbolEquipmentDTO;
import org.maping.maping.external.nexon.dto.union.UnionArtifactDTO;
import org.maping.maping.external.nexon.dto.union.UnionDTO;
import org.maping.maping.external.nexon.dto.union.UnionRaiderDTO;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CharacterQDTO {
    private String Ocid;
    private CharacterBasicDTO Basic;
}
