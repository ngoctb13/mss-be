package vn.edu.fpt.be.service.impl;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.be.dto.request.DebtPaymentRequest;
import vn.edu.fpt.be.dto.request.DebtRecordRequest;
import vn.edu.fpt.be.dto.request.PaymentRecordRequest;
import vn.edu.fpt.be.dto.response.DebtRecordResponse;
import vn.edu.fpt.be.dto.response.PaymentRecordResponse;
import vn.edu.fpt.be.exception.CustomServiceException;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.DebtRecord;
import vn.edu.fpt.be.model.PaymentRecord;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.RecordType;
import vn.edu.fpt.be.model.enums.SourceType;
import vn.edu.fpt.be.repository.CustomerRepository;
import vn.edu.fpt.be.repository.DebtRecordRepository;
import vn.edu.fpt.be.service.DebtPaymentHistoryService;
import vn.edu.fpt.be.service.DebtRecordService;
import vn.edu.fpt.be.service.UserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DebtRecordServiceImpl implements DebtRecordService {
    private final DebtRecordRepository debtRecordRepository;
    private final CustomerRepository customerRepository;
    private final UserService userService;
    private final DebtPaymentHistoryService debtPaymentHistoryService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    @Transactional
    public DebtRecordResponse createDebtRecord(DebtRecordRequest request, SourceType sourceType, Long sourceId) {
        try {
            User currentUser = userService.getCurrentUser();
            Optional<Customer> customer = customerRepository.findById(request.getCustomerId());
            if (customer.isEmpty()) {
                throw new RuntimeException("Customer cannot be null");
            }
            if (!customer.get().getStore().equals(currentUser.getStore())) {
                throw new RuntimeException("This customer not belongs to current store");
            }

            DebtRecord debtRecord = new DebtRecord();
            debtRecord.setCustomer(customer.get());

            if (request.getDebtAmount() == 0) {
                throw new RuntimeException("The debt amount can not equal zero");
            }

            debtRecord.setRecordDate(request.getRecordDate());
            debtRecord.setDebtAmount(request.getDebtAmount());
            debtRecord.setNote(request.getNote());
            debtRecord.setCreatedBy(currentUser.getUsername());

            DebtRecord createdDebtRecord = debtRecordRepository.save(debtRecord);
            DebtPaymentRequest debtPaymentRequest = createDebtPaymentRequest(createdDebtRecord, sourceType, sourceId);
            debtPaymentHistoryService.saveDebtPaymentHistory(debtPaymentRequest);
            updateDebtForCustomer(customer.get(), request.getDebtAmount());

            return modelMapper.map(createdDebtRecord, DebtRecordResponse.class);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database integrity violation", e);
        } catch (DataAccessException e) {
            throw new RuntimeException("Data access exception occurred", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred", e);
        }
    }

    @Override
    public DebtRecordResponse getDebtRecordById(Long debtRecordId) {
        User currentUser = userService.getCurrentUser();
        Optional<DebtRecord> debtRecord = debtRecordRepository.findById(debtRecordId);
        if (debtRecord.isEmpty()) {
            throw new RuntimeException("Not found payment record with id " + debtRecordId);
        }
        Customer customer = debtRecord.get().getCustomer();
        if (!customer.getStore().equals(currentUser.getStore())) {
            throw new RuntimeException("This debt record not belongs to current store");
        }

        return modelMapper.map(debtRecord.get(), DebtRecordResponse.class);
    }

    public DebtPaymentRequest createDebtPaymentRequest(DebtRecord debtRecord, SourceType sourceType, Long sourceId) {
        DebtPaymentRequest debtPaymentRequest = new DebtPaymentRequest();
        debtPaymentRequest.setCustomerId(debtRecord.getCustomer().getId());
        debtPaymentRequest.setType(RecordType.SALE_INVOICE);
        debtPaymentRequest.setAmount(debtRecord.getDebtAmount());
        if (sourceId == null || sourceId == 0) {
            debtPaymentRequest.setSourceId(debtRecord.getId());
        } else {
            debtPaymentRequest.setSourceId(sourceId);
        }
        debtPaymentRequest.setSourceType(sourceType);
        debtPaymentRequest.setRecordDate(debtRecord.getRecordDate());
        debtPaymentRequest.setNote(debtRecord.getNote());

        return debtPaymentRequest;
    }

    private void updateDebtForCustomer(Customer customer, double amount) {
        try {
            double oldDebt = customer.getTotalDebt();
            customer.setTotalDebt(oldDebt + amount);
            customerRepository.save(customer);
        } catch (DataAccessException e) {
            throw new CustomServiceException("Fail to update customer: " + e.getMessage(), e);
        }
    }
}
