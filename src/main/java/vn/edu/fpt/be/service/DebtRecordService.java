package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.request.DebtPaymentRequest;
import vn.edu.fpt.be.dto.request.DebtRecordRequest;
import vn.edu.fpt.be.dto.request.PaymentRecordRequest;
import vn.edu.fpt.be.dto.response.DebtRecordResponse;
import vn.edu.fpt.be.dto.response.PaymentRecordResponse;
import vn.edu.fpt.be.model.enums.SourceType;

public interface DebtRecordService {
    DebtRecordResponse createDebtRecord(DebtRecordRequest request, SourceType sourceType, Long sourceId);
    DebtRecordResponse getDebtRecordById(Long debtRecordId);

}
