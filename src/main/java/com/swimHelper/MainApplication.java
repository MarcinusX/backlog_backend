package com.swimHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class MainApplication implements CommandLineRunner {


    @Autowired
    private NotesRepository notesRepository;

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Override
    public void run(String... args) {
        addInitialDataToDatabase();
    }

    private void addInitialDataToDatabase() {
        Note note = new Note();
        note.setAuthor("Marcin");
        note.setText("Zróbmy refresh indicator");
        note.setColor(Long.decode("0xFFF44336"));
        note.setLikes(0);
        notesRepository.save(note);
    }
}