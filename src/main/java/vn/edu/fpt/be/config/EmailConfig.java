//package vn.edu.fpt.be.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
//import java.util.Properties;
//
//@Configuration
//public class EmailConfig {
//
//    @Bean(name = "senderMailSender")
//    public JavaMailSender senderMailSender(@Value("${spring.mail.sender.host}") String host,
//                                           @Value("${spring.mail.sender.port}") int port,
//                                           @Value("${spring.mail.sender.username}") String username,
//                                           @Value("${spring.mail.sender.password}") String password,
//                                           @Value("${spring.mail.sender.properties.mail.smtp.auth}") boolean auth,
//                                           @Value("${spring.mail.sender.properties.mail.smtp.starttls.enable}") boolean starttls,
//                                           @Value("${spring.mail.sender.properties.mail.smtp.starttls.required}") boolean starttlsRequired,
//                                           @Value("${spring.mail.sender.properties.mail.smtp.ssl.trust}") String sslTrust) {
//        return createMailSender(host, port, username, password, auth, starttls, starttlsRequired, sslTrust);
//    }
//
//    @Bean(name = "readerMailSender")
//    public JavaMailSender readerMailSender(@Value("${spring.mail.reader.host}") String host,
//                                           @Value("${spring.mail.reader.port}") int port,
//                                           @Value("${spring.mail.reader.username}") String username,
//                                           @Value("${spring.mail.reader.password}") String password,
//                                           @Value("${spring.mail.reader.properties.mail.smtp.auth}") boolean auth,
//                                           @Value("${spring.mail.reader.properties.mail.smtp.starttls.enable}") boolean starttls,
//                                           @Value("${spring.mail.reader.properties.mail.smtp.starttls.required}") boolean starttlsRequired,
//                                           @Value("${spring.mail.reader.properties.mail.smtp.ssl.trust}") String sslTrust) {
//        return createMailSender(host, port, username, password, auth, starttls, starttlsRequired, sslTrust);
//    }
//
//    private JavaMailSender createMailSender(String host, int port, String username, String password,
//                                            boolean auth, boolean starttls, boolean starttlsRequired, String sslTrust) {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost(host);
//        mailSender.setPort(port);
//        mailSender.setUsername(username);
//        mailSender.setPassword(password);
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.smtp.auth", auth);
//        props.put("mail.smtp.starttls.enable", starttls);
//        props.put("mail.smtp.starttls.required", starttlsRequired);
//        props.put("mail.smtp.ssl.trust", sslTrust);
//
//        return mailSender;
//    }
//}
//
