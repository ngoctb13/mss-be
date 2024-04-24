package vn.edu.fpt.be;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.fpt.be.dto.*;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.UserProfile;
import vn.edu.fpt.be.model.enums.Gender;
import vn.edu.fpt.be.model.enums.Role;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.UserProfileRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.service.impl.UserServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserProfile userProfile;
    private Store store;
    @BeforeEach
    void setUp() {
        user = new User();
        store = new Store();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setStore(store);
        user.setRole(Role.STORE_OWNER);
        user.setStatus(Status.ACTIVE);

        userProfile = new UserProfile();
        userProfile.setUser(user);
        userProfile.setEmail("test@example.com");
    }

    @Test
    void testGetAllUser() {
        List<User> staffUsers = Arrays.asList(
                new User("staff1", "password", Role.STAFF, store, Status.ACTIVE),
                new User("staff2", "password", Role.STAFF, store, Status.ACTIVE)
        );
        when(userRepository.findByRoleIn(Arrays.asList(Role.STORE_OWNER, Role.STAFF))).thenReturn(staffUsers);

        List<User> allUsers = userService.getAllUser();

        assertEquals(2, allUsers.size());
        verify(userRepository, times(1)).findByRoleIn(Arrays.asList(Role.STORE_OWNER, Role.STAFF));
    }

    @Test
    void testDeactivateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User updatedUser = userService.deactivateUser(1L);

        assertEquals(Status.INACTIVE, updatedUser.getStatus());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void testGetStatusByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        String status = userService.getStatusByUsername("testuser");

        assertEquals(Status.ACTIVE.toString(), status);
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testChangePasswordByUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedpassword");

        UserDTO updatedUser = userService.changePasswordByUserId(1L, "newpassword");

        assertNotNull(updatedUser);
        assertEquals("testuser", updatedUser.getUsername());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetRoleByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        String role = userService.getRoleByUsername("testuser");

        assertEquals(Role.STORE_OWNER.toString(), role);
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testRegisterUser() {
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setUsername("newuser");
        registerRequestDTO.setPassword("password");
        registerRequestDTO.setEmail("newuser@example.com");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userProfileRepository.findByEmail("newuser@example.com")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encodedpassword");

        userService.registerUser(registerRequestDTO);

        verify(userRepository, times(1)).save(any(User.class));
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    void testUpdateUser() {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setFullName("Updated Name");
        userUpdateDTO.setGender(Gender.MALE);
        userUpdateDTO.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUser(user)).thenReturn(userProfile);

        UserProfileDTO updatedProfile = userService.updateUser(1L, userUpdateDTO);

        assertNotNull(updatedProfile);
        assertEquals("Updated Name", updatedProfile.getFullName());
        assertEquals("Male", updatedProfile.getGender());

        verify(userRepository, times(1)).save(user);
        verify(userProfileRepository, times(1)).save(userProfile);
    }

    @Test
    void testCreateStaffAccount() {
        StaffCreateDTO staffCreateDTO = new StaffCreateDTO();
        staffCreateDTO.setUsername("newstaff");
        staffCreateDTO.setPassword("password");

        when(userRepository.findByUsername("newstaff")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedpassword");

        UserDTO createdStaff = userService.createStaffAccount(staffCreateDTO);

        assertNotNull(createdStaff);
        assertEquals(Role.STAFF, createdStaff.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }
}