package io.lanki.noteservice.web;

import io.lanki.noteservice.domain.Note;
import io.lanki.noteservice.domain.NoteService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/v1/api/notes", produces = "application/json")
@AllArgsConstructor
public class NoteController {

  private final NoteService noteService;

  @GetMapping
  public Iterable<Note> get() {
    return noteService.getAll();
  }

  @GetMapping("/{id}")
  public Note getById(@PathVariable("id") Long id) {
    return noteService.get(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Note post(@Valid @RequestBody Note note) {
    return noteService.post(note);
  }

  @PutMapping("/{id}")
  public Note put(@PathVariable("id") Long id, @Valid @RequestBody Note note) {
    return noteService.put(id, note);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") Long id) {
    noteService.delete(id);
  }
}
