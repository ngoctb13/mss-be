package vn.edu.fpt.be;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.fpt.be.dto.request.UpdateProfileRequest;
import vn.edu.fpt.be.dto.response.UserProfileResponse;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.UserProfile;
import vn.edu.fpt.be.model.enums.Gender;
import vn.edu.fpt.be.repository.UserProfileRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.service.UserService;
import vn.edu.fpt.be.service.impl.UserProfileImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserProfileImpl userProfileImpl;

    private User currentUser;
    private UserProfile userProfile;
    private UpdateProfileRequest request;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        String dobString = "1990-01-11";

        // Creating a SimpleDateFormat object to parse the string
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dob = new Date();
        try {
            // Parsing the string and converting it to a Date object
            dob = dateFormat.parse(dobString);

            // Setting the date of birth in the request
            request.setDateOfBirth(dob);
        } catch (Exception e) {
            System.out.println("Error parsing date: " + e.getMessage());
        }
        userProfile = new UserProfile();
        userProfile.setUser(currentUser);
        request = new UpdateProfileRequest();
        request.setEmail("test@example.com");
        request.setFullName("Test User");
        request.setGender(Gender.MALE);
        request.setDateOfBirth(dob);
        request.setPhoneNumber("1234567890");
        request.setIdentityNumber("123456789");
    }

    @Test
    void testCheckEmailExists() {
        when(userProfileRepository.existsByEmail("test@example.com")).thenReturn(true);
        boolean exists = userProfileImpl.checkEmailExists("test@example.com");
        assertTrue(exists);
    }

    @Test
    void testGetCurrentUserProfile() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userProfileRepository.findByUser(currentUser)).thenReturn(userProfile);

        UserProfileResponse response = userProfileImpl.getCurrentUserProfile();
        assertNotNull(response);
    }

    @Test
    void testUpdateCurrentUserProfile_NewProfile() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userProfileRepository.findByUser(currentUser)).thenReturn(null);

        UserProfileResponse response = userProfileImpl.updateCurrentUserProfile(request);
        assertNotNull(response);
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    void testUpdateCurrentUserProfile_ExistingProfile() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userProfileRepository.findByUser(currentUser)).thenReturn(userProfile);

        UserProfileResponse response = userProfileImpl.updateCurrentUserProfile(request);
        assertNotNull(response);
        verify(userProfileRepository, times(1)).save(userProfile);
    }

    @Test
    void testChangePassword() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedpassword");

        userProfileImpl.changePassword("newpassword");
        verify(userRepository, times(1)).save(currentUser);
    }

    @Test
    void testGetUserProfileByUser() {
        when(userProfileRepository.findByUserId(1L)).thenReturn(userProfile);

        UserProfileResponse response = userProfileImpl.getUserProfileByUser(1L);
        assertNotNull(response);
    }
}