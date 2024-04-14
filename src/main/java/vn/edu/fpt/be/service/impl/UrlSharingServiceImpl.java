package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.response.CustomerInformation;
import vn.edu.fpt.be.dto.response.DebtPaymentResponse;
import vn.edu.fpt.be.model.*;
import vn.edu.fpt.be.repository.CustomerRepository;
import vn.edu.fpt.be.repository.DebtPaymentHistoryRepository;
import vn.edu.fpt.be.repository.UrlSharingTokenRepository;
import vn.edu.fpt.be.service.DebtPaymentHistoryService;
import vn.edu.fpt.be.service.UrlSharingService;
import vn.edu.fpt.be.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UrlSharingServiceImpl implements UrlSharingService {
    @Value("${mss.app.fe-url}")
    private String feHost;
    private final int MINUTES = 10;
    private final CustomerRepository customerRepository;
    private final UrlSharingTokenRepository urlSharingTokenRepository;
    private final DebtPaymentHistoryRepository debtPaymentHistoryRepository;
    private final DebtPaymentHistoryService debtPaymentHistoryService;
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();

    public String generateToken() {
        return UUID.randomUUID().toString();
    }
    public LocalDateTime expireTimeRange() {
        return LocalDateTime.now().plusMinutes(MINUTES);
    }
    @Override
    public String generateSharingUrl(Long customerId) {
        try {
            User currentUser = userService.getCurrentUser();
            Optional<Customer> customer = customerRepository.findById(customerId);
            if (customer.isEmpty()) {
                throw new RuntimeException("Can not found any customer with id " + customerId);
            }
            if (!customer.get().getStore().equals(currentUser.getStore())) {
                throw new RuntimeException("This customer not belongs to current store");
            }

            UrlSharingToken urlSharingToken = new UrlSharingToken();
            urlSharingToken.setToken(generateToken());
            urlSharingToken.setExpireTime(expireTimeRange());
            urlSharingToken.setCustomerId(customerId);

            UrlSharingToken savedUrlSharingToken = urlSharingTokenRepository.save(urlSharingToken);

            return feHost + "customer-transactions?token=" + savedUrlSharingToken.getToken();
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database integrity violation", e);
        } catch (DataAccessException e) {
            throw new RuntimeException("Data access exception occurred", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred", e);
        }
    }

    @Override
    public List<DebtPaymentResponse> getAllTransactionHistoryByUrlSharingToken(String sharingToken) {
        try {
            if (sharingToken == null) {
                throw new RuntimeException("Sharing token can not be null");
            }
            UrlSharingToken urlSharingToken = urlSharingTokenRepository.findByToken(sharingToken);
            if (urlSharingToken == null) {
                throw new IllegalArgumentException("Can not found any UrlSharingToken with token " + sharingToken);
            } else if (isExpired(sharingToken)) {
                throw new IllegalArgumentException("This sharing token is expired");
            }
            Long customerId = urlSharingToken.getCustomerId();
            return getAllTransactionHistoryByCustomer(customerId);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database integrity violation", e);
        } catch (DataAccessException e) {
            throw new RuntimeException("Data access exception occurred", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred", e);
        }
    }

    @Override
    public CustomerInformation getCustomerInformation(String sharingToken) {
        try {
            if (sharingToken == null) {
                throw new RuntimeException("Sharing token can not be null");
            }
            UrlSharingToken urlSharingToken = urlSharingTokenRepository.findByToken(sharingToken);
            if (urlSharingToken == null) {
                throw new IllegalArgumentException("Can not found any UrlSharingToken with token " + sharingToken);
            } else if (isExpired(sharingToken)) {
                throw new IllegalArgumentException("This sharing token is expired");
            }
            Long customerId = urlSharingToken.getCustomerId();

            Optional<Customer> customer = customerRepository.findById(customerId);
            if (customer.isEmpty()) {
                throw new IllegalArgumentException("Not found any customer with id " + customerId);
            }

            return modelMapper.map(customer.get(), CustomerInformation.class);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database integrity violation", e);
        } catch (DataAccessException e) {
            throw new RuntimeException("Data access exception occurred", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred", e);
        }
    }

    @Override
    public boolean isExpired(String sharingToken) {
        UrlSharingToken urlSharingToken = urlSharingTokenRepository.findByToken(sharingToken);
        if (urlSharingToken == null) {
            throw new RuntimeException("Not have any forgot password token available");
        }
        return LocalDateTime.now().isAfter(urlSharingToken.getExpireTime());
    }

    public List<DebtPaymentResponse> getAllTransactionHistoryByCustomer(Long customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isEmpty()) {
            throw new RuntimeException("Not found customer with id " + customerId);
        }
        List<DebtPaymentHistory> list = debtPaymentHistoryRepository.findByCustomerIdOrderByRecordDateDesc(customerId);
        return list.stream()
                .map(debtPaymentHistory -> modelMapper.map(debtPaymentHistory, DebtPaymentResponse.class))
                .collect(Collectors.toList());
    }

}
