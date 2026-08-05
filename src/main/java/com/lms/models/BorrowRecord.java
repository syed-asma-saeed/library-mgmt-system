package com.lms.models;

import com.lms.enums.Genre;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BorrowRecord {
    private String recordId;
    private String memberId;
    private String isbn;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;
    private boolean returned;

    public BorrowRecord(String recordId, String memberId, String isbn, LocalDate borrowDate, LocalDate dueDate){
        this.recordId = recordId;
        this.memberId = memberId;
        this.isbn = isbn;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.fine = 0.0;
        this.returned = false;
    }

    private BorrowRecord(String recordId, String memberId, String isbn, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate, double fine, boolean returned){
        this.recordId = recordId;
        this.memberId = memberId;
        this.isbn = isbn;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine;
        this.returned = returned;
    }

    public String getRecordId()      { return recordId; }
    public String getMemberId()      { return memberId; }
    public String getIsbn()          { return isbn; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate()    { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public double getFine()          { return fine; }
    public boolean isReturned()      { return returned; }


    public void markReturned(LocalDate returnDate, double fine){
        this.returnDate = returnDate;
        this.fine = fine;
        this.returned = true;
    }

    public boolean isOverdue(){
        return !returned && LocalDate.now().isAfter(dueDate);
    }

    public String toCSV(){
        return (String.join(",",
                recordId,
                memberId,
                isbn,
                borrowDate.toString(),
                dueDate.toString(),
                (returnDate == null)? "NULL" : returnDate.toString(),
                String.valueOf(fine),
                String.valueOf(returned)));
    }

    public static BorrowRecord fromCSV(String line){
        String[] parts = line.split(",");
        if (parts.length != 8) {
            throw new IllegalArgumentException("Invalid CSV record: " + line);
        }
        return new BorrowRecord(parts[0], parts[1], parts[2], LocalDate.parse(parts[3]), LocalDate.parse(parts[4]), parts[5].equals("NULL") ? null : LocalDate.parse(parts[5]), Double.parseDouble(parts[6]), Boolean.parseBoolean(parts[7]));
    }

    @Override
    public String toString(){
        return String.format(
                "RecordID: %s | MemberID: %s | ISBN: %s | Borrow Date: %s | Due Date: %s | Return Date: %s | Fine: %f | Returned: %b",
                recordId, memberId, isbn, borrowDate.toString(), dueDate.toString(), (returnDate == null) ? "NULL" : returnDate.toString(), fine, returned
        );
    }
}