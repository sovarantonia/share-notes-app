package com.example.sharesnotesapp.service.user;

import com.example.sharesnotesapp.model.Status;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.UserNameDto;
import com.example.sharesnotesapp.model.dto.request.UserRequestDto;
import com.example.sharesnotesapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %s does not exist", id)));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format("User with the address %s does not exist", email)
                        )
                );
    }

    /**
     * Checks if there is already a user with the email
     *
     * @param email - the email to be verified
     */
    @Override
    public void validateEmail(String email) {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new IllegalArgumentException(String.format("%s is already used", email));
        }
    }

    /**
     * Saves the new user with the credentials from the registration
     *
     * @param userRequestDto - contains the user credentials to be saved
     */

    @Override
    public User saveUser(UserRequestDto userRequestDto) {
        validateEmail(userRequestDto.getEmail());

        final User newUser = User.builder()
                .firstName(userRequestDto.getFirstName())
                .lastName(userRequestDto.getLastName())
                .email(userRequestDto.getEmail())
                .password(userRequestDto.getPassword())
                .build();

        newUser.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

        return userRepository.save(newUser);
    }


    @Override
    public void deleteUser(Long id) {
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id %s does not exist", id)));

        // Remove the user from all friends' lists
        for (User friend : userToDelete.getFriendList()) {
            friend.getFriendList().remove(userToDelete);
        }

        // Clear their own friend list
        userToDelete.getFriendList().clear();


        // Save updates to affected users
        userRepository.saveAll(userToDelete.getFriendList());
        userRepository.save(userToDelete);
        userRepository.flush();

        // Now it's safe to delete
        userRepository.delete(userToDelete);
    }

    @Override
    public User updateUserCredentials(Long id, UserNameDto userRequestDto) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No user with that username"));

        if (!userRequestDto.getFirstName().isEmpty() && !userRequestDto.getFirstName().isBlank()) {
            userToUpdate.setFirstName(userRequestDto.getFirstName());
        }

        if (!userRequestDto.getLastName().isEmpty() && !userRequestDto.getLastName().isBlank()) {
            userToUpdate.setLastName(userRequestDto.getLastName());
        }

        return userRepository.save(userToUpdate);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException("No user with that username"));
    }

    @Transactional
    @Override
    public List<User> getUserFriends(User user) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Hibernate.initialize(managedUser.getFriendList());
        List<User> friends = managedUser.getFriendList();
        friends.sort(Comparator.comparing(User::getLastName));

        return friends;
    }

    @Override
    public List<User> searchUsers(String string, Long currentUserId) {
        if (!string.isEmpty()) {
            return userRepository
                    .searchUsersExcludingCurrent
                            (currentUserId, string);
        } else {
            return userRepository.findByIdNot(currentUserId);
        }
    }

    @Transactional
    @Override
    public List<User> searchUserFriends(String string, Long currentUserId) {
        User managedUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Hibernate.initialize(managedUser.getFriendList());

        List<User> friends = managedUser.getFriendList();
        if (!string.isEmpty()) {

            return friends.stream().filter(friend -> friend.getFirstName().toLowerCase().contains(string.toLowerCase()) ||
                            friend.getLastName().toLowerCase().contains(string.toLowerCase()) ||
                            friend.getEmail().toLowerCase().contains(string.toLowerCase()))
                    .toList();
        } else {

            return friends;
        }
    }


    @Override
    @Transactional
    public List<User> searchUsersNotFriends(String searchString, User user) {
        List<Status> blockedStatuses = List.of(Status.PENDING, Status.ACCEPTED);

        if (!searchString.isEmpty()) {
            return userRepository.searchUsersNotFriendsWith(user.getId(), searchString, blockedStatuses);
        }
        return userRepository.getUsersNotFriendsWith(user.getId(), blockedStatuses);
    }


}
