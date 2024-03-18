package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.exception.CustomServiceException;
import vn.edu.fpt.be.model.ForgotPasswordToken;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.UserProfile;
import vn.edu.fpt.be.repository.ForgotPasswordTokenRepository;
import vn.edu.fpt.be.repository.UserProfileRepository;
import vn.edu.fpt.be.service.ForgotPasswordService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForgotPasswordServiceImpl implements ForgotPasswordService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${mss.app.fe-url}")
    private String feHost;
    @Value("${spring.mail.username}")
    private String fromEmail;
    private final int MINUTES = 10;
    private final ForgotPasswordTokenRepository forgotPasswordTokenRepository;
    private final UserProfileRepository userProfileRepository;
    @Override
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public LocalDateTime expireTimeRange() {
        return LocalDateTime.now().plusMinutes(MINUTES);
    }

    @Override
    public void sendEmail(String to, String subject, String emailLink) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        String emailContent = "<p>Xin chào!</p>"
                            + "Truy cập đường link bên dưới để đặt lại mật khẩu"
                            + "<p><a href=\"" + emailLink + "\">Thay đổi mật khẩu</a></p>"
                            + "<br>"
                            + "Bỏ qua nếu bạn không thực hiện yêu cầu này!";
        helper.setText(emailContent, true);
        helper.setFrom(fromEmail, "G50 - FPT University");
        helper.setSubject(subject);
        helper.setTo(to);

        javaMailSender.send(message);
    }

    @Override
    public void requestForgotPassword(String email) {
        String emailLink = createForgotPasswordToken(email);
        try {
            sendEmail(email, "Đặt lại mật khẩu", emailLink);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String createForgotPasswordToken(String email) {
        try {
            UserProfile userProfile = userProfileRepository.findByEmail(email);
            if (userProfile.getUser() == null) {
                throw new RuntimeException("This email not belong to any user");
            }
            User user = userProfile.getUser();

            ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
            forgotPasswordToken.setExpireTime(expireTimeRange());
            forgotPasswordToken.setToken(generateToken());
            forgotPasswordToken.setUser(user);
            forgotPasswordToken.setUsed(false);

            forgotPasswordTokenRepository.save(forgotPasswordToken);

            return feHost + "/reset-password?token=" + forgotPasswordToken.getToken();
        } catch (DataAccessException e) {
            throw new CustomServiceException("Fail to update supplier: " + e.getMessage(), e);
        }
    }
}
