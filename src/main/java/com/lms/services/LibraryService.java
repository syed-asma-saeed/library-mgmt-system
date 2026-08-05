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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LibraryService implements Searchable<Book> {

    private Map<String, Book> books = new HashMap<>();
    private Map<String, Member> members = new HashMap<>();
    private Map<String, BorrowRecord> borrowRecords = new HashMap<>();
    private BookFileHandler bookFileHandler = new BookFileHandler();
    private MemberFileHandler memberFileHandler = new MemberFileHandler();
    private BorrowRecordFileHandler borrowRecordFileHandler = new BorrowRecordFileHandler();
    private int bookCounter = 1000;
    private int memberCounter = 1000;
    private int borrowRecordCounter = 1000;

    public LibraryService() {
        List<Book> bookList = bookFileHandler.loadAll();
        for(Book b: bookList){
            this.books.put(b.getIsbn(), b);
        }
        List<Member> memberList = memberFileHandler.loadAll();
        for(Member m: memberList){
            this.members.put(m.getMemberId(), m);
        }
        List<BorrowRecord> borrowRecordList = borrowRecordFileHandler.loadAll();
        for(BorrowRecord br: borrowRecordList){
            this.borrowRecords.put(br.getRecordId(), br);
        }

        bookCounter = 1000 + bookList.size();
        memberCounter = 1000 + memberList.size();
        borrowRecordCounter = 1000 + borrowRecordList.size();
    }

    //Book Management
    public String addBook(String title, String author, Genre genre, int totalCopies) {
        String isbn = "ISBN" + (bookCounter++);

        Book book = new Book(isbn, title, author, genre, totalCopies);
        books.put(isbn, book);

        saveAll();
        return isbn;
    }

    public void removeBook(String isbn) throws BookNotFoundException, BookNotAvailableException{
        Book book = books.get(isbn);
        if (book == null)
            throw new BookNotFoundException("No book found: " + isbn);
        if (book.getAvailableCopies() != book.getTotalCopies())
            throw new BookNotAvailableException("Cannot remove — copies currently borrowed");
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

    public void removeMember(String memberId) throws MemberNotFoundException, BorrowLimitExceededException{
        Member member = members.get(memberId);
        if (member == null)
            throw new MemberNotFoundException("No member found: " + memberId);
        if (member.getCurrentBorrowCount() > 0)
            throw new BorrowLimitExceededException("Cannot remove — member has active borrows");
        members.remove(memberId);
        saveAll();
    }

    //Core library operations:
    private Member getMember(String memberId) throws MemberNotFoundException {
        Member member = members.get(memberId);
        if (member == null)
            throw new MemberNotFoundException("No member found: " + memberId);
        return member;
    }

    private Book getBook(String isbn) throws BookNotFoundException {
        Book book = books.get(isbn);
        if (book == null)
            throw new BookNotFoundException("No book found: " + isbn);
        return book;
    }

    private BorrowRecord getRecord(String recordId) throws BorrowRecordNotFoundException {
        BorrowRecord record = borrowRecords.get(recordId);
        if (record == null)
            throw new BorrowRecordNotFoundException("No record found: " + recordId);
        return record;
    }

    public String borrowBook(String memberId, String isbn) throws MemberNotFoundException, BookNotFoundException, BorrowLimitExceededException, BookNotAvailableException {
        Member member = getMember(memberId);
        Book book = getBook(isbn);

        if (!member.canBorrow())
            throw new BorrowLimitExceededException("Borrow Limit Exceeded");

        book.borrowCopy();

        LocalDate borrowDate = LocalDate.now().minusDays(50);
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

    public double returnBook(String recordId) throws BorrowRecordNotFoundException, BookAlreadyReturnedException {
        BorrowRecord record;
        if (borrowRecords.containsKey(recordId))
            record = borrowRecords.get(recordId);
        else
            throw new BorrowRecordNotFoundException("No record found with recordId: " + recordId);

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

    public List<Book> searchByGenre(Genre genre) {
        return books.values().stream()
                .filter(b -> b.getGenre() == genre)
                .collect(Collectors.toList());
    }

    public List<BorrowRecord> getOverdueRecords(){
        return borrowRecords.values().stream()
                .filter(BorrowRecord::isOverdue)
                .collect(Collectors.toList());
    }

    public List<BorrowRecord> getMemberBorrowHistory(String memberId)
            throws MemberNotFoundException {
        getMember(memberId);  // throws if not found
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