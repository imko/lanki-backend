package io.lanki.noteservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
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

  @JsonProperty("next_review_date")
  private Instant nextReviewDate;

  @PositiveOrZero(message = "score must be greater than or equal to 0")
  @Max(100)
  private int score;

  private String url;

  @CreatedDate
  @JsonProperty("created_date")
  private Instant createdDate;

  @LastModifiedDate
  @JsonProperty("last_modified_date")
  private Instant lastModifiedDate;

  @CreatedBy
  @JsonProperty("created_by")
  private String createdBy;

  @LastModifiedBy
  @JsonProperty("last_modified_by")
  private String lastModifiedBy;

  @Version private int version;

  public enum NoteType {
    LEETCODE,
    BEHAVIOURAL,
    PERSONAL
  }
}
