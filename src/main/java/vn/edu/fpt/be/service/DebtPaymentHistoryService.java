package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.request.DebtPaymentRequest;

public interface DebtPaymentHistoryService {
    void saveDebtPaymentHistory (DebtPaymentRequest request);
}
