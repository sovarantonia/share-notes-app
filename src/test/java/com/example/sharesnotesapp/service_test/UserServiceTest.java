//package com.example.sharesnotesapp.service_test;
//
//import com.example.sharesnotesapp.model.User;
//import com.example.sharesnotesapp.model.dto.request.UserNameDto;
//import com.example.sharesnotesapp.model.dto.request.UserRequestDto;
//import com.example.sharesnotesapp.repository.UserRepository;
//import com.example.sharesnotesapp.service.user.UserServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import javax.persistence.EntityNotFoundException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class UserServiceTest {
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    private User user;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        user = new User();
//        user.setFirstName("ExampleA");
//        user.setLastName("ExampleB");
//        user.setEmail("test_example@test.com");
//        user.setPassword(passwordEncoder.encode("test123"));
//    }
//
//    @Test
//    void testSaveUser() {
//        UserRequestDto userDto = new UserRequestDto();
//        userDto.setFirstName("ExampleA");
//        userDto.setLastName("ExampleB");
//        userDto.setEmail("test_example@test.com");
//        userDto.setPassword("test123");
//
//        when(userRepository.findUserByEmail(userDto.getEmail())).thenReturn(Optional.empty());
//        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
//
//        User createdUser = userService.saveUser(userDto);
//
//        assertEquals("ExampleA", createdUser.getFirstName());
//        assertEquals("ExampleB", createdUser.getLastName());
//        assertEquals("test_example@test.com", createdUser.getEmail());
//        assertEquals(passwordEncoder.encode("test123"), createdUser.getPassword());
//
//        verify(userRepository).save(Mockito.any(User.class));
//    }
//
//    @Test
//    void testSaveUser_EmailAlreadyExists() {
//        User existingUser = new User();
//        existingUser.setFirstName("Exist");
//        existingUser.setLastName("Exist");
//        existingUser.setEmail("exists@test.com");
//        existingUser.setPassword(passwordEncoder.encode("test123"));
//
//        String existingEmail = "exists@test.com";
//        when(userRepository.findUserByEmail(existingEmail)).thenReturn(Optional.of(existingUser));
//
//        UserRequestDto userDto = new UserRequestDto();
//        userDto.setFirstName("Exists");
//        userDto.setLastName("Exists");
//        userDto.setEmail("exists@test.com");
//        userDto.setPassword("test123");
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            userService.saveUser(userDto);
//        });
//
//        String message = String.format("%s is already used", existingEmail);
//        assertEquals(message, exception.getMessage());
//
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void testGetUserById() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//
//        User foundUser = userService.getUserById(1L).orElseThrow(null);
//
//        assertEquals("ExampleA", foundUser.getFirstName());
//        assertEquals("ExampleB", foundUser.getLastName());
//        assertEquals("test_example@test.com", foundUser.getEmail());
//        assertEquals(passwordEncoder.encode("test123"), foundUser.getPassword());
//    }
//
//    @Test
//    void testGetUserById_NonExistent() {
//        Long nonExistentId = 888L;
//
//        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());
//
//        Throwable exception = assertThrows(EntityNotFoundException.class, () -> {
//            userService.getUserById(nonExistentId);
//        });
//
//        String message = String.format("User with id %s does not exist", nonExistentId);
//
//        assertEquals(message, exception.getMessage());
//    }
//
//    @Test
//    void testGetUserByEmail() {
//        String email = "test_example@test.com";
//
//        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
//
//        User foundUser = userService.getUserByEmail(email).orElseThrow(null);
//
//        assertEquals("ExampleA", foundUser.getFirstName());
//        assertEquals("ExampleB", foundUser.getLastName());
//        assertEquals("test_example@test.com", foundUser.getEmail());
//        assertEquals(passwordEncoder.encode("test123"), foundUser.getPassword());
//    }
//
//    @Test
//    void testGetUserByEmail_NonExistent() {
//        String nonExistentEmail = "no_email@test.com";
//
//        when(userRepository.findUserByEmail(nonExistentEmail)).thenReturn(Optional.empty());
//
//        Throwable exception = assertThrows(UsernameNotFoundException.class, () -> {
//            userService.getUserByEmail(nonExistentEmail);
//        });
//
//        String message = String.format("User with the address %s does not exist", nonExistentEmail);
//
//        assertEquals(message, exception.getMessage());
//    }
//
//    @Test
//    void testValidateEmail() {
//        String newValidEmail = "new_email@test.com";
//
//        when(userRepository.findUserByEmail(newValidEmail)).thenReturn(Optional.empty());
//
//        userService.validateEmail(newValidEmail);
//    }
//
//    @Test
//    void testValidateEmail_NotValid() {
//        User user = new User();
//        String alreadyUsedEmail = "used_email@test.com";
//        user.setFirstName("A");
//        user.setLastName("B");
//        user.setEmail(alreadyUsedEmail);
//
//        when(userRepository.findUserByEmail(alreadyUsedEmail)).thenReturn(Optional.of(user));
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.validateEmail(alreadyUsedEmail));
//
//        String message = alreadyUsedEmail + " is already used";
//        assertEquals(message, exception.getMessage());
//    }
//
//    @Test
//    void testDeleteUser() {
//        Long id = 1L;
//
//        when(userRepository.findById(id)).thenReturn(Optional.of(user));
//
//        userService.deleteUser(id);
//
//        verify(userRepository, times(1)).deleteById(id);
//    }
//
//    @Test
//    void testDeleteUser_NonExistentId() {
//        Long nonExistentId = 999L;
//
//        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());
//
//        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(nonExistentId));
//
//        String message = "User does not exist";
//        assertEquals(message, exception.getMessage());
//
//        verify(userRepository, never()).deleteById(nonExistentId);
//    }
//
//    @Test
//    void testUpdateUserCredentials() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//
//        UserNameDto userNameDto = new UserNameDto();
//        userNameDto.setFirstName("New-Name");
//        userService.updateUserCredentials(1L, userNameDto);
//
//        assertEquals(userNameDto.getFirstName(), user.getFirstName());
//        assertEquals("ExampleB", user.getLastName());
//        assertEquals("test_example@test.com", user.getEmail());
//        assertEquals(passwordEncoder.encode("test123"), user.getPassword());
//    }
//
//    @Test
//    void testUpdateUserCredential_InvalidId() {
//        Long nonExistingId = 123L;
//
//        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());
//
//        UserNameDto userNameDto = new UserNameDto();
//        userNameDto.setFirstName("Name");
//
//        UsernameNotFoundException exception = assertThrows
//                (UsernameNotFoundException.class, () -> userService.updateUserCredentials(nonExistingId, userNameDto));
//
//        String message = "No user with that username";
//
//        assertEquals(message, exception.getMessage());
//    }
//
//    @Test
//    void testUpdateUserCredential_AllEmptyFields() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//
//        UserNameDto userNameDto = new UserNameDto();
//
//        userService.updateUserCredentials(1L, userNameDto);
//
//        assertEquals("ExampleA", user.getFirstName());
//        assertEquals("ExampleB", user.getLastName());
//        assertEquals("test_example@test.com", user.getEmail());
//        assertEquals(passwordEncoder.encode("test123"), user.getPassword());
//    }
//
//    @Test
//    public void testGetUserFriends() {
//        User anotherUser = mock(User.class);
//        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(anotherUser));
//        List<User> friendList = new ArrayList<>();
//        friendList.add(user);
//        when(anotherUser.getFriendList()).thenReturn(friendList);
//        List<User> friends = userService.getUserFriends(anotherUser);
//        assertEquals(friendList.get(0), friends.get(0));
//    }
//    @Test
//    public void testSearchUsers_EmptyString() {
//        User anotherUser = new User(2L, "Ams", "Bs", "abc@example.com", "test123");
//        when(userRepository.findByIdNot(any(Long.class))).thenReturn(List.of(anotherUser, user));
//        List<User> users = userService.searchUsers("", 3L);
//        assertEquals(anotherUser, users.get(0));
//        assertEquals(user, users.get(1));
//        verify(userRepository, times(1)).findByIdNot(3L);
//    }
//
//    @Test
//    public void testSearchUsers_NonEmptyString() {
//        when(userRepository.searchUsersExcludingCurrent(any(Long.class), any(String.class)))
//                .thenReturn(List.of(user));
//
//        List<User> users = userService.searchUsers("ex", 3L);
//        assertEquals(user, users.get(0));
//    }
//
//    @Test
//    public void testSearchUserFriends_EmptyString() {
//        User anotherUser = new User(2L, "Ams", "Bs", "abc@example.com", "test123");
//        User anotherUser1 = new User(3L, "B", "F", "abc4@example.com", "test123");
//        user.getFriendList().add(anotherUser);
//        user.getFriendList().add(anotherUser1);
//        anotherUser.getFriendList().add(user);
//        anotherUser1.getFriendList().add(user);
//
//        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
//
//        List<User> friends = userService.searchUserFriends("", 1L);
//        assertEquals(anotherUser, friends.get(0));
//        assertEquals(anotherUser1, friends.get(1));
//    }
//
//    @Test
//    public void testSearchUserFriends_NonEmptyString() {
//        User anotherUser = new User(2L, "Ams", "Bs", "abc@example.com", "test123");
//        user.getFriendList().add(anotherUser);
//        anotherUser.getFriendList().add(user);
//
//        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
//
//        List<User> friends = userService.searchUserFriends("am", 1L);
//        assertEquals(anotherUser, friends.get(0));
//    }
//}
