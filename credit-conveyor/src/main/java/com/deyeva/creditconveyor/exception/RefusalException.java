package com.deyeva.creditconveyor.exception;

import java.util.List;

public class RefusalException extends RuntimeException{

    public RefusalException(List<String> message) {
        super(String.valueOf(message));
    }

}
