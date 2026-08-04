package com.lms.exceptions;

public class BookAlreadyReturnedException extends Exception{
    public BookAlreadyReturnedException(String mssg){
        super(mssg);
    }
}