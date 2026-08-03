package com.lms.exceptions;

public class BookNotAvailableException extends Exception{
    public BookNotAvailableException(String mssg){
        super(mssg);
    }
}