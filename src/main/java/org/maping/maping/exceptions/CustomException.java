package org.maping.maping.exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.maping.maping.common.enums.expection.ErrorCode;

@Getter
@Setter
@NoArgsConstructor

public class CustomException extends RuntimeException{
    private ErrorCode errorCode;
    private HttpStatus status;

    public CustomException(ErrorCode errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
