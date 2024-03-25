package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.be.dto.request.DebtPaymentRequest;
import vn.edu.fpt.be.dto.request.PaymentRecordRequest;
import vn.edu.fpt.be.dto.response.PaymentRecordResponse;
import vn.edu.fpt.be.exception.CustomServiceException;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.PaymentRecord;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.RecordType;
import vn.edu.fpt.be.model.enums.SourceType;
import vn.edu.fpt.be.repository.CustomerRepository;
import vn.edu.fpt.be.repository.PaymentRecordRepository;
import vn.edu.fpt.be.service.DebtPaymentHistoryService;
import vn.edu.fpt.be.service.PaymentRecordService;
import vn.edu.fpt.be.service.UserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentRecordServiceImpl implements PaymentRecordService {
    private final PaymentRecordRepository repo;
    private final CustomerRepository customerRepository;
    private final PaymentRecordRepository paymentRecordRepository;
    private final UserService userService;
    private final DebtPaymentHistoryService debtPaymentHistoryService;
    private final ModelMapper modelMapper = new ModelMapper();
    @Override
    @Transactional
    public PaymentRecordResponse createPaymentRecord(PaymentRecordRequest request, SourceType sourceType, Long sourceId) {
        try {
            User currentUser = userService.getCurrentUser();
            Optional<Customer> customer = customerRepository.findById(request.getCustomerId());
            if (customer.isEmpty()) {
                throw new RuntimeException("Customer cannot be null");
            }
            if (!customer.get().getStore().equals(currentUser.getStore())) {
                throw new RuntimeException("This customer not belongs to current store");
            }

            PaymentRecord paymentRecord = new PaymentRecord();
            paymentRecord.setCustomer(customer.get());

            if (request.getPaymentAmount() > customer.get().getTotalDebt()) {
                throw new RuntimeException("Payment amount can not greater than customer total debt");
            }
            paymentRecord.setPaymentAmount(request.getPaymentAmount());
            paymentRecord.setNote(request.getNote());
            paymentRecord.setCreatedBy(currentUser.getUsername());

            PaymentRecord savedPaymentRecord = repo.save(paymentRecord);

            DebtPaymentRequest debtPaymentRequest = createDebtPaymentRequest(savedPaymentRecord, sourceType, sourceId);
            debtPaymentHistoryService.saveDebtPaymentHistory(debtPaymentRequest);
            updateDebtForCustomer(customer.get(), request.getPaymentAmount());
            return modelMapper.map(savedPaymentRecord, PaymentRecordResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error create payment record: ", e);
        }
    }

    @Override
    public PaymentRecordResponse getPaymentRecordById(Long paymentRecordId) {
        User currentUser = userService.getCurrentUser();
        Optional<PaymentRecord> paymentRecord = paymentRecordRepository.findById(paymentRecordId);
        if (paymentRecord.isEmpty()) {
            throw new RuntimeException("Not found payment record with id " + paymentRecordId);
        }
        Customer customer = paymentRecord.get().getCustomer();
        if (!customer.getStore().equals(currentUser.getStore())) {
            throw new RuntimeException("This payment record not belongs to current store");
        }

        return modelMapper.map(paymentRecord.get(), PaymentRecordResponse.class);
    }

    public DebtPaymentRequest createDebtPaymentRequest(PaymentRecord paymentRecord, SourceType sourceType, Long sourceId) {
        DebtPaymentRequest debtPaymentRequest = new DebtPaymentRequest();
        debtPaymentRequest.setCustomerId(paymentRecord.getCustomer().getId());
        debtPaymentRequest.setType(RecordType.PAYMENT);
        debtPaymentRequest.setAmount(paymentRecord.getPaymentAmount());
        if (sourceId == null || sourceId == 0) {
            debtPaymentRequest.setSourceId(paymentRecord.getId());
        } else {
            debtPaymentRequest.setSourceId(sourceId);
        }
        debtPaymentRequest.setSourceType(sourceType);
        debtPaymentRequest.setRecordDate(paymentRecord.getCreatedAt());
        debtPaymentRequest.setNote(paymentRecord.getNote());

        return debtPaymentRequest;
    }

    private void updateDebtForCustomer(Customer customer, double amount) {
        try {
            double oldDebt = customer.getTotalDebt();
            customer.setTotalDebt(oldDebt - amount);
            customerRepository.save(customer);
        } catch (DataAccessException e) {
            throw new CustomServiceException("Fail to update customer: " + e.getMessage(), e);
        }
    }
}
