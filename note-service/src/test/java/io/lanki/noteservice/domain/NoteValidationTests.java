package io.lanki.noteservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.lanki.noteservice.domain.Note.NoteType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NoteValidationTests {

  private static Validator validator;

  @BeforeAll
  public static void setup() {
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @Test
  @DisplayName("Test when all fields are correct and validation succeeds")
  public void testAllFieldsCorrect() {
    var note =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();
    Set<ConstraintViolation<Note>> violations = validator.validate(note);
    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Test when title is not defined and validation fails")
  public void testTitleNotDefined() {
    var note = Note.builder().content("content").type(NoteType.PERSONAL).score(100).build();
    Set<ConstraintViolation<Note>> violations = validator.validate(note);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("title must be defined");
  }

  @Test
  @DisplayName("Test when title is empty and validation fails")
  public void testTitleEmpty() {
    var note =
        Note.builder().title("").content("content").type(NoteType.PERSONAL).score(100).build();
    Set<ConstraintViolation<Note>> violations = validator.validate(note);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("title must be defined");
  }

  @Test
  @DisplayName("Test when content is not defined and validation fails")
  public void testContentNotDefined() {
    var note = Note.builder().title("title").type(NoteType.PERSONAL).score(100).build();
    Set<ConstraintViolation<Note>> violations = validator.validate(note);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("content must be defined");
  }

  @Test
  @DisplayName("Test when content is empty and validation fails")
  public void testContentEmpty() {
    var note = Note.builder().title("title").content("").type(NoteType.PERSONAL).score(100).build();
    Set<ConstraintViolation<Note>> violations = validator.validate(note);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("content must be defined");
  }

  @Test
  @DisplayName("Test when type is not defined and validation fails")
  public void testTypeNotDefined() {
    var note = Note.builder().title("title").content("content").score(100).build();
    Set<ConstraintViolation<Note>> violations = validator.validate(note);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("type must be defined");
  }

  @Test
  @DisplayName("Test when score is not positive or zero and validation fails")
  public void testScoreNotPositiveOrZero() {
    var note =
        Note.builder().title("title").content("content").score(-1).type(NoteType.PERSONAL).build();
    Set<ConstraintViolation<Note>> violations = validator.validate(note);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("score must be greater than or equal to 0");
  }

  @Test
  @DisplayName("Test when score is greater than max and validation fails")
  public void testScoreGreaterThanMax() {
    var note =
        Note.builder().title("title").content("content").score(101).type(NoteType.PERSONAL).build();
    Set<ConstraintViolation<Note>> violations = validator.validate(note);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("must be less than or equal to 100");
  }
}
