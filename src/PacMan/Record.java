package PacMan;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;

public class Record implements Serializable,Comparable<Record>{
    private final String name;
    private final int score;
    private static final String recordsFilePath="src/records.dat";
    public Record(String name, int score){
        this.name=name;
        this.score=score;
    }
    public int getScore(){

        return score;
    }
    public String getName(){

        return name;
    }

    public static void addRecord(String name, int score) {
        List<Record>records=loadRecords();
        records.add(new Record(name, score));
        Collections.sort(records);
        saveRecords(records);
    }
    private static void saveRecords(List<Record>records) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(recordsFilePath))) {
            outputStream.writeObject(records);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving records: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static List<Record> loadRecords() {
        File file = new File(recordsFilePath);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Record>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "You haven't got any records yet!" ,"Error", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }



    @Override
    public int compareTo(Record other) {
        return other.score-this.score;
    }
}
