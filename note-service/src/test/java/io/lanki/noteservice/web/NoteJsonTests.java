package io.lanki.noteservice.web;

import static org.assertj.core.api.Assertions.assertThat;

import io.lanki.noteservice.domain.Note;
import io.lanki.noteservice.domain.Note.NoteType;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
public class NoteJsonTests {

  @Autowired private JacksonTester<Note> jacksonTester;

  @Test
  public void testSerialize() throws Exception {
    var now = Instant.now();
    var note =
        Note.builder()
            .id(1L)
            .title("title")
            .content("content")
            .type(NoteType.PERSONAL)
            .reviews(0)
            .nextReviewDate(now)
            .score(100)
            .url("url")
            .createdDate(now)
            .lastModifiedDate(now)
            .version(1)
            .build();
    var jsonContent = jacksonTester.write(note);

    assertThat(jsonContent)
        .extractingJsonPathNumberValue("@.id")
        .isEqualTo(note.getId().intValue());
    assertThat(jsonContent).extractingJsonPathStringValue("@.title").isEqualTo(note.getTitle());
    assertThat(jsonContent).extractingJsonPathStringValue("@.content").isEqualTo(note.getContent());
    assertThat(jsonContent)
        .extractingJsonPathStringValue("@.type")
        .isEqualTo(note.getType().toString());
    assertThat(jsonContent).extractingJsonPathNumberValue("@.reviews").isEqualTo(note.getReviews());
    assertThat(jsonContent)
        .extractingJsonPathStringValue("@.next_review_date")
        .isEqualTo(note.getNextReviewDate().toString());
    assertThat(jsonContent).extractingJsonPathNumberValue("@.score").isEqualTo(note.getScore());
    assertThat(jsonContent).extractingJsonPathStringValue("@.url").isEqualTo(note.getUrl());
    assertThat(jsonContent)
        .extractingJsonPathStringValue("@.created_date")
        .isEqualTo(note.getCreatedDate().toString());
    assertThat(jsonContent)
        .extractingJsonPathStringValue("@.last_modified_date")
        .isEqualTo(note.getLastModifiedDate().toString());
    assertThat(jsonContent).extractingJsonPathNumberValue("@.version").isEqualTo(note.getVersion());
  }

  @Test
  public void testDeserialize() throws Exception {
    var instant = Instant.parse("2024-06-08T06:08:37.135029Z");
    var content =
        """
          {
            "id": 1,
            "title": "title",
            "content": "content",
            "type": "PERSONAL",
            "reviews": 0,
            "next_review_date": "2024-06-08T06:08:37.135029Z",
            "score": 100,
            "url": "url",
            "created_date": "2024-06-08T06:08:37.135029Z",
            "last_modified_date": "2024-06-08T06:08:37.135029Z",
            "version": 1
          }
        """;
    var note =
        Note.builder()
            .id(1L)
            .title("title")
            .content("content")
            .type(NoteType.PERSONAL)
            .reviews(0)
            .nextReviewDate(instant)
            .score(100)
            .url("url")
            .createdDate(instant)
            .lastModifiedDate(instant)
            .version(1)
            .build();

    assertThat(jacksonTester.parse(content)).usingRecursiveComparison().isEqualTo(note);
  }
}
