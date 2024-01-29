package io.lanki.noteservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.lanki.noteservice.config.DataConfig;
import io.lanki.noteservice.domain.Note.NoteType;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(DataConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("integration")
public class NoteRepositoryTests {

  @Autowired private NoteRepository noteRepository;

  @Autowired private TestEntityManager testEntityManager;

  @BeforeEach
  public void setup() {
    noteRepository.deleteAll();
    testEntityManager.clear();
    testEntityManager.flush();
  }

  @Test
  @DisplayName("Test find all with more than one note")
  public void testFindAll() {
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

    testEntityManager.persist(n1);
    testEntityManager.persist(n2);

    Iterable<Note> notes = noteRepository.findAll();

    assertThat(
            StreamSupport.stream(notes.spliterator(), true)
                .filter(
                    note ->
                        note.getTitle().equals(n1.getTitle())
                            || note.getTitle().equals(n2.getTitle()))
                .collect(Collectors.toList()))
        .hasSize(2);
  }

  @Test
  @DisplayName("Test find all with no note")
  public void testFindAllEmpty() {
    Iterable<Note> notes = noteRepository.findAll();
    assertThat(notes).hasSize(0);
  }

  @Test
  @DisplayName("Test find note with existing ID")
  public void testFindByIdExists() {
    var note =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    var noteId = (Long) testEntityManager.persistAndGetId(note);

    Optional<Note> result = noteRepository.findById(noteId);

    assertThat(result).isPresent();
    assertThat(result.get().getTitle()).isEqualTo(note.getTitle());
  }

  @Test
  @DisplayName("Test find note with non-existing ID")
  public void testFindByIdNotExist() {
    var note =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    testEntityManager.persist(note);

    Optional<Note> result = noteRepository.findById(123L);

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Test save note unauthenticated")
  public void testSaveUnauthenticated() {
    var note =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    Note result = noteRepository.save(note);

    assertThat(result.getTitle()).isEqualTo(note.getTitle());
    assertThat(result.getContent()).isEqualTo(note.getContent());
    assertThat(result.getType()).isEqualTo(note.getType());
    assertThat(result.getScore()).isEqualTo(note.getScore());
    assertThat(result.getCreatedBy()).isNull();
    assertThat(result.getLastModifiedBy()).isNull();
  }

  @Test
  @DisplayName("Test save note authenticated")
  @WithMockUser("bob")
  public void testSaveAuthenticated() {
    var note =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    Note result = noteRepository.save(note);

    assertThat(result.getTitle()).isEqualTo(note.getTitle());
    assertThat(result.getContent()).isEqualTo(note.getContent());
    assertThat(result.getType()).isEqualTo(note.getType());
    assertThat(result.getScore()).isEqualTo(note.getScore());
    assertThat(result.getCreatedBy()).isEqualTo("bob");
    assertThat(result.getLastModifiedBy()).isEqualTo("bob");
  }

  @Test
  @DisplayName("Test delete note by ID")
  public void testDeleteById() {
    var note =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    var noteId = (Long) testEntityManager.persistAndGetId(note);

    noteRepository.deleteById(noteId);

    assertThat(testEntityManager.find(Note.class, noteId)).isNull();
  }
}
