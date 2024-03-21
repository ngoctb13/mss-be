package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.request.PaymentRecordRequest;
import vn.edu.fpt.be.dto.response.PaymentRecordResponse;

public interface PaymentRecordService {
    PaymentRecordResponse createPaymentRecord(PaymentRecordRequest request);
}
