package com.lms.enums;

public class MemberType {
    public enum memberType{
        STUDENT("Student") {
            @Override
            public int getBorrowLimit() {
                return 3;
            }

            @Override
            public double getFineMultiplier() {
                return 1.0;
            }
        },
        FACULTY("Faculty") {
            @Override
            public int getBorrowLimit() {
                return 10;
            }

            @Override
            public double getFineMultiplier() {
                return 0.5;
            }
        };

        private final String displayName;

        memberType(String displayName){
            this.displayName = displayName;
        }

        public String getDisplayName(){
            return this.displayName;
        }

        public abstract int getBorrowLimit();
        public abstract double getFineMultiplier();
    }
}