package com.example.sharesnotesapp.service_test;

import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.Share;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.repository.NoteRepository;
import com.example.sharesnotesapp.repository.ShareRepository;
import com.example.sharesnotesapp.repository.UserRepository;
import com.example.sharesnotesapp.service.share.ShareServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ShareServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private NoteRepository noteRepository;
    @Mock
    private ShareRepository shareRepository;

    @InjectMocks
    private ShareServiceImpl shareService;

    private User sender;
    private User receiver;
    private Note note;
    private Share share;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        sender = new User(1L, "Sender", "Sender", "sender@example.com", "test123");
        receiver = new User(2L, "Receiver", "Receiver", "receiver@example.com", "test123");
        sender.setFriendList(List.of(receiver));
        receiver.setFriendList(List.of(sender));

        note = new Note(1L, sender, LocalDate.now(), "Title", "Text", 9, Set.of());

        share = new Share(1L, sender, receiver, note, LocalDate.now());
    }

    @Test
    public void testShareNote(){
        when(userRepository.findUserByEmail(any(String.class))).thenReturn(Optional.of(receiver));
        when(noteRepository.findById(any(Long.class))).thenReturn(Optional.of(note));
        when(shareRepository.save(any(Share.class))).thenReturn(share);

        Share sharedNote = shareService.shareNote(sender, "receiver@example.com", 1L);

        assertEquals(sender, sharedNote.getSender());
        assertEquals(receiver, sharedNote.getReceiver());
        assertEquals(note, sharedNote.getSentNote());
        assertEquals(LocalDate.now(), sharedNote.getSentAt());
    }

    @Test
    public void testShareNote_InvalidEmail(){
        when(userRepository.findUserByEmail(any(String.class))).thenReturn(Optional.empty());
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> shareService.shareNote(sender, "receiver@example.com", 1L));

        String message = "User does not exist";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testShareNote_InvalidNoteId(){
        when(userRepository.findUserByEmail(any(String.class))).thenReturn(Optional.of(receiver));
        when(noteRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> shareService.shareNote(sender, "receiver@example.com", 1L));

        String message = "Note does not exist";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testGetShareById(){
        when(shareRepository.findById(any(Long.class))).thenReturn(Optional.of(share));
        Share result = shareService.getShareById(1L);
        assertEquals(share, result);
    }

    @Test
    public void testGetShareById_InvalidId(){
        Long id = 999L;
        when(shareRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> shareService.getShareById(id));

        String message = String.format("Share with id %s does not exist", id);
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testGetAllSharedNotesBetweenUsers_EmptyEmail(){
        Share anotherShare = mock(Share.class);
        when(shareRepository.getSharesBySenderOrderBySentAtDesc(any(User.class))).thenReturn(List.of(share, anotherShare));
        List<Share> shareList = shareService.getAllSharedNotesBetweenUsers(sender, "");
        verify(shareRepository, times(1)).getSharesBySenderOrderBySentAtDesc(sender);
        assertEquals(share, shareList.get(0));
        assertEquals(anotherShare, shareList.get(1));
    }

    @Test
    public void testGetAllSharedNotesBetweenUsers_NonEmptyEmail(){
        Share anotherShare = mock(Share.class);
        when(userRepository.findUserByEmail(any(String.class))).thenReturn(Optional.of(receiver));
        when(shareRepository.getSharesBySenderAndReceiverOrderBySentAtDesc(any(User.class), any(User.class)))
                .thenReturn(List.of(share, anotherShare));
        List<Share> shareList = shareService.getAllSharedNotesBetweenUsers(sender, "receiver@example.com");
        verify(shareRepository, times(1)).getSharesBySenderAndReceiverOrderBySentAtDesc(sender, receiver);
        assertEquals(share, shareList.get(0));
        assertEquals(anotherShare, shareList.get(1));
    }

    @Test
    public void testGetAllSharedNotesBetweenUsers_InvalidEmail(){
        when(userRepository.findUserByEmail(any(String.class))).thenReturn(Optional.empty());
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> shareService.getAllSharedNotesBetweenUsers(sender, "invalid@email.com"));

        String message = "User does not exist";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testGetAllReceivedNotesBetweenUsers_EmptyEmail(){
        Share anotherShare = mock(Share.class);
        when(shareRepository.getSharesByReceiverOrderBySentAtDesc(any(User.class))).thenReturn(List.of(share, anotherShare));
        List<Share> shareList = shareService.getAllReceivedNotesBetweenUsers(receiver, "");
        verify(shareRepository, times(1)).getSharesByReceiverOrderBySentAtDesc(receiver);
        assertEquals(share, shareList.get(0));
        assertEquals(anotherShare, shareList.get(1));
    }

    @Test
    public void testGetAllReceivedNotesBetweenUsers_NonEmptyEmail(){
        Share anotherShare = mock(Share.class);
        when(userRepository.findUserByEmail(any(String.class))).thenReturn(Optional.of(sender));
        when(shareRepository.getSharesBySenderAndReceiverOrderBySentAtDesc(any(User.class), any(User.class)))
                .thenReturn(List.of(share, anotherShare));
        List<Share> shareList = shareService.getAllSharedNotesBetweenUsers(receiver, "sender@example.com");
        verify(shareRepository, times(1)).getSharesBySenderAndReceiverOrderBySentAtDesc(receiver, sender);
        assertEquals(share, shareList.get(0));
        assertEquals(anotherShare, shareList.get(1));
    }

    @Test
    public void testGetAllReceivedNotesBetweenUsers_InvalidEmail(){
        when(userRepository.findUserByEmail(any(String.class))).thenReturn(Optional.empty());
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> shareService.getAllReceivedNotesBetweenUsers(receiver, "invalid@email.com"));

        String message = "User does not exist";
        assertEquals(message, exception.getMessage());
    }
}
