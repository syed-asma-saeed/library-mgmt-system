package com.lms.exceptions;
public class BorrowLimitExceededException extends Exception{
    public BorrowLimitExceededException(String mssg){
        super(mssg);
    }
}