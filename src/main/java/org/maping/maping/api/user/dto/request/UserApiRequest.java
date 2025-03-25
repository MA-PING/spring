package org.maping.maping.api.user.dto.request;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserApiRequest {
    @Size(max = 255)
    @NotNull
    private String userApiInfo;
}
