package com.lms.models;

import com.lms.enums.Genre;
import com.lms.exceptions.BookAlreadyReturnedException;
import com.lms.exceptions.BookNotAvailableException;

public class Book {
    static String isbn;
    String title;
    String author;
    Genre.genre genre;
    int totalCopies;
    int availableCopies;

    Book(String isbn, String title, String author, Genre.genre genre, int totalCopies, int availableCopies){
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = availableCopies;
        this.availableCopies = availableCopies;
    }

    public void borrowCopy(){
        if(availableCopies <= 0){
            throw new BookNotAvailableException("This book is not available at the moment.");
        }else{
            availableCopies--;
        }
    }

    public void returnCopy() throws BookAlreadyReturnedException{
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
        return new Book(parts[0], parts[1], parts[2], Genre.genre.valueOf(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
    }

    @Override
    public String toString(){
        return "ISBN: " + isbn + "\nTitle: " + title + "\nAuthor: " + author + "\nGenre: " + genre.getDisplayName() + "\nAvailableCopies: " + availableCopies + "\nTotalCopies: " + totalCopies;
    }
}