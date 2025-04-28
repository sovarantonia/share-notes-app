package com.example.sharesnotesapp.controller_test;

import com.example.sharesnotesapp.controller.ShareController;
import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.Share;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.mapper.ShareMapper;
import com.example.sharesnotesapp.model.dto.response.NoteResponseDto;
import com.example.sharesnotesapp.model.dto.response.ShareResponseDto;
import com.example.sharesnotesapp.model.dto.response.UserInfoDto;
import com.example.sharesnotesapp.model.dto.response.UserResponseDto;
import com.example.sharesnotesapp.service.share.ShareServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(ShareController.class)
@Import(TestSecurityConfig.class)
public class ShareControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShareServiceImpl shareService;

    @MockBean
    private ShareMapper mapper;

    private Authentication authentication;

    private User sender;
    private User receiver;
    private Note note;
    private Share share;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        sender = new User(1L, "Sender", "Sender", "sender@example.com", "test123");
        receiver = new User(2L, "Receiver", "Receiver", "receiver@example.com", "test123");
        note = new Note(1L, sender, LocalDate.parse("2024-10-10"), "A title", "Some text", 9, Set.of());
        share = new Share(1L, sender, receiver, note, LocalDate.parse("2024-10-15"));

        authentication = new UsernamePasswordAuthenticationToken(sender, sender.getPassword(), Collections.emptyList());
    }

    @Test
    public void testShareNote() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String receiverEmail = "receiver@example.com";
        UserInfoDto senderResponseDto = new UserInfoDto(1L, sender.getFirstName(), sender.getLastName(), sender.getEmail());
        UserInfoDto receiverResponseDto
                = new UserInfoDto(2L, receiver.getFirstName(), receiver.getLastName(), receiver.getEmail());
        UserResponseDto senderUResponseDto = new UserResponseDto(1L, sender.getFirstName(), sender.getLastName(), sender.getEmail());
        NoteResponseDto noteResponseDto
                = new NoteResponseDto(senderUResponseDto, 1L, note.getTitle(), note.getText(), note.getDate(), note.getGrade(), Set.of());
        ShareResponseDto responseDto
                = new ShareResponseDto(senderResponseDto, receiverResponseDto, noteResponseDto, LocalDate.parse("2024-10-15"));

        when(mapper.toDto(any(Share.class))).thenReturn(responseDto);
        when(shareService.shareNote(any(User.class), any(String.class), any(Long.class))).thenReturn(share);

        mockMvc.perform(post("/share/{noteId}", note.getId())
                        .content(receiverEmail))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sender.firstName", is("Sender")))
                .andExpect(jsonPath("$.sender.lastName", is("Sender")))
                .andExpect(jsonPath("$.sender.email", is("sender@example.com")))
                .andExpect(jsonPath("$.receiver.firstName", is("Receiver")))
                .andExpect(jsonPath("$.receiver.lastName", is("Receiver")))
                .andExpect(jsonPath("$.receiver.email", is("receiver@example.com")))
                .andExpect(jsonPath("$.sentNote.user.firstName", is("Sender")))
                .andExpect(jsonPath("$.sentNote.user.lastName", is("Sender")))
                .andExpect(jsonPath("$.sentNote.user.email", is("sender@example.com")))
                .andExpect(jsonPath("$.sentAt", is("15-10-2024")));
    }

    @Test
    public void testShareNote_NotLoggedIn() throws Exception {
        String receiverEmail = "receiver@example.com";
        mockMvc.perform(post("/share/{noteId}", note.getId())
                        .content(receiverEmail))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testShareNote_InvalidEmail() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String receiverEmail = "receiver@example.com";

        when(shareService.shareNote(any(User.class), any(String.class), any(Long.class)))
                .thenThrow(new UsernameNotFoundException("User does not exist"));

        mockMvc.perform(post("/share/{noteId}", note.getId())
                        .content(receiverEmail))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User does not exist"));
    }

    @Test
    public void testShareNote_InvalidNoteId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String receiverEmail = "receiver@example.com";

        when(shareService.shareNote(any(User.class), any(String.class), any(Long.class)))
                .thenThrow(new EntityNotFoundException("Note does not exist"));

        mockMvc.perform(post("/share/{noteId}", 999L)
                        .content(receiverEmail))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Note does not exist"));
    }

    @Test
    public void testGetAllSharedNotesBetweenUsers_EmptyEmail() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = mock(User.class);
        Share mockedShare = new Share(2L, sender, user, note, LocalDate.parse("2024-10-16"));

        UserInfoDto senderResponseDto = new UserInfoDto(1L, sender.getFirstName(), sender.getLastName(), sender.getEmail());
        UserInfoDto receiverResponseDto
                = new UserInfoDto(2L, receiver.getFirstName(), receiver.getLastName(), receiver.getEmail());
        UserResponseDto senderUResponseDto = new UserResponseDto(1L, sender.getFirstName(), sender.getLastName(), sender.getEmail());
        NoteResponseDto noteResponseDto
                = new NoteResponseDto(senderUResponseDto, 1L, note.getTitle(), note.getText(), note.getDate(), note.getGrade(), Set.of());
        ShareResponseDto responseDto
                = new ShareResponseDto(senderResponseDto, receiverResponseDto, noteResponseDto, LocalDate.parse("2024-10-15"));

        UserInfoDto anotherUserInfoDto = new UserInfoDto(3L, "User", "User", "user@example.com");
        ShareResponseDto otherShareResponseDto = new ShareResponseDto(senderResponseDto, anotherUserInfoDto, noteResponseDto, LocalDate.parse("2024-10-16"));

        when(shareService.getAllSharedNotesBetweenUsers(any(User.class), any(String.class))).thenReturn(List.of(mockedShare, share));
        when(mapper.toDto(share)).thenReturn(responseDto);
        when(mapper.toDto(mockedShare)).thenReturn(otherShareResponseDto);

        mockMvc.perform(get("/share/sent")
                        .param("receiverEmail", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sender.firstName", is("Sender")))
                .andExpect(jsonPath("$[0].sender.lastName", is("Sender")))
                .andExpect(jsonPath("$[0].sender.email", is("sender@example.com")))
                .andExpect(jsonPath("$[0].receiver.firstName", is("User")))
                .andExpect(jsonPath("$[0].receiver.lastName", is("User")))
                .andExpect(jsonPath("$[0].receiver.email", is("user@example.com")))
                .andExpect(jsonPath("$[0].sentNote.user.firstName", is("Sender")))
                .andExpect(jsonPath("$[0].sentNote.user.lastName", is("Sender")))
                .andExpect(jsonPath("$[0].sentNote.user.email", is("sender@example.com")))
                .andExpect(jsonPath("$[0].sentAt", is("16-10-2024")))

                .andExpect(jsonPath("$[1].sender.firstName", is("Sender")))
                .andExpect(jsonPath("$[1].sender.lastName", is("Sender")))
                .andExpect(jsonPath("$[1].sender.email", is("sender@example.com")))
                .andExpect(jsonPath("$[1].receiver.firstName", is("Receiver")))
                .andExpect(jsonPath("$[1].receiver.lastName", is("Receiver")))
                .andExpect(jsonPath("$[1].receiver.email", is("receiver@example.com")))
                .andExpect(jsonPath("$[1].sentNote.user.firstName", is("Sender")))
                .andExpect(jsonPath("$[1].sentNote.user.lastName", is("Sender")))
                .andExpect(jsonPath("$[1].sentNote.user.email", is("sender@example.com")))
                .andExpect(jsonPath("$[1].sentAt", is("15-10-2024")));
    }

    @Test
    public void testGetAllSharedNotesBetweenUsers_NonEmptyEmail() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserInfoDto senderResponseDto = new UserInfoDto(1L, sender.getFirstName(), sender.getLastName(), sender.getEmail());
        UserInfoDto receiverResponseDto
                = new UserInfoDto(2L, receiver.getFirstName(), receiver.getLastName(), receiver.getEmail());
        UserResponseDto senderUResponseDto = new UserResponseDto(1L, sender.getFirstName(), sender.getLastName(), sender.getEmail());
        NoteResponseDto noteResponseDto
                = new NoteResponseDto(senderUResponseDto, 1L, note.getTitle(), note.getText(), note.getDate(), note.getGrade(), Set.of());
        ShareResponseDto responseDto
                = new ShareResponseDto(senderResponseDto, receiverResponseDto, noteResponseDto, LocalDate.parse("2024-10-15"));

        when(shareService.getAllSharedNotesBetweenUsers(any(User.class), any(String.class))).thenReturn(List.of(share));
        when(mapper.toDto(any(Share.class))).thenReturn(responseDto);

        mockMvc.perform(get("/share/sent")
                        .param("receiverEmail", "receiver@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sender.firstName", is("Sender")))
                .andExpect(jsonPath("$[0].sender.lastName", is("Sender")))
                .andExpect(jsonPath("$[0].sender.email", is("sender@example.com")))
                .andExpect(jsonPath("$[0].receiver.firstName", is("Receiver")))
                .andExpect(jsonPath("$[0].receiver.lastName", is("Receiver")))
                .andExpect(jsonPath("$[0].receiver.email", is("receiver@example.com")))
                .andExpect(jsonPath("$[0].sentNote.user.firstName", is("Sender")))
                .andExpect(jsonPath("$[0].sentNote.user.lastName", is("Sender")))
                .andExpect(jsonPath("$[0].sentNote.user.email", is("sender@example.com")))
                .andExpect(jsonPath("$[0].sentAt", is("15-10-2024")));
    }

    @Test
    public void testGetAllSharedNotesBetweenUsers_NotLoggedIn() throws Exception {
        mockMvc.perform(get("/share/sent")
                        .param("receiverEmail", "receiver@example.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllReceivedNotesBetweenUsers_EmptyEmail() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = new User(3L, "User", "User", "user@example.com", "test123");
        Share mockedShare = new Share(2L, user, sender, note, LocalDate.parse("2024-10-16"));

        UserInfoDto senderResponseDto = new UserInfoDto(1L, sender.getFirstName(), sender.getLastName(), sender.getEmail());
        UserInfoDto receiverResponseDto
                = new UserInfoDto(2L, receiver.getFirstName(), receiver.getLastName(), receiver.getEmail());

        UserResponseDto receiverUResponseDto
                = new UserResponseDto(1L, receiver.getFirstName(), receiver.getLastName(), receiver.getEmail());
        UserResponseDto userUResponseDto = new UserResponseDto(2L, user.getFirstName(), user.getLastName(), user.getEmail());

        NoteResponseDto noteResponseDto
                = new NoteResponseDto(receiverUResponseDto, 1L, note.getTitle(), note.getText(), note.getDate(), note.getGrade(), Set.of());
        NoteResponseDto noteResponseDto1
                = new NoteResponseDto(userUResponseDto, 2L, note.getTitle(), note.getText(), note.getDate(), note.getGrade(), Set.of());

        ShareResponseDto responseDto
                = new ShareResponseDto(receiverResponseDto, senderResponseDto, noteResponseDto, LocalDate.parse("2024-10-15"));

        UserInfoDto anotherUserInfoDto = new UserInfoDto(3L, "User", "User", "user@example.com");
        ShareResponseDto otherShareResponseDto = new ShareResponseDto(anotherUserInfoDto, senderResponseDto, noteResponseDto1, LocalDate.parse("2024-10-16"));

        when(shareService.getAllReceivedNotesBetweenUsers(any(User.class), any(String.class))).thenReturn(List.of(mockedShare, share));
        when(mapper.toDto(share)).thenReturn(responseDto);
        when(mapper.toDto(mockedShare)).thenReturn(otherShareResponseDto);

        mockMvc.perform(get("/share/received")
                        .param("senderEmail", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sender.firstName", is("User")))
                .andExpect(jsonPath("$[0].sender.lastName", is("User")))
                .andExpect(jsonPath("$[0].sender.email", is("user@example.com")))
                .andExpect(jsonPath("$[0].receiver.firstName", is("Sender")))
                .andExpect(jsonPath("$[0].receiver.lastName", is("Sender")))
                .andExpect(jsonPath("$[0].receiver.email", is("sender@example.com")))
                .andExpect(jsonPath("$[0].sentNote.user.firstName", is("User")))
                .andExpect(jsonPath("$[0].sentNote.user.lastName", is("User")))
                .andExpect(jsonPath("$[0].sentNote.user.email", is("user@example.com")))
                .andExpect(jsonPath("$[0].sentAt", is("16-10-2024")))

                .andExpect(jsonPath("$[1].sender.firstName", is("Receiver")))
                .andExpect(jsonPath("$[1].sender.lastName", is("Receiver")))
                .andExpect(jsonPath("$[1].sender.email", is("receiver@example.com")))
                .andExpect(jsonPath("$[1].receiver.firstName", is("Sender")))
                .andExpect(jsonPath("$[1].receiver.lastName", is("Sender")))
                .andExpect(jsonPath("$[1].receiver.email", is("sender@example.com")))
                .andExpect(jsonPath("$[1].sentNote.user.firstName", is("Receiver")))
                .andExpect(jsonPath("$[1].sentNote.user.lastName", is("Receiver")))
                .andExpect(jsonPath("$[1].sentNote.user.email", is("receiver@example.com")))
                .andExpect(jsonPath("$[1].sentAt", is("15-10-2024")));
    }

    @Test
    public void testGetAllReceivedNotesBetweenUsers_NonEmptyEmail() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserInfoDto senderResponseDto = new UserInfoDto(1L, sender.getFirstName(), sender.getLastName(), sender.getEmail());
        UserInfoDto receiverResponseDto
                = new UserInfoDto(2L, receiver.getFirstName(), receiver.getLastName(), receiver.getEmail());

        UserResponseDto receiverUResponseDto
                = new UserResponseDto(1L, receiver.getFirstName(), receiver.getLastName(), receiver.getEmail());

        NoteResponseDto noteResponseDto
                = new NoteResponseDto(receiverUResponseDto, 1L, note.getTitle(), note.getText(), note.getDate(), note.getGrade(), Set.of());

        ShareResponseDto responseDto
                = new ShareResponseDto(receiverResponseDto, senderResponseDto, noteResponseDto, LocalDate.parse("2024-10-15"));

        when(shareService.getAllReceivedNotesBetweenUsers(any(User.class), any(String.class))).thenReturn(List.of(share));
        when(mapper.toDto(any(Share.class))).thenReturn(responseDto);

        mockMvc.perform(get("/share/received")
                        .param("senderEmail", "sender@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sender.firstName", is("Receiver")))
                .andExpect(jsonPath("$[0].sender.lastName", is("Receiver")))
                .andExpect(jsonPath("$[0].sender.email", is("receiver@example.com")))
                .andExpect(jsonPath("$[0].receiver.firstName", is("Sender")))
                .andExpect(jsonPath("$[0].receiver.lastName", is("Sender")))
                .andExpect(jsonPath("$[0].receiver.email", is("sender@example.com")))
                .andExpect(jsonPath("$[0].sentNote.user.firstName", is("Receiver")))
                .andExpect(jsonPath("$[0].sentNote.user.lastName", is("Receiver")))
                .andExpect(jsonPath("$[0].sentNote.user.email", is("receiver@example.com")))
                .andExpect(jsonPath("$[0].sentAt", is("15-10-2024")));
    }

    @Test
    public void testGetAllReceivedNotesBetweenUsers_NotLoggedIn() throws Exception {
        mockMvc.perform(get("/share/received")
                        .param("senderEmail", "sender@example.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetShareById() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserInfoDto senderResponseDto = new UserInfoDto(1L, sender.getFirstName(), sender.getLastName(), sender.getEmail());
        UserInfoDto receiverResponseDto
                = new UserInfoDto(2L, receiver.getFirstName(), receiver.getLastName(), receiver.getEmail());
        UserResponseDto senderUResponseDto = new UserResponseDto(1L, sender.getFirstName(), sender.getLastName(), sender.getEmail());
        NoteResponseDto noteResponseDto
                = new NoteResponseDto(senderUResponseDto, 1L, note.getTitle(), note.getText(), note.getDate(), note.getGrade(), Set.of());
        ShareResponseDto responseDto
                = new ShareResponseDto(senderResponseDto, receiverResponseDto, noteResponseDto, LocalDate.parse("2024-10-15"));

        when(shareService.getShareById(any(Long.class))).thenReturn(share);
        when(mapper.toDto(any(Share.class))).thenReturn(responseDto);

        mockMvc.perform(get("/share/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sender.firstName", is("Sender")))
                .andExpect(jsonPath("$.sender.lastName", is("Sender")))
                .andExpect(jsonPath("$.sender.email", is("sender@example.com")))
                .andExpect(jsonPath("$.receiver.firstName", is("Receiver")))
                .andExpect(jsonPath("$.receiver.lastName", is("Receiver")))
                .andExpect(jsonPath("$.receiver.email", is("receiver@example.com")))
                .andExpect(jsonPath("$.sentNote.user.firstName", is("Sender")))
                .andExpect(jsonPath("$.sentNote.user.lastName", is("Sender")))
                .andExpect(jsonPath("$.sentNote.user.email", is("sender@example.com")))
                .andExpect(jsonPath("$.sentAt", is("15-10-2024")));
    }

    @Test
    public void testGetShareById_InvalidId() throws Exception{
        Long id = 999L;
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(shareService.getShareById(id)).thenThrow(new EntityNotFoundException(String.format("Share with id %s does not exist", id)));

        mockMvc.perform(get("/share/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("Share with id %s does not exist", id)));
    }

    @Test
    public void testGetShareById_NotLoggedIn() throws Exception{
        mockMvc.perform(get("/share/{id}", 999L))
                .andExpect(status().isBadRequest());
    }

}
