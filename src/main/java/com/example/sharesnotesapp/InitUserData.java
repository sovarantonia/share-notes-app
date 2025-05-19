package com.example.sharesnotesapp;

import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitUserData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public InitUserData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User janeTest = new User(1L, "Jane", "Doe", "jane@test.com", passwordEncoder.encode("test123"));
            User bobFriend = new User (2L, "Bob", "Friend", "bob@test.com", passwordEncoder.encode("test123"));
            User maryToAdd = new User (3L, "Mary", "Smith", "mary@test.com", passwordEncoder.encode("test123"));
            User samanthaJones = new User(4L, "Samantha", "Jones", "samantha@test.com", passwordEncoder.encode("test123"));
            User johnJohn = new User(5L, "John", "John", "john@test.com", passwordEncoder.encode("test123"));

            User emilyClark = new User(6L, "Emily", "Clark", "emily@test.com", passwordEncoder.encode("test123"));
            User danielGreen = new User(7L, "Daniel", "Green", "daniel@test.com", passwordEncoder.encode("test123"));
            User oliviaBrown = new User(8L, "Olivia", "Brown", "olivia@test.com", passwordEncoder.encode("test123"));
            User michaelLee = new User(9L, "Michael", "Lee", "michael@test.com", passwordEncoder.encode("test123"));
            User chloeWhite = new User(10L, "Chloe", "White", "chloe@test.com", passwordEncoder.encode("test123"));

            janeTest.getFriendList().add(bobFriend);
            bobFriend.getFriendList().add(janeTest);

            janeTest.getFriendList().add(maryToAdd);
            maryToAdd.getFriendList().add(janeTest);

            userRepository.save(janeTest);
            userRepository.save(bobFriend);
            userRepository.save(maryToAdd);
            userRepository.save(samanthaJones);
            userRepository.save(johnJohn);
            userRepository.save(emilyClark);
            userRepository.save(danielGreen);
            userRepository.save(oliviaBrown);
            userRepository.save(michaelLee);
            userRepository.save(chloeWhite);


        }
    }
}
