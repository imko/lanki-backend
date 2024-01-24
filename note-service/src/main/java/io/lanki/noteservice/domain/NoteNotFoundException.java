package io.lanki.noteservice.domain;

public class NoteNotFoundException extends RuntimeException {

  public NoteNotFoundException(Long id) {
    super(String.format("note with ID %d was not found", id));
  }
}
