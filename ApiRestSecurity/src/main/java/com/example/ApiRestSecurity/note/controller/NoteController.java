package com.example.ApiRestSecurity.note.controller;

import com.example.ApiRestSecurity.note.model.Note;
import com.example.ApiRestSecurity.note.repositories.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/note")
@RequiredArgsConstructor
public class NoteController {

    private final NoteRepository repository;

    @GetMapping
    public ResponseEntity<List<Note>> getAll(){
        return buildResponseOfAList(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getById(Long id){
        return ResponseEntity.of(repository.findById(id));
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<Note>> getByAuthor(@PathVariable String author){
        return buildResponseOfAList(repository.findByAuthor(author));
    }

    @PostMapping
    public ResponseEntity<Note> saveNote(Note note){
        Note created= repository.save(note);

        URI createdURI= ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId()).toUri();

        return ResponseEntity
                .created(createdURI)
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> edit(@PathVariable Long id, @RequestBody Note note){
        return ResponseEntity.of(
                repository.findById(id)
                        .map(editNote->{
                            editNote.setTitle(note.getTitle());
                            editNote.setContent(note.getContent());
                            editNote.setAuthor(note.getAuthor());
                            editNote.setImportant(note.isImportant());
                            return repository.save(editNote);
                        })
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<List<Note>> buildResponseOfAList(List<Note> list){
        if(list.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }
}
