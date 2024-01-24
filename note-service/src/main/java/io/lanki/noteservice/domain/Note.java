package io.lanki.noteservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "notes")
@EntityListeners(AuditingEntityListener.class)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class Note {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotBlank(message = "title must be defined")
  private String title;

  @NotBlank(message = "content must be defined")
  private String content;

  @NotNull(message = "type must be defined")
  @Enumerated(EnumType.STRING)
  private NoteType type;

  private int reviews;

  private Instant nextReviewDate;

  @PositiveOrZero(message = "score must be defined")
  @Max(100)
  private int score;

  private String url;

  @CreatedDate
  private Instant createdDate;

  @LastModifiedDate
  private Instant lastModifiedDate;

  @Version
  private int version;

  public enum NoteType {
    LEETCODE,
    BEHAVIOURAL,
    PERSONAL
  }
}
