package com.lms.models;

import com.lms.enums.MemberType;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class StudentMember extends Member{

    public StudentMember(String memberId, String name, String email, int currentBorrowCount, List<String> history){
        super(memberId, name, email, MemberType.memberType.STUDENT, currentBorrowCount, history );
    }

    @Override
    public double calculateFine(LocalDate dueDate){
        long daysOverdue = ChronoUnit.DAYS.between(dueDate, LocalDate.now());

        if (daysOverdue <= 0) {
            return 0.0;
        }

        return daysOverdue * 2.0 * MemberType.memberType.getFineMultiplier();
    }
}