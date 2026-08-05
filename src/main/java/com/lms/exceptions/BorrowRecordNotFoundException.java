package com.lms.exceptions;

import javax.print.DocFlavor;

public class BorrowRecordNotFoundException extends Exception{
    public BorrowRecordNotFoundException(String message){
        super(message);
    }
}