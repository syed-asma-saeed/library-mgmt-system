package com.lms.services;

import com.lms.enums.Genre;
import com.lms.enums.MemberType;
import com.lms.exceptions.*;
import com.lms.interfaces.Searchable;
import com.lms.models.*;
import com.lms.storage.BookFileHandler;
import com.lms.storage.BorrowRecordFileHandler;
import com.lms.storage.MemberFileHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LibraryService implements Searchable {

    private Map<String, Book> books;
    private Map<String, Member> members;
    private Map<String, BorrowRecord> borrowRecords;
    private BookFileHandler bookFileHandler;
    private MemberFileHandler memberFileHandler;
    private BorrowRecordFileHandler borrowRecordFileHandler;
    private int bookCounter = 1000;
    private int memberCounter = 1000;
    private int borrowRecordCounter = 1000;

    public LibraryService(Map<String, Book> books, Map<String, Member> members, Map<String, BorrowRecord> borrowRecords) {
        this.books = books;
        this.members = members;
        this.borrowRecords = borrowRecords;
    }

    //Book Management
    public String addBook(String title, String author, Genre genre, int totalCopies, int availableCopies) {
        String isbn = "B" + (bookCounter++);

        Book book = new Book(isbn, title, author, genre, totalCopies, availableCopies);
        books.put(isbn, book);

        saveAll();
        return isbn;
    }

    public void removeBook(String isbn) {
        books.remove(isbn);
        saveAll();
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    //Member Management
    public String addMember(String name, String email, MemberType memberType) {
        String memberId = "M" + (memberCounter++);

        int currentBorrowCount = 0;
        List<String> borrowHistory = new ArrayList<>();

        Member member;
        if(memberType == MemberType.STUDENT)
            member = new StudentMember(memberId, name, email, currentBorrowCount, borrowHistory);
        else{
            member = new FacultyMember(memberId, name, email, currentBorrowCount, borrowHistory);
        }
        members.put(memberId, member);

        saveAll();
        return memberId;
    }

    public void removeMember(String memberId) {
        members.remove(memberId);
        saveAll();
    }

    //Core library operations:
    public String borrowBook(String memberId, String isbn) throws MemberNotFoundException, BookNotFoundException, BorrowLimitExceededException, BookNotAvailableException {
        Member member;
        if (members.containsKey(memberId))
            member = members.get(memberId);
        else
            throw new MemberNotFoundException("No member found with memberId: " + memberId);

        Book book;
        if (books.containsKey(isbn))
            book = books.get(isbn);
        else
            throw new BookNotFoundException("No book found with ISBN: " + isbn);

        if (!member.canBorrow())
            throw new BorrowLimitExceededException("Borrow Limit Exceeded");

        book.borrowCopy();

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate;
        if (member.getMemberType() == MemberType.STUDENT)
            dueDate = borrowDate.plusDays(14);
        else {
            dueDate = borrowDate.plusDays(30);
        }

        String recordId = "R" + (borrowRecordCounter++);
        borrowRecords.put(recordId, new BorrowRecord(recordId, memberId, isbn, borrowDate, dueDate));

        member.recordBorrow(recordId);

        saveAll();

        return recordId;
    }

    public double returnBook(String recordId) throws MemberNotFoundException, BookAlreadyReturnedException {
        BorrowRecord record;
        if (borrowRecords.containsKey(recordId))
            record = borrowRecords.get(recordId);
        else
            throw new MemberNotFoundException("No record found with recordId: " + recordId);

        if (record.isReturned())
            throw new BookAlreadyReturnedException("Book Already returned with recordId: " + recordId);

        Member member = members.get(record.getMemberId());
        Book book = books.get(record.getIsbn());

        double fine = member.calculateFine(record.getDueDate());

        LocalDate today = LocalDate.now();
        record.markReturned(today, fine);

        member.recordReturn();

        book.returnCopy();

        saveAll();

        return fine;
    }

    //Search (implement Searchable<Book>):
    public List<Book> searchByTitle(String keyword){
        return books.values().stream()
                .filter(book -> book.getTitle() != null &&
                        book.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Book> searchByAuthor(String keyword){
        return books.values().stream()
                .filter(book -> book.getAuthor() != null &&
                        book.getAuthor().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Book> searchByGenre(Genre genre){
        List<Book> result = new ArrayList<>();

        for (Book book : books.values()) {
            if (book.getGenre() != null &&
                    book.getGenre() == genre) {
                result.add(book);
            }
        }

        return result;
    }

    public List<BorrowRecord> getOverdueRecords(){
        return borrowRecords.values().stream()
                .filter(BorrowRecord::isOverdue)
                .collect(Collectors.toList());
    }

    public List<BorrowRecord> getMemberBorrowHistory(String memberId) {
        return borrowRecords.values().stream()
                .filter(record -> record.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }

    public List<Book> getAvailableBooks(){
        return books.values().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }

    private void saveAll(){
        List<Book> bookList = new ArrayList<>(books.values());
        bookFileHandler.saveAll(bookList);

        List<Member> memberList = new ArrayList<>(members.values());
        memberFileHandler.saveAll(memberList);

        List<BorrowRecord> borrowRecordList = new ArrayList<>(borrowRecords.values());
        borrowRecordFileHandler.saveAll(borrowRecordList);
    }
}