package com.qomoi.exception;



import com.qomoi.Utility.Constants;
import com.qomoi.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ExistingUserFoundException.class)
    public ResponseEntity<ResponseDto> userAlreadyExist(ExistingUserFoundException e){
        ResponseDto errorDto = new ResponseDto();
        errorDto.setCode(0);
        errorDto.setMessage(Constants.EMAIL_ID_EXISTS);
        return ResponseEntity.status(409).body(errorDto);
    }

    @ExceptionHandler(MissingFieldException.class)
    public ResponseEntity<ResponseDto> missingUserField(MissingFieldException e){
        ResponseDto errorDto = new ResponseDto();
        errorDto.setCode(1);
        errorDto.setMessage(Constants.INPUT_FIELD_MISSING);
        return ResponseEntity.status(409).body(errorDto);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseDto> missingUserField(BadCredentialsException e){
        ResponseDto errorDto = new ResponseDto();
        errorDto.setCode(2);
        errorDto.setMessage(Constants.INVALID_CREDENTIALS);
        return ResponseEntity.status(409).body(errorDto);
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ResponseDto> tokenRefreshException(TokenRefreshException e){
        ResponseDto errorDto = new ResponseDto();
        errorDto.setCode(3);
        errorDto.setMessage(Constants.TOKEN_REFRESHED_NOT_AVAILABLE);
        return ResponseEntity.status(404).body(errorDto);
    }
}
