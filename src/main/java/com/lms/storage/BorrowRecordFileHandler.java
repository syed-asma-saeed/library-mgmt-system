package com.lms.storage;

import com.lms.models.Book;
import com.lms.models.BorrowRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowRecordFileHandler{
    private static final String FILE_PATH = "data/borrow_records.csv";

    public void saveAll(List<BorrowRecord> borrowRecord) {
        new File("data").mkdirs();

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))){
            for(BorrowRecord b: borrowRecord){
                bw.write(b.toCSV());
                bw.newLine();
            }
        }catch (IOException e){
            System.out.println("Error saving books file: " + e.getMessage());        }
    }

    public List<BorrowRecord> loadAll(){
        List<BorrowRecord> list = new ArrayList<>();

        File file = new File(FILE_PATH);
        if (!file.exists()) return list;

        try(BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))){
            String line;
            while((line = br.readLine()) != null){
                if(line.trim().isEmpty())
                    continue;
                list.add(BorrowRecord.fromCSV(line));
            }
        }catch (IOException e) {
            System.out.println("Error reading books file: " + e.getMessage());
        }
        return list;
    }

}