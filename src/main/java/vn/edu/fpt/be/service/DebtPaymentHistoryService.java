package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.request.DebtPaymentRequest;
import vn.edu.fpt.be.dto.request.OwnerDebtPaymentHistoryReq;
import vn.edu.fpt.be.dto.response.DebtPaymentResponse;
import vn.edu.fpt.be.dto.response.OwnerDebtPaymentHistoryRes;

import java.time.LocalDateTime;
import java.util.List;

public interface DebtPaymentHistoryService {
    void saveDebtPaymentHistory (DebtPaymentRequest request);
    List<DebtPaymentResponse> getAllTransactionHistoryByCustomer(Long customerId);
    List<DebtPaymentResponse> getAllTransactionHistoryByCustomerAndDateRange(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
    DebtPaymentResponse saveOwnerDebtPaymentHistory(OwnerDebtPaymentHistoryReq req);
    List<DebtPaymentResponse> getAllOwnerTransactionHistoryByCustomerAndDateRange(Long customerId, LocalDateTime startDate, LocalDateTime endDate);

}
