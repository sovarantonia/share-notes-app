package com.example.sharesnotesapp.controller;

import com.example.sharesnotesapp.model.FileType;
import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.mapper.NoteMapper;
import com.example.sharesnotesapp.model.dto.request.NoteRequestDto;
import com.example.sharesnotesapp.model.dto.response.GradeSummaryDto;
import com.example.sharesnotesapp.model.dto.response.NoteResponseDto;
import com.example.sharesnotesapp.service.note.NoteService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@RestController
@RequestMapping("/notes")
public class NoteController {
    private final NoteService noteService;
    private final NoteMapper mapper;

    @Autowired
    public NoteController(NoteService noteService, NoteMapper mapper) {
        this.noteService = noteService;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDto> getNoteById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            Note note = noteService.getNoteById(id).orElseThrow((() -> new EntityNotFoundException("No note with such id")));

            return ResponseEntity.ok(mapper.toDto(note));
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping
    public ResponseEntity<NoteResponseDto> createNote(@RequestBody @Valid NoteRequestDto noteRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user) {
            URI uri = URI.create((ServletUriComponentsBuilder.fromCurrentContextPath().path("/notes").toUriString()));

            return ResponseEntity.created(uri).body(mapper.toDto(noteService.saveNote(user.getId(), noteRequestDto)));
        }

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNote(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            noteService.deleteNote(id);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<NoteResponseDto> updateNote(@PathVariable Long id, @RequestBody @Valid NoteRequestDto noteRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            Note updatedNote = noteService.updateNote(id, noteRequestDto);

            return ResponseEntity.ok(mapper.toDto(updatedNote));
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<NoteResponseDto>> getAllNotesByUser(@RequestParam(required = false) List<String> tagNames) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user) {
            List<Note> notes;
            if (tagNames != null && !tagNames.isEmpty()) {
                notes = noteService.getAllNotesByTag(user, tagNames);
            }
            else {
                notes = noteService.getNotesByUser(user);
            }


            return ResponseEntity.ok(notes.stream().map(mapper::toDto).toList());
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/filter-title")
    public ResponseEntity<List<NoteResponseDto>> getNotesFilteredByTitle(@RequestParam(defaultValue = "") String string) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user) {
            List<Note> filteredNotes = noteService.getFilteredNotesByTitle(user, string);

            return ResponseEntity.ok(filteredNotes.stream().map(mapper::toDto).toList());
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<NoteResponseDto>> searchNotes(@RequestParam(required = false) String title,
                                                             @RequestParam(required = false) String tag,
                                                             @RequestParam(required = false) Integer grade,
                                                             @RequestParam(required = false) @DateTimeFormat(fallbackPatterns = "dd-MM-yyyy") LocalDate from,
                                                             @RequestParam(required = false) @DateTimeFormat(fallbackPatterns = "dd-MM-yyyy") LocalDate to) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user) {
            List<Note> notes = noteService.searchNotes(user, title, tag, grade, from, to);
            return ResponseEntity.ok(notes.stream().map(mapper::toDto).toList());
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/latest")
    public ResponseEntity<List<NoteResponseDto>> getLatestNotes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user) {
            List<Note> latestNotes = noteService.getLatestNotes(user);

            return ResponseEntity.ok(latestNotes.stream().map(mapper::toDto).toList());
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/dates")
    public ResponseEntity<List<GradeSummaryDto>> getNotesBetweenDates
            (@RequestParam("startDate") @DateTimeFormat(fallbackPatterns = "dd-MM-yyyy") LocalDate startDate,
             @RequestParam("endDate") @DateTimeFormat(fallbackPatterns = "dd-MM-yyyy") LocalDate endDate) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user) {

            return ResponseEntity.ok(noteService.getNotesBetweenDates(startDate, endDate, user));
        }

        return ResponseEntity.badRequest().build();
    }


    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadNotes(
            @RequestParam List<Long> ids,
            @RequestParam String type) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {

            byte[] fileContent;
            FileType fileType;
            try {
                fileType = FileType.valueOf(type);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid file type".getBytes());
            }

            if (ids.size() == 1) {
                Note note = noteService.getNoteById(ids.get(0)).orElseThrow((() -> new EntityNotFoundException("No note with such id")));
                fileContent = new byte[0];

                if (fileType.equals(FileType.txt)) {
                    fileContent = noteService.createTextFileContent(note).getBytes();
                } else if (fileType.equals(FileType.pdf)) {
                    fileContent = noteService.createPdfContent(note);
                } else if (fileType.equals(FileType.docx)) {
                    fileContent = noteService.createDocxContent(note);
                } else {
                    ResponseEntity.badRequest().body("Invalid file type");
                }

                HttpHeaders headers = noteService.downloadNote(note, FileType.valueOf(type));
                headers.add("Access-Control-Expose-Headers", "Content-Disposition");

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(fileContent);

            }

            else if (ids.size() > 1) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(baos);

                for (Long id : ids) {
                    Note note = noteService.getNoteById(id).orElseThrow(((() -> new EntityNotFoundException("No note with such id"))));

                    fileContent = new byte[0];
                    String fileName = noteService.buildFileName(note, fileType);

                    if (fileType.equals(FileType.txt)) {
                        fileContent = noteService.createTextFileContent(note).getBytes();
                    } else if (fileType.equals(FileType.pdf)) {
                        fileContent = noteService.createPdfContent(note);
                    } else if (fileType.equals(FileType.docx)) {
                        fileContent = noteService.createDocxContent(note);
                    } else {
                        return ResponseEntity.badRequest().body("Invalid file type".getBytes());
                    }

                    ZipEntry entry = new ZipEntry(fileName);
                    zos.putNextEntry(entry);
                    zos.write(fileContent);
                    zos.closeEntry();
                }

                zos.close();

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"notes.zip\"");
                headers.add("Access-Control-Expose-Headers", "Content-Disposition");

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(baos.toByteArray());
            }

            else if (ids.isEmpty()) {
                return ResponseEntity.badRequest().body("No notes selected".getBytes());
            }
        }

        return ResponseEntity.badRequest().build();
    }
}
