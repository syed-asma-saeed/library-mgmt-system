package com.lms.storage;

import com.lms.models.Book;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BookFileHandler{
    private static final String FILE_PATH = "data/books.csv";

    public void saveAll(List<Book> books) {
        new File("data").mkdirs();

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))){
            for(Book b: books){
                bw.write(b.toCSV());
                bw.newLine();
            }
        }catch (IOException e){
            System.out.println("Error saving books file: " + e.getMessage());        }
    }

    public List<Book> loadAll(){
        List<Book> list = new ArrayList<>();

        File file = new File(FILE_PATH);
        if (!file.exists()) return list;

        try(BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))){
            String line;
            while((line = br.readLine()) != null){
                if(line.trim().isEmpty())
                    continue;
                list.add(Book.fromCSV(line));
            }
        }catch (IOException e) {
            System.out.println("Error reading books file: " + e.getMessage());
        }
        return list;
    }

}