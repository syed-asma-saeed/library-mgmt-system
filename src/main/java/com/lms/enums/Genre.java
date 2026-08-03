package com.lms.enums;

public class Genre{
    public enum genre{
        FICTION("Fiction"),
        NON_FICTION("Non_Fiction"),
        SCIENCE("Science"),
        TECHNOLOGY("Technology"),
        HISTORY("History"),
        BIOGRAPHY("Biography"),
        MATHEMATICS("Mathematics"),
        PHILOSOPHY("Philosophy");

        private final String displayName;

        genre(String displayName){
            this.displayName = displayName;
        }

        public String getDisplayName(){
            return this.displayName;
        }

    }
}
