//package org.maping.maping.model.ocid;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//import lombok.*;
//
//import java.time.Instant;
//
//@Entity
//@Table(name = "OCID_TB")
//@Getter
//@Setter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
//@Builder
//public class OcidJpaEntity {
//    @Id
//    @Size(max = 128)
//    @Column(name = "ocid", nullable = false, length = 128)
//    private String ocid;
//
//    @Size(max = 64)
//    @NotNull
//    @Column(name = "character_name", nullable = false, length = 64)
//    private String characterName;
//
//    @NotNull
//    @Column(name = "created_at", nullable = false)
//    private Instant createdAt;
//
//}
