package com.lms.models;

import com.lms.enums.Genre;
import com.lms.exceptions.BookAlreadyReturnedException;
import com.lms.exceptions.BookNotAvailableException;

public class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private final Genre genre;
    private final int totalCopies;
    private int availableCopies;

    public Book(String isbn, String title, String author, Genre genre, int totalCopies){
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    private Book(String isbn, String title, String author, Genre genre, int totalCopies, int availableCopies){
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }



    public String getTitle(){
        return this.title;
    }

    public String getAuthor(){
        return this.author;
    }
    public Genre getGenre(){
        return this.genre;
    }

    public void borrowCopy() throws BookNotAvailableException{
        if(availableCopies <= 0){
            throw new BookNotAvailableException("This book is not available at the moment.");
        }else{
            availableCopies--;
        }
    }

    public void returnCopy(){
        if(availableCopies < totalCopies){
            availableCopies++;
        }
    }

    public boolean isAvailable(){
        return availableCopies > 0;
    }

    public String toCSV(){
        return (String.join(",",
                isbn,
                title,
                author,
                genre.name(),
                String.valueOf(totalCopies),
                String.valueOf(availableCopies)));
    }

    public static Book fromCSV(String line){
        String[] parts = line.split(",");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid CSV record: " + line);
        }
        return new Book(parts[0], parts[1], parts[2], Genre.valueOf(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
    }

    @Override
    public String toString(){
        return String.format(
                "ISBN: %s | Title: %s | Author: %s | Genre: %s | Available: %d/%d",
                isbn, title, author, genre.getDisplayName(), availableCopies, totalCopies
        );
    }

    public String getIsbn() {
        return this.isbn;
    }

    public int getAvailableCopies() {
        return this.availableCopies;
    }

    public int getTotalCopies() {
        return this.totalCopies;
    }
}