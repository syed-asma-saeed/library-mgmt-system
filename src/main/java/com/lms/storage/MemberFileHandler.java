package com.lms.storage;

import com.lms.models.Book;
import com.lms.models.Member;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MemberFileHandler{
    private static final String FILE_PATH = "data/members.csv";

    public void saveAll(List<Member> members) {
        new File("data").mkdirs();

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))){
            for(Member m: members){
                bw.write(m.toCSV());
                bw.newLine();
            }
        }catch (IOException e){
            System.out.println("Error saving books file: " + e.getMessage());        }
    }

    public List<Member> loadAll(){
        List<Member> list = new ArrayList<>();

        File file = new File(FILE_PATH);
        if (!file.exists()) return list;

        try(BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))){
            String line;
            while((line = br.readLine()) != null){
                if(line.trim().isEmpty())
                    continue;
                list.add(Member.fromCSV(line));
            }
        }catch (IOException e) {
            System.out.println("Error reading books file: " + e.getMessage());
        }
        return list;
    }

}