package com.example.tasktwo.Exception;

public class EmptyDataException extends RuntimeException{

    public EmptyDataException(String userDefinedMessage){
        super(userDefinedMessage);
    }
}
