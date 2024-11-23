package com.citronix.citronix.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {
    public <T> EntityNotFoundException(String entity, T id ) {
            super(entity + " with id " + id + " not found");
        }
    }
