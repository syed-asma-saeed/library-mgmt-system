package com.lms.exceptions;

public class MemberNotFoundException extends Exception{
    public MemberNotFoundException(String mssg){
        super(mssg);
    }
}