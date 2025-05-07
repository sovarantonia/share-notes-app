package com.example.sharesnotesapp.service.note;

import com.example.sharesnotesapp.model.FileType;
import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.NoteRequestDto;
import com.itextpdf.text.DocumentException;
import org.springframework.http.HttpHeaders;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface NoteService {
    Note saveNote(Long id, NoteRequestDto noteRequestDto);

    void deleteNote(Long id);

    Note updateNote(Long id, NoteRequestDto noteRequestDto);
    Optional<Note> getNoteById(Long id);

    List<Note> getNotesByUser(User user);

    List<Note> getFilteredNotesByTitle(User user, String string);

    List<Note> getLatestNotes(User user);

    List<Note> getNotesBetweenDates(LocalDate startDate, LocalDate endDate);

    HttpHeaders downloadNote(Note note, FileType type);

    String createTextFileContent(Note note);

    byte[] createPdfContent(Note note);

    byte[] createDocxContent(Note note);
    String buildFileName(Note note, FileType type);
    List<Note> getAllNotesByTag(User user, List<String> tags);
    List<Note> searchNotes(User user, String title, String tag, Integer grade, LocalDate startDate, LocalDate endDate);
}
