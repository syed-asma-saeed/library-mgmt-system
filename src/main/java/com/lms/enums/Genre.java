package com.lms.enums;

public enum Genre{
    FICTION("Fiction"),
    NON_FICTION("Non_Fiction"),
    SCIENCE("Science"),
    TECHNOLOGY("Technology"),
    HISTORY("History"),
    BIOGRAPHY("Biography"),
    MATHEMATICS("Mathematics"),
    PHILOSOPHY("Philosophy");

    private final String displayName;

    Genre(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }

}
