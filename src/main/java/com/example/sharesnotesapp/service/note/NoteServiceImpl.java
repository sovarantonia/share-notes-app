package com.example.sharesnotesapp.service.note;

import com.example.sharesnotesapp.model.FileType;
import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.Tag;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.NoteRequestDto;
import com.example.sharesnotesapp.model.dto.response.GradeSummaryDto;
import com.example.sharesnotesapp.repository.NoteRepository;
import com.example.sharesnotesapp.repository.TagRepository;
import com.example.sharesnotesapp.repository.UserRepository;

import com.example.sharesnotesapp.service.tag.TagService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.TreeMap;
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final TagService tagService;

    @Override
    public Note saveNote(Long userId, NoteRequestDto noteRequestDto) {
        User associatedUser = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        if (noteRequestDto.getGrade() == null) {
            throw new IllegalArgumentException("Grade must be an integer between 1 and 10");
        }

        Note createdNote = Note.builder()
                .user(associatedUser)
                .title(noteRequestDto.getTitle())
                .text(noteRequestDto.getText())
                .date(noteRequestDto.getDate())
                .grade(noteRequestDto.getGrade())
                .build();

        Set<Tag> tags = tagService.findOrCreateTagsForUser(associatedUser, noteRequestDto.getTags());
        createdNote.setTags(tags);

        return noteRepository.save(createdNote);
    }

    @Override
    public void deleteNote(Long id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Note does not exist"));
        note.getTags().clear();
        noteRepository.save(note);
        noteRepository.delete(note);
    }

    @Override
    public Note updateNote(Long id, NoteRequestDto noteRequestDto) {
        Note updatedNote = noteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Note does not exist"));

        if (!noteRequestDto.getTitle().isBlank() && !noteRequestDto.getTitle().isEmpty()) {
            updatedNote.setTitle(noteRequestDto.getTitle());
        }

        if (!noteRequestDto.getText().isBlank() && !noteRequestDto.getText().isEmpty()) {
            updatedNote.setText(noteRequestDto.getText());
        }

        if (noteRequestDto.getGrade() != 0) {
            updatedNote.setGrade(noteRequestDto.getGrade());
        }

        Set<Tag> tags = tagService.findOrCreateTagsForUser(updatedNote.getUser(), noteRequestDto.getTags());
        updatedNote.setTags(tags);

        return noteRepository.save(updatedNote);
    }


    @Override
    public Optional<Note> getNoteById(Long id) {
        if (noteRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException(String.format("Note with id %s does not exist", id));
        }
        return noteRepository.findById(id);
    }

    @Override
    public List<Note> getNotesByUser(User user) {
        return noteRepository.getNotesByUserOrderByDateDesc(user);
    }

    @Override
    public List<Note> getFilteredNotesByTitle(User user, String string) {
        if (!string.isEmpty() && !string.isBlank()) {
            return noteRepository.findAllByUserAndTitleContainsIgnoreCaseOrderByDateDesc(user, string);
        }

        return noteRepository.getNotesByUserOrderByDateDesc(user);
    }

    @Override
    public List<Note> getLatestNotes(User user) {
        return noteRepository.getFirst5ByUserOrderByDateDesc(user);
    }

    @Override
    public List<GradeSummaryDto> getNotesBetweenDates(LocalDate startDate, LocalDate endDate, User user) {
        List<Note> notes = noteRepository.getNotesByUserAndDateBetweenOrderByDateAsc(user, startDate, endDate);

        return notes.stream()
                .collect(Collectors.groupingBy(
                        Note::getDate,
                        TreeMap::new,
                        Collectors.averagingInt(Note::getGrade)
                ))
                .entrySet().stream()
                .map(entry -> new GradeSummaryDto(
                        entry.getKey(),
                        entry.getValue()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public HttpHeaders downloadNote(Note note, FileType type) {
        HttpHeaders headers = new HttpHeaders();
        String filename = buildFileName(note, type);

        if (type.equals(FileType.txt)) {
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentLength(createTextFileContent(note).getBytes().length);
        } else if (type.equals(FileType.pdf)) {
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(createPdfContent(note).length);
        } else if (type.equals(FileType.docx)) {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(createDocxContent(note).length);
        }

        headers.setContentDisposition(ContentDisposition
                .attachment()
                .filename(filename)
                .build());

        return headers;
    }


    @Override
    public String createTextFileContent(Note note) {
        return "Title: " + note.getTitle() + " " + note.getDate() + "\n\n" +
                "Content: " + "\n" + note.getText() + "\n\n" +
                "Grade: " + note.getGrade();
    }

    @Override
    public byte[] createPdfContent(Note note) {
        Document document = new Document();
        byte[] pdfBytes;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            document.add(new Paragraph("Title: " + note.getTitle()));
            document.add(new Paragraph("Date: " + note.getDate()));
            document.add(new Paragraph("Content:"));
            document.add(new Paragraph(note.getText()));
            document.add(new Paragraph("Grade: " + note.getGrade()));

            document.close();
            pdfBytes = byteArrayOutputStream.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Error creating PDF document", e);
        }

        return pdfBytes;
    }

    @Override
    public byte[] createDocxContent(Note note) {
        try (XWPFDocument document = new XWPFDocument()) {
            // Create a title paragraph
            XWPFParagraph titleParagraph = document.createParagraph();
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setBold(true);
            titleRun.setText("Title: " + note.getTitle());
            titleRun.addBreak();

            // Create a date paragraph
            XWPFParagraph dateParagraph = document.createParagraph();
            XWPFRun dateRun = dateParagraph.createRun();
            dateRun.setText("Date: " + note.getDate());
            dateRun.addBreak();

            // Create a content paragraph
            XWPFParagraph contentParagraph = document.createParagraph();
            XWPFRun contentRun = contentParagraph.createRun();
            contentRun.setText("Content:");
            contentRun.addBreak();
            contentRun.setText(note.getText());
            contentRun.addBreak();

            //Create grade paragraph
            XWPFParagraph gradeParagraph = document.createParagraph();
            XWPFRun gradeRun = gradeParagraph.createRun();
            gradeRun.setText("Grade: " + note.getGrade());

            // Write the document to a byte array output stream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error while creating DOCX content", e);
        }
    }

    @Override
    public String buildFileName(Note note, FileType type) {
        return "note_" + note.getTitle() + "_" + note.getDate() + "." + type.toString();
    }

    @Override
    public List<Note> getAllNotesByTag(User user, List<String> tags) {
        return noteRepository.findDistinctByUserAndTagsNameIn(user, tags);
    }

    @Override
    public List<Note> searchNotes(User user, String title, String tag, Integer grade, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = noteRepository.findMinDateByUserId(user.getId());
            if (startDate == null) {startDate = LocalDate.of(2000, 1, 1);}
        }

        if (endDate == null) {
            endDate = noteRepository.findMaxDateByUserId(user.getId());
            if (endDate == null) {endDate = LocalDate.of(2100, 1, 1);}
        }


        return noteRepository.search(user.getId(), title, tag, grade, startDate, endDate);
    }
}
