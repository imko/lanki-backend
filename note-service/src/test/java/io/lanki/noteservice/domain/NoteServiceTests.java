package io.lanki.noteservice.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.lanki.noteservice.domain.Note.NoteType;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTests {

  @Mock private NoteRepository noteRepository;

  @InjectMocks private NoteService noteService;

  @Test
  @DisplayName("Test get an empty list of notes")
  public void testGetAllEmpty() {
    when(noteRepository.findAll()).thenReturn(Collections.emptyList());
    assertThat(noteService.getAll()).hasSize(0);
  }

  @Test
  @DisplayName("Test get a list of notes")
  public void testGetAllOnlyOne() {
    var note =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    when(noteRepository.findAll()).thenReturn(List.of(note));

    assertThat(
            StreamSupport.stream(noteService.getAll().spliterator(), true)
                .filter(n -> n.getTitle().equals(note.getTitle()))
                .collect(Collectors.toList()))
        .hasSize(1);
  }

  @Test
  @DisplayName("Test get a list of notes")
  public void testGetAllMoreThanOne() {
    var n1 =
        Note.builder()
            .title("title_1")
            .content("content_1")
            .type(NoteType.PERSONAL)
            .score(100)
            .build();
    var n2 =
        Note.builder()
            .title("title_2")
            .content("content_2")
            .type(NoteType.LEETCODE)
            .score(50)
            .build();

    when(noteRepository.findAll()).thenReturn(List.of(n1, n2));

    assertThat(
            StreamSupport.stream(noteService.getAll().spliterator(), true)
                .filter(
                    n -> n.getTitle().equals(n1.getTitle()) || n.getTitle().equals(n2.getTitle()))
                .collect(Collectors.toList()))
        .hasSize(2);
  }

  @Test
  @DisplayName("Test get note by existing ID")
  public void testGetByIdExists() {
    var note =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

    Note actualNote = noteService.get(1L);

    assertThat(actualNote).isNotNull();
    assertThat(actualNote.getTitle()).isEqualTo(note.getTitle());
    assertThat(actualNote.getContent()).isEqualTo(note.getContent());
    assertThat(actualNote.getType()).isEqualTo(note.getType());
    assertThat(actualNote.getScore()).isEqualTo(note.getScore());
  }

  @Test
  @DisplayName("Test get note by non-existing ID")
  public void testGetByIdNotExist() {
    when(noteRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> noteService.get(1L))
        .isInstanceOf(NoteNotFoundException.class)
        .hasMessage("note with ID 1 was not found");
  }

  @Test
  @DisplayName("Test save note with all fields correct")
  public void testSaveAllFieldsCorrect() {
    var note =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    when(noteRepository.save(note)).thenReturn(note);

    Note actualNote = noteService.post(note);

    assertThat(actualNote).isNotNull();
    assertThat(actualNote.getTitle()).isEqualTo(note.getTitle());
    assertThat(actualNote.getContent()).isEqualTo(note.getContent());
    assertThat(actualNote.getType()).isEqualTo(note.getType());
    assertThat(actualNote.getScore()).isEqualTo(note.getScore());
  }

  @Test
  @DisplayName("Test save note with all fields correct")
  public void testUpdateAllFieldCorrect() {
    var note =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    when(noteRepository.save(note)).thenReturn(note);

    Note actualNote = noteService.put(1L, note);

    assertThat(actualNote).isNotNull();
    assertThat(actualNote.getTitle()).isEqualTo(note.getTitle());
    assertThat(actualNote.getContent()).isEqualTo(note.getContent());
    assertThat(actualNote.getType()).isEqualTo(note.getType());
    assertThat(actualNote.getScore()).isEqualTo(note.getScore());
  }

}
