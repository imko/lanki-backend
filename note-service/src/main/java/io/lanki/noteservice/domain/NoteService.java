package io.lanki.noteservice.domain;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NoteService {

  private final NoteRepository noteRepository;

  public Iterable<Note> getAll() {
    return noteRepository.findAll();
  }

  public Note get(Long id) {
    return noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
  }

  public Note post(Note note) {
    return noteRepository.save(note);
  }

  public Note put(Long id, Note note) {
    return noteRepository
        .findById(id)
        .map(
            existingNote -> {
              var noteToUpdate =
                  Note.builder()
                      .id(existingNote.getId())
                      .title(note.getTitle())
                      .content(note.getContent())
                      .type(note.getType())
                      .reviews(note.getReviews())
                      .nextReviewDate(note.getNextReviewDate())
                      .score(note.getScore())
                      .url(note.getUrl())
                      .createdDate(existingNote.getCreatedDate())
                      .lastModifiedDate(existingNote.getLastModifiedDate())
                      .createdBy(existingNote.getCreatedBy())
                      .lastModifiedBy(existingNote.getLastModifiedBy())
                      .version(existingNote.getVersion())
                      .build();
              return noteRepository.save(noteToUpdate);
            })
        .orElseGet(() -> post(note));
  }

  public void delete(Long id) {
    noteRepository.deleteById(id);
  }
}
