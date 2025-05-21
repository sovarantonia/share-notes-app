package com.example.sharesnotesapp.service;

import com.example.sharesnotesapp.model.*;
import com.example.sharesnotesapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResetService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;
    private final RequestRepository requestRepository;
    private final ShareRepository shareRepository;

    @Autowired
    public ResetService(UserRepository userRepository, PasswordEncoder passwordEncoder, NoteRepository noteRepository, TagRepository tagRepository, RequestRepository requestRepository, ShareRepository shareRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.noteRepository = noteRepository;
        this.tagRepository = tagRepository;
        this.requestRepository = requestRepository;
        this.shareRepository = shareRepository;
    }

    @Transactional
    public void reset() {
        shareRepository.deleteAll();
        requestRepository.deleteAll();
        noteRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();

        //jane is tested
        User janeTest = new User("Jane", "Doe", "jane@test.com", passwordEncoder.encode("test123"));

        User bobFriend = new User("Bob", "Friend", "bob@test.com", passwordEncoder.encode("test123"));
        User maryToAdd = new User("Mary", "Smith", "mary@test.com", passwordEncoder.encode("test123"));
        User samanthaJones = new User("Samantha", "Jones", "samantha@test.com", passwordEncoder.encode("test123"));
        User johnJohn = new User("John", "John", "john@test.com", passwordEncoder.encode("test123"));

        User emilyClark = new User("Emily", "Clark", "emily@test.com", passwordEncoder.encode("test123"));
        User danielGreen = new User("Daniel", "Green", "daniel@test.com", passwordEncoder.encode("test123"));
        User oliviaBrown = new User("Olivia", "Brown", "olivia@test.com", passwordEncoder.encode("test123"));
        User michaelLee = new User("Michael", "Lee", "michael@test.com", passwordEncoder.encode("test123"));
        User chloeWhite = new User("Chloe", "White", "chloe@test.com", passwordEncoder.encode("test123"));

        //jane is friend with bob, mary and samantha
        janeTest.getFriendList().add(bobFriend);
        bobFriend.getFriendList().add(janeTest);
        janeTest.getFriendList().add(maryToAdd);
        maryToAdd.getFriendList().add(janeTest);
        janeTest.getFriendList().add(samanthaJones);
        samanthaJones.getFriendList().add(janeTest);
        userRepository.saveAll(List.of(janeTest, bobFriend, maryToAdd, samanthaJones, johnJohn, emilyClark, danielGreen, oliviaBrown, michaelLee, chloeWhite));

        //jane's tags
        Tag workTag = new Tag("Work", janeTest);
        Tag motivationTag = new Tag("Motivation", janeTest);
        Tag toDoTag = new Tag("To do", janeTest);
        Tag reminderTag = new Tag("Reminder", janeTest);
        Tag otherTag = new Tag("Other", janeTest);
        tagRepository.saveAll(List.of(workTag, motivationTag, toDoTag, reminderTag, otherTag));

        Note note1 = new Note(janeTest, LocalDate.of(2025, 5, 5), "My motivation for today", "Be kind to yourself", 9);
        Note note2 = new Note(janeTest, LocalDate.of(2025, 5, 6), "Work presentation", "Had my work presentation and the application crashed", 3);
        Note note3 = new Note(janeTest, LocalDate.of(2025, 5, 8), "Appointment", "Need to go to that appointment", 10);
        Note note4 = new Note(janeTest, LocalDate.of(2025, 5, 9), "Feelings", "Today was average", 7);
        Note note5 = new Note(janeTest, LocalDate.of(2025, 5, 10), "Self-reflection", "Listened to a podcast and it was interesting", 8);
        Note note6 = new Note(janeTest, LocalDate.of(2025, 4, 7), "Birthday", "Went out with my friends", 9);
        Note note7 = new Note(janeTest, LocalDate.of(2025, 5, 17), "Power cut", "No power for all day", 5);
        Note note8 = new Note(janeTest, LocalDate.of(2025, 2, 5), "February", "Want to go somewhere else", 4);
        Note note9 = new Note(janeTest, LocalDate.of(2024, 12, 31), "Last day", "I am grateful for this year", 9);
        Note note10 = new Note(janeTest, LocalDate.of(2025, 5, 1), "Labour day", "Really need a bbq with my friends", 8);
        Note note11 = new Note(maryToAdd, LocalDate.of(2025, 3, 4), "Going out", "Went out and met with our old friend Jenny", 9);
        Note note12 = new Note(maryToAdd, LocalDate.of(2025, 1, 13), "Worst day ever", "I broke my car and then I lost my laptop", 2);
        //notes, 10 for jane and 2 for mary
        List<Note> notes = List.of(
                note1, note2, note3, note4, note5, note6, note7, note8, note9, note10, note11, note12
        );
        noteRepository.saveAll(notes);

        //put tags for jane's notes
        note1.getTags().addAll(List.of(motivationTag, reminderTag)); // Note 1
        note2.getTags().add(workTag); // Note 2
        note3.getTags().add(toDoTag); // Note 3
        note5.getTags().add(otherTag); // Note 5
        note7.getTags().add(otherTag); // Note 7
        note10.getTags().add(motivationTag); // Note 10
        noteRepository.saveAll(notes);

        //jane receives request from john
        Request request = new Request(johnJohn, janeTest, Status.PENDING, LocalDateTime.of(2025, 5, 14, 0, 0));
        requestRepository.save(request);

        //shared notes; jane shared 3 notes and received 2
        List<Share> shares = List.of(
                new Share(janeTest, bobFriend, notes.get(0), LocalDate.of(2025, 5, 5)), // note 1
                new Share(janeTest, maryToAdd, notes.get(0), LocalDate.of(2025, 5, 5)),
                new Share(janeTest, bobFriend, notes.get(2), LocalDate.of(2025, 5, 8)), // note 3
                new Share(maryToAdd, janeTest, notes.get(10), LocalDate.of(2025, 3, 6)), // note 11
                new Share(maryToAdd, janeTest, notes.get(11), LocalDate.of(2025, 1, 14))  // note 12
        );
        shareRepository.saveAll(shares);
    }
}
