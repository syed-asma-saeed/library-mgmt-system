package com.lms.models;

import com.lms.enums.Genre;
import com.lms.enums.MemberType;
import com.lms.exceptions.BorrowLimitExceededException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Member {
    private String memberId;
    private String name;
    private String email;
    private MemberType memberType;
    private int currentBorrowCount;
    private List<String> borrowHistory;

    public Member(String memberId, String name, String email, MemberType memberType, int currentBorrowCount, List<String> borrowHistory){
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.memberType = memberType;
        this.currentBorrowCount = currentBorrowCount;
        this.borrowHistory = new ArrayList<>(borrowHistory);
    }

    public String getMemberId()       { return memberId; }
    public String getName()           { return name; }
    public String getEmail()          { return email; }
    public MemberType getMemberType() { return memberType; }
    public int getCurrentBorrowCount(){ return currentBorrowCount; }

    public boolean canBorrow(){
        return currentBorrowCount < memberType.getBorrowLimit();
    }

    public void recordBorrow (String recordId) throws BorrowLimitExceededException{
        if(!canBorrow())
            throw new BorrowLimitExceededException("Cannot borrow more books: Borrow Limit Reached.");
        currentBorrowCount++;
        borrowHistory.add(recordId);
    }

    public void recordReturn(){
        currentBorrowCount--;
    }

    public List<String> getBorrowHistory(){
        return  Collections.unmodifiableList(borrowHistory);
    }

    public String toCSV(){
        return (String.join(",",
                memberId,
                name,
                email,
                memberType.name(),
                String.valueOf(currentBorrowCount),
                String.join("|", borrowHistory)));
    }

    public static Member fromCSV(String line) {
        String[] parts = line.split(",", -1);

        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid CSV record: " + line);
        }

        MemberType type = MemberType.valueOf(parts[3]);

        List<String> history = parts[5].isEmpty()
                ? new ArrayList<>()
                : new ArrayList<>(Arrays.asList(parts[5].split("\\|")));

        switch (type) {
            case STUDENT:
                return new StudentMember(
                        parts[0],
                        parts[1],
                        parts[2],
                        Integer.parseInt(parts[4]),
                        history
                );

            case FACULTY:
                return new FacultyMember(
                        parts[0],
                        parts[1],
                        parts[2],
                        Integer.parseInt(parts[4]),
                        history
                );

            default:
                throw new IllegalArgumentException("Unknown member type");
        }
    }

    public abstract double calculateFine(LocalDate dueDate);
}