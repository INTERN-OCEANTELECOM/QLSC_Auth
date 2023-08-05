package com.ocena.qlsc.common.error.advice;

import com.ocena.qlsc.common.error.exception.*;
import com.ocena.qlsc.common.constants.message.StatusCode;
import com.ocena.qlsc.common.constants.message.StatusMessage;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ThrownExceptionHandler {
    @ExceptionHandler({DataNotFoundException.class})
    public ResponseEntity<Object> handleDataNotFound(final RuntimeException e) {
        DataResponse<?> response = ResponseMapper.toDataResponse(e.getMessage(),
                StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({DataAlreadyExistException.class})
    public ResponseEntity<Object> handleDataAlreadyExist(final RuntimeException e) {
        DataResponse<?> response = ResponseMapper.toDataResponse(e.getMessage(),
                StatusCode.NOT_IMPLEMENTED, StatusMessage.NOT_IMPLEMENTED);
        return new ResponseEntity<>(response, HttpStatus.NOT_IMPLEMENTED);
    }

    // findById response is null
    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFound(final RuntimeException e) {
        DataResponse<?> response = ResponseMapper.toDataResponse(e.getMessage(),
                StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({NotPermissionException.class})
    public ResponseEntity<Object> handleNotPermission(final RuntimeException e) {
        DataResponse<?> response = ResponseMapper.toDataResponse(e.getMessage(),
                StatusCode.NOT_IMPLEMENTED, StatusMessage.NOT_PERMISSION);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidTimeException.class})
    public ResponseEntity<Object> handleInvalidTime(final RuntimeException e) {
        DataResponse<?> response = ResponseMapper.toDataResponse(e.getMessage(),
                StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
        System.out.println("Vao day 2");
        return new ResponseEntity<>(response, HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler({FunctionLimitedTimeException.class})
    public ResponseEntity<Object> handleFunctionLimitedTimeException(final RuntimeException e) {
        DataResponse<?> response = ResponseMapper.toDataResponse(e.getMessage(),
                StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
