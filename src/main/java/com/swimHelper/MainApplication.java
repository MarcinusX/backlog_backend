package com.swimHelper;

import com.swimHelper.exception.BusinessException;
import com.swimHelper.model.*;
import com.swimHelper.repository.ExerciseRepository;
import com.swimHelper.repository.TrainingRepository;
import com.swimHelper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SpringBootApplication
public class MainApplication implements CommandLineRunner {


    @Autowired
    private NotesRepository notesRepository;

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

    @Override
    @Transactional
    public void run(String... args) {
        addInitialDataToDatabase();
    }

    private void addInitialDataToDatabase() {
	    Note note = new Note();
	    note.setAuthor("Marcin");
	    note.setText("Cokolwiek");
	    note.setColor(0L);
	    note.setLikes(0);
	    notesRepository.save(note);
    }
}