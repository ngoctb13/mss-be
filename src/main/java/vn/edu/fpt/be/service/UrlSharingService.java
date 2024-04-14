package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.response.CustomerInformation;
import vn.edu.fpt.be.dto.response.DebtPaymentResponse;

import java.util.List;

public interface UrlSharingService {
    String generateSharingUrl(Long customerId);
    List<DebtPaymentResponse> getAllTransactionHistoryByUrlSharingToken(String sharingToken);
    CustomerInformation getCustomerInformation(String sharingToken);
    boolean isExpired(String sharingToken);
}
