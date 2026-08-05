package com.lms.interfaces;

import com.lms.enums.Genre;

import java.util.List;

public interface Searchable <T> {
    public List<T> searchByTitle(String name);
    public List<T> searchByAuthor(String name);
    public List<T> searchByGenre(Genre genre);
}