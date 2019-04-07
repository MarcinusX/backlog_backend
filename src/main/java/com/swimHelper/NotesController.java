package com.swimHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class NotesController {

    @Autowired
    NotesRepository repository;

    @GetMapping
    public List<Note> getItems() {
        return repository.findAll();
    }


    final Long[] colors = {
            Long.decode("0xFFF44336"),
            Long.decode("0xFFFFEB3B"),
            Long.decode("0xFF4CAF50"),
            Long.decode("0xFF2196F3")

    };

    @PostMapping
    public ResponseEntity addItem(@RequestBody Note note) {
        if (note.getAuthor() == null || note.getAuthor().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Missing author");
        } else if (note.getText() == null || note.getText().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Missing text");
        } else {
            Note note1 = new Note();
            note1.setText(note.getText());
            note1.setAuthor(note.getAuthor());
            int randomNum = ThreadLocalRandom.current().nextInt(0, 4);
            note1.setColor(colors[randomNum]);
            note1.setLikes(0);
            note1 = repository.saveAndFlush(note1);
            return ResponseEntity.ok(note1);
        }
    }

    @PutMapping
    public ResponseEntity like(@RequestParam(name = "id", required = true) Long id) {
        Note note = repository.findOne(id);
        if (note == null) {
            return ResponseEntity.badRequest().body("Wrong id");
        } else {
            note.setLikes(note.getLikes() + 1);
            repository.saveAndFlush(note);
            return ResponseEntity.ok(note);
        }
    }
}
