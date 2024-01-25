package io.lanki.noteservice.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lanki.noteservice.domain.Note;
import io.lanki.noteservice.domain.Note.NoteType;
import io.lanki.noteservice.domain.NoteService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NoteController.class)
public class NoteControllerMvcTests {

  @Autowired private MockMvc mockMvc;

  @MockBean private NoteService noteService;

  @Test
  @DisplayName("Test get an empty list of notes")
  public void testGetAllEmpty() throws Exception {
    given(noteService.getAll()).willReturn(Collections.emptyList());

    mockMvc
        .perform(get("/v1/api/notes"))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  @DisplayName("Test get a list of notes with only one")
  public void testGetAllOnlyOne() throws Exception {
    var note =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    given(noteService.getAll()).willReturn(List.of(note));

    mockMvc
        .perform(get("/v1/api/notes"))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isNotEmpty())
        .andExpect(jsonPath("$[0].title", is(note.getTitle())))
        .andExpect(jsonPath("$[0].content", is(note.getContent())))
        .andExpect(jsonPath("$[0].type", is(note.getType().toString())))
        .andExpect(jsonPath("$[0].score", is(note.getScore())));
  }

  @Test
  @DisplayName("Test get a list of notes with more than one")
  public void testGetAllMoreThanOne() throws Exception {
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

    given(noteService.getAll()).willReturn(List.of(n1, n2));

    mockMvc
        .perform(get("/v1/api/notes"))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isNotEmpty())
        .andExpect(jsonPath("$[0].title", is(n1.getTitle())))
        .andExpect(jsonPath("$[0].content", is(n1.getContent())))
        .andExpect(jsonPath("$[0].type", is(n1.getType().toString())))
        .andExpect(jsonPath("$[0].score", is(n1.getScore())))
        .andExpect(jsonPath("$[1].title", is(n2.getTitle())))
        .andExpect(jsonPath("$[1].content", is(n2.getContent())))
        .andExpect(jsonPath("$[1].type", is(n2.getType().toString())))
        .andExpect(jsonPath("$[1].score", is(n2.getScore())));
  }

  @Test
  @DisplayName("Test post with all fields correct")
  public void testPostAllFieldsCorrect() throws Exception {
    var note =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    given(noteService.post(note)).willReturn(note);

    mockMvc
        .perform(
            post("/v1/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(note)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title", is(note.getTitle())))
        .andExpect(jsonPath("$.content", is(note.getContent())))
        .andExpect(jsonPath("$.type", is(note.getType().toString())))
        .andExpect(jsonPath("$.score", is(note.getScore())));
  }

  @Test
  @DisplayName("Test post when title is not defined")
  public void testPostTitleNotDefined() throws Exception {
    var note = Note.builder().content("content").type(NoteType.PERSONAL).score(100).build();

    given(noteService.post(note)).willReturn(null);

    mockMvc
        .perform(
            post("/v1/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(note)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title", is("title must be defined")));
  }

  @Test
  @DisplayName("Test post when content is not defined")
  public void testPostContentNotDefined() throws Exception {
    var note = Note.builder().title("title").type(NoteType.PERSONAL).score(100).build();

    given(noteService.post(note)).willReturn(null);

    mockMvc
        .perform(
            post("/v1/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(note)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", is("content must be defined")));
  }

  @Test
  @DisplayName("Test post when type is not defined")
  public void testPostTypeNotDefined() throws Exception {
    var note = Note.builder().title("title").content("content").score(100).build();

    given(noteService.post(note)).willReturn(null);

    mockMvc
        .perform(
            post("/v1/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(note)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.type", is("type must be defined")));
  }

  @Test
  @DisplayName("Test post when score is not defined")
  public void testPostScoreNotDefined() throws Exception {
    var note = Note.builder().title("title").content("content").type(NoteType.PERSONAL).build();

    given(noteService.post(note)).willReturn(note);

    mockMvc
        .perform(
            post("/v1/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(note)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title", is(note.getTitle())))
        .andExpect(jsonPath("$.content", is(note.getContent())))
        .andExpect(jsonPath("$.type", is(note.getType().toString())))
        .andExpect(jsonPath("$.score", is(0)));
  }

  @Test
  @DisplayName("Test put with all fields correct")
  public void testPutAllFieldsCorrect() throws Exception {
    var noteId = 1L;
    var note =
        Note.builder().title("title").content("content").type(NoteType.PERSONAL).score(100).build();

    given(noteService.put(noteId, note)).willReturn(note);

    mockMvc
        .perform(
            put("/v1/api/notes/" + noteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(note)))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title", is(note.getTitle())))
        .andExpect(jsonPath("$.content", is(note.getContent())))
        .andExpect(jsonPath("$.type", is(note.getType().toString())))
        .andExpect(jsonPath("$.score", is(note.getScore())));
  }

  @Test
  @DisplayName("Test delete")
  public void testDelete() throws Exception {
    mockMvc.perform(delete("/v1/api/notes/1")).andExpect(status().isNoContent());
  }

  public static String asJsonString(Object o) {
    try {
      return new ObjectMapper().writeValueAsString(o);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
