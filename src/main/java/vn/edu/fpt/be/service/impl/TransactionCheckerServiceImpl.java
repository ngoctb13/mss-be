package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.request.TransactionDetails;
import vn.edu.fpt.be.service.TransactionCheckerService;

import javax.mail.*;
import javax.mail.search.AndTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.SubjectTerm;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TransactionCheckerServiceImpl implements TransactionCheckerService {

    private final SimpMessageSendingOperations messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(TransactionCheckerServiceImpl.class);


    @Override
    @Scheduled(fixedRate = 30000)
    public void checkForNewTransactions() {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.starttls.enable", "true");

        Session emailSession = Session.getDefaultInstance(props);

        try {
            Store store = emailSession.getStore("imaps");
            store.connect("imap.gmail.com", "ngoctb56@gmail.com", "vferrepjwhinzldl");
            logger.info("Kết nối tới Gmail thành công.");


            Folder inbox = store.getFolder("inbox");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.search(
                    new AndTerm(new FromStringTerm("mailalert@acb.com.vn"),
                            new SubjectTerm("ACB-Dich vu bao so du tu dong")));

            for (Message message : messages) {
                logger.info("Subject: " + message.getSubject());
                String content = getTextFromMessage(message);
                TransactionDetails details = extractTransactionDetails(content);
                if (details.getTransactionId() != null && details.getAmount() != null) {
                    messagingTemplate.convertAndSend("/topic/transaction", details);
                }
            }

            inbox.close(false);
            store.close();

        } catch (MessagingException e) {
            logger.error("Kết nối tới Gmail thất bại: ", e);
        } catch (IOException e) {
            logger.error("Lỗi IO khi xử lý email: ", e);
        }
    }
    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        if (message.isMimeType("text/*")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/*")) {
                    return (String) bodyPart.getContent();
                }
            }
        }
        throw new MessagingException("Không tìm thấy nội dung text trong email");
    }
    private TransactionDetails extractTransactionDetails(String content) {
        int englishContentStart = content.indexOf("Dear Customers,");
        String englishContent = content;
        if (englishContentStart != -1) {
            englishContent = content.substring(englishContentStart);
        }
        logger.info("english content: " + englishContent);

        Pattern amountPattern = Pattern.compile("Latest transaction: Credit <b>([^<]+)</b>");
        Pattern transactionIdPattern = Pattern.compile("Content: <b>(.+)</b>");

        Matcher amountMatcher = amountPattern.matcher(englishContent);
        Matcher transactionIdMatcher = transactionIdPattern.matcher(englishContent);

        TransactionDetails details = new TransactionDetails();

        if (amountMatcher.find()) {
            String amountString = amountMatcher.group(1).replaceAll("\\D", "");
            int a = (Integer.parseInt(amountString));
            double amount = Double.parseDouble(String.valueOf(a/100)); // Chuyển đổi sang kiểu Double
            details.setAmount(amount); // Gán giá trị vào details
        }

        if (transactionIdMatcher.find()) {
            details.setTransactionId(transactionIdMatcher.group(1).trim());
        }

        logger.info("transactionId: " +details.getTransactionId());
        logger.info("Amount: " + details.getAmount());

        return details;
    }
}
