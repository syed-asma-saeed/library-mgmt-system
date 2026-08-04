package com.lms.exceptions;

public class BookNotFoundException  extends Exception{
    public BookNotFoundException (String mssg){
        super(mssg);
    }
}