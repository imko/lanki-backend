package io.lanki.noteservice;

import static org.assertj.core.api.Assertions.assertThat;

import io.lanki.noteservice.domain.Note;
import io.lanki.noteservice.domain.Note.NoteType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Slf4j
class NoteServiceApplicationTests {

  @Autowired private WebTestClient webTestClient;

  @Test
  @DisplayName("Test GET request when note ID already exists")
  public void testGetRequestIdExists() {
    var noteToCreate =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    Note expectedNote =
        webTestClient
            .post()
            .uri("/v1/api/notes")
            .bodyValue(noteToCreate)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Note.class)
            .value(note -> assertThat(note).isNotNull())
            .returnResult()
            .getResponseBody();

    webTestClient
        .get()
        .uri("/v1/api/notes/" + expectedNote.getId())
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(Note.class)
        .value(
            actualBook -> {
              assertThat(actualBook).isNotNull();
              assertThat(actualBook.getTitle()).isEqualTo(expectedNote.getTitle());
              assertThat(actualBook.getContent()).isEqualTo(expectedNote.getContent());
              assertThat(actualBook.getType()).isEqualTo(expectedNote.getType());
              assertThat(actualBook.getScore()).isEqualTo(expectedNote.getScore());
            });
  }

  @Test
  @DisplayName("Test GET request when note ID doesn't exist")
  public void testGetRequestIdNotExist() {
    webTestClient.get().uri("/v1/api/notes/123").exchange().expectStatus().isNotFound();
  }

  @Test
  @DisplayName("Test POST request with all fields correct")
  public void testPostRequestAllFieldsCorrect() {
    var noteToCreate =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    webTestClient
        .post()
        .uri("/v1/api/notes")
        .bodyValue(noteToCreate)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(Note.class)
        .value(
            note -> {
              assertThat(note).isNotNull();
              assertThat(note.getTitle()).isEqualTo(noteToCreate.getTitle());
              assertThat(note.getContent()).isEqualTo(noteToCreate.getContent());
              assertThat(note.getType()).isEqualTo(noteToCreate.getType());
              assertThat(note.getScore()).isEqualTo(noteToCreate.getScore());
            });
  }

  @Test
  @DisplayName("Test POST request when title is not defined")
  public void testPostRequestTitleNotDefined() {
    var noteToCreate = Note.builder().content("content").type(NoteType.PERSONAL).score(100).build();

    webTestClient
        .post()
        .uri("/v1/api/notes")
        .bodyValue(noteToCreate)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  @DisplayName("Test POST request when content is not defined")
  public void testPostRequestContentNotDefined() {
    var noteToCreate = Note.builder().title("title").type(NoteType.PERSONAL).score(100).build();

    webTestClient
        .post()
        .uri("/v1/api/notes")
        .bodyValue(noteToCreate)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  @DisplayName("Test POST request when type is not defined")
  public void testPostRequestTypeNotDefined() {
    var noteToCreate = Note.builder().title("title").content("content").score(100).build();

    webTestClient
        .post()
        .uri("/v1/api/notes")
        .bodyValue(noteToCreate)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  @DisplayName("Test POST request when score is not defined")
  public void testPostRequestScoreNotDefined() {
    var noteToCreate =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).build();

    webTestClient
        .post()
        .uri("/v1/api/notes")
        .bodyValue(noteToCreate)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(Note.class)
        .value(
            note -> {
              assertThat(note).isNotNull();
              assertThat(note.getTitle()).isEqualTo(noteToCreate.getTitle());
              assertThat(note.getContent()).isEqualTo(noteToCreate.getContent());
              assertThat(note.getType()).isEqualTo(noteToCreate.getType());
              assertThat(note.getScore()).isEqualTo(0);
            });
  }

  @Test
  @DisplayName("Test PUT request with all fields correct with existing ID")
  public void testPutRequestAllFieldsCorrectAndIdExists() {
    var noteToCreate =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    Note expectedNote =
        webTestClient
            .post()
            .uri("/v1/api/notes")
            .bodyValue(noteToCreate)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Note.class)
            .value(
                note -> {
                  assertThat(note).isNotNull();
                  assertThat(note.getTitle()).isEqualTo(noteToCreate.getTitle());
                  assertThat(note.getContent()).isEqualTo(noteToCreate.getContent());
                  assertThat(note.getType()).isEqualTo(noteToCreate.getType());
                  assertThat(note.getScore()).isEqualTo(noteToCreate.getScore());
                })
            .returnResult()
            .getResponseBody();

    noteToCreate.setTitle("new title");
    noteToCreate.setContent("new content");
    noteToCreate.setType(NoteType.BEHAVIOURAL);
    noteToCreate.setScore(50);

    webTestClient
        .put()
        .uri("/v1/api/notes/" + expectedNote.getId())
        .bodyValue(noteToCreate)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(Note.class)
        .value(
            note -> {
              assertThat(note).isNotNull();
              assertThat(note.getTitle()).isEqualTo(noteToCreate.getTitle());
              assertThat(note.getContent()).isEqualTo(noteToCreate.getContent());
              assertThat(note.getType()).isEqualTo(noteToCreate.getType());
              assertThat(note.getScore()).isEqualTo(noteToCreate.getScore());
            });
  }

  @Test
  @DisplayName("Test PUT request when title is not defined with existing ID")
  public void testPutRequestTitleNotDefinedAndIdExists() {
    var noteToCreate =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    Note expectedNote =
        webTestClient
            .post()
            .uri("/v1/api/notes")
            .bodyValue(noteToCreate)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Note.class)
            .value(
                note -> {
                  assertThat(note).isNotNull();
                  assertThat(note.getTitle()).isEqualTo(noteToCreate.getTitle());
                  assertThat(note.getContent()).isEqualTo(noteToCreate.getContent());
                  assertThat(note.getType()).isEqualTo(noteToCreate.getType());
                  assertThat(note.getScore()).isEqualTo(noteToCreate.getScore());
                })
            .returnResult()
            .getResponseBody();

    noteToCreate.setTitle("");

    webTestClient
        .put()
        .uri("/v1/api/notes/" + expectedNote.getId())
        .bodyValue(noteToCreate)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  @DisplayName("Test PUT request when content is not defined with existing ID")
  public void testPutRequestContentNotDefinedAndIdExists() {
    var noteToCreate =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    Note expectedNote =
        webTestClient
            .post()
            .uri("/v1/api/notes")
            .bodyValue(noteToCreate)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Note.class)
            .value(
                note -> {
                  assertThat(note).isNotNull();
                  assertThat(note.getTitle()).isEqualTo(noteToCreate.getTitle());
                  assertThat(note.getContent()).isEqualTo(noteToCreate.getContent());
                  assertThat(note.getType()).isEqualTo(noteToCreate.getType());
                  assertThat(note.getScore()).isEqualTo(noteToCreate.getScore());
                })
            .returnResult()
            .getResponseBody();

    noteToCreate.setContent("");

    webTestClient
        .put()
        .uri("/v1/api/notes/" + expectedNote.getId())
        .bodyValue(noteToCreate)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  @DisplayName("Test PUT request when score is negative with existing ID")
  public void testPutRequestScoreNegativeAndIdExists() {
    var noteToCreate =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    Note expectedNote =
        webTestClient
            .post()
            .uri("/v1/api/notes")
            .bodyValue(noteToCreate)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Note.class)
            .value(
                note -> {
                  assertThat(note).isNotNull();
                  assertThat(note.getTitle()).isEqualTo(noteToCreate.getTitle());
                  assertThat(note.getContent()).isEqualTo(noteToCreate.getContent());
                  assertThat(note.getType()).isEqualTo(noteToCreate.getType());
                  assertThat(note.getScore()).isEqualTo(noteToCreate.getScore());
                })
            .returnResult()
            .getResponseBody();

    noteToCreate.setScore(-1);

    webTestClient
        .put()
        .uri("/v1/api/notes/" + expectedNote.getId())
        .bodyValue(noteToCreate)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  @DisplayName("Test PUT request with all fields correct with non-existing ID")
  public void testPutRequestAllFieldsCorrectAndIdNotExist() {
    var noteId = 123L;
    var noteToCreate =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    webTestClient
        .put()
        .uri("/v1/api/notes/" + noteId)
        .bodyValue(noteToCreate)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(Note.class)
        .value(
            note -> {
              assertThat(note).isNotNull();
              assertThat(note.getTitle()).isEqualTo(noteToCreate.getTitle());
              assertThat(note.getContent()).isEqualTo(noteToCreate.getContent());
              assertThat(note.getType()).isEqualTo(noteToCreate.getType());
              assertThat(note.getScore()).isEqualTo(noteToCreate.getScore());
            });
  }

  @Test
  @DisplayName("Test PUT request when title is not defined with non-existing ID")
  public void testPutRequestTitleNotDefinedAndIdNotExist() {
    var noteToCreate = Note.builder().content("content").type(NoteType.PERSONAL).score(100).build();

    webTestClient
        .put()
        .uri("/v1/api/notes/123")
        .bodyValue(noteToCreate)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  @DisplayName("Test PUT request when content is not defined with non-existing ID")
  public void testPutRequestContentNotDefinedAndIdNotExist() {
    var noteToCreate = Note.builder().title("title").type(NoteType.PERSONAL).score(100).build();

    webTestClient
        .put()
        .uri("/v1/api/notes/123")
        .bodyValue(noteToCreate)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  @DisplayName("Test PUT request when type is not defined with non-existing ID")
  public void testPutRequestTypeNotDefinedAndIdNotExist() {
    var noteToCreate = Note.builder().title("title").content("content").score(100).build();

    webTestClient
        .put()
        .uri("/v1/api/notes/123")
        .bodyValue(noteToCreate)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  @DisplayName("Test PUT request when score is not defined with non-existing ID")
  public void testPutRequestScoreNotDefinedAndIdNotExist() {
    var noteToCreate =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).build();

    webTestClient
        .put()
        .uri("/v1/api/notes/123")
        .bodyValue(noteToCreate)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(Note.class)
        .value(
            note -> {
              assertThat(note).isNotNull();
              assertThat(note.getTitle()).isEqualTo(noteToCreate.getTitle());
              assertThat(note.getContent()).isEqualTo(noteToCreate.getContent());
              assertThat(note.getType()).isEqualTo(noteToCreate.getType());
              assertThat(note.getScore()).isEqualTo(0);
            });
  }

  @Test
  @DisplayName("Test DELETE request")
  public void testDeleteRequest() {
    var noteToCreate =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    Note expectedNote =
        webTestClient
            .post()
            .uri("/v1/api/notes")
            .bodyValue(noteToCreate)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Note.class)
            .value(note -> assertThat(note).isNotNull())
            .returnResult()
            .getResponseBody();

    webTestClient
        .delete()
        .uri("/v1/api/notes/" + expectedNote.getId())
        .exchange()
        .expectStatus()
        .isNoContent();

    webTestClient
        .get()
        .uri("/v1/api/notes/" + expectedNote.getId())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody(String.class)
        .value(
            error -> {
              assertThat(error)
                  .isEqualTo("note with ID " + expectedNote.getId() + " was not found");
            });
  }
}
