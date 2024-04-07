package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.request.PaymentRecordRequest;
import vn.edu.fpt.be.dto.response.PaymentRecordResponse;
import vn.edu.fpt.be.model.enums.SourceType;

public interface PaymentRecordService {
    PaymentRecordResponse createPaymentRecord(PaymentRecordRequest request, SourceType sourceType, Long sourceId);
    PaymentRecordResponse getPaymentRecordById(Long paymentRecordId);

}
