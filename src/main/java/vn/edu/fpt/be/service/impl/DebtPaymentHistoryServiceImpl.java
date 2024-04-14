package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.be.dto.request.DebtPaymentRequest;
import vn.edu.fpt.be.dto.request.OwnerDebtPaymentHistoryReq;
import vn.edu.fpt.be.dto.response.DebtPaymentResponse;
import vn.edu.fpt.be.dto.response.OwnerDebtPaymentHistoryRes;
import vn.edu.fpt.be.exception.CustomServiceException;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.DebtPaymentHistory;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.RecordType;
import vn.edu.fpt.be.repository.CustomerRepository;
import vn.edu.fpt.be.repository.DebtPaymentHistoryRepository;
import vn.edu.fpt.be.service.DebtPaymentHistoryService;
import vn.edu.fpt.be.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DebtPaymentHistoryServiceImpl implements DebtPaymentHistoryService {
    private final UserService userService;
    private final CustomerRepository customerRepository;
    private final DebtPaymentHistoryRepository repo;
    private final ModelMapper modelMapper = new ModelMapper();
    @Override
    public void saveDebtPaymentHistory(DebtPaymentRequest request) {
        try {
            User currentUser = userService.getCurrentUser();
            Optional<Customer> customer = customerRepository.findById(request.getCustomerId());
            if (customer.isEmpty()) {
                throw new RuntimeException("Customer cannot be null");
            }
            if (!customer.get().getStore().equals(currentUser.getStore())) {
                throw new RuntimeException("This customer not belongs to current store");
            }

            DebtPaymentHistory debtPaymentHistory = new DebtPaymentHistory();
            debtPaymentHistory.setCustomer(customer.get());
            debtPaymentHistory.setType(request.getType());
            if (request.getSourceId() == null) {
                throw new RuntimeException("source id cannot be null");
            }
            debtPaymentHistory.setSourceId(request.getSourceId());
            debtPaymentHistory.setSourceType(request.getSourceType());
            debtPaymentHistory.setAmount(request.getAmount());
            debtPaymentHistory.setRecordDate(request.getRecordDate());
            debtPaymentHistory.setNote(request.getNote());

            repo.save(debtPaymentHistory);
        } catch (Exception e) {
            throw new RuntimeException("Error save debt payment history record: ", e);
        }

    }

    @Override
    public List<DebtPaymentResponse> getAllTransactionHistoryByCustomer(Long customerId) {
        User currentUser = userService.getCurrentUser();
        Store store = currentUser.getStore();

        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isEmpty()) {
            throw new RuntimeException("Not found customer with id " + customerId);
        }
        if (!customer.get().getStore().equals(store)) {
            throw new RuntimeException("This customer not belongs to current store");
        }

        List<DebtPaymentHistory> list = repo.findByCustomerIdOrderByRecordDateDesc(customerId);
        return list.stream()
                .map(debtPaymentHistory -> modelMapper.map(debtPaymentHistory, DebtPaymentResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DebtPaymentResponse> getAllTransactionHistoryByCustomerAndDateRange(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = userService.getCurrentUser();
        Store store = currentUser.getStore();

        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isEmpty()) {
            throw new RuntimeException("Not found customer with id " + customerId);
        }
        if (!customer.get().getStore().equals(store)) {
            throw new RuntimeException("This customer not belongs to current store");
        }

        List<DebtPaymentHistory> list = repo.findByCustomerIdAndOptionalCreatedAtRange(customerId, startDate, endDate);
        return list.stream()
                .map(debtPaymentHistory -> modelMapper.map(debtPaymentHistory, DebtPaymentResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DebtPaymentResponse saveOwnerDebtPaymentHistory(OwnerDebtPaymentHistoryReq req) {
        try {
            User currentUser = userService.getCurrentUser();
            Store currentStore = currentUser.getStore();
            Optional<Customer> customer = customerRepository.findById(req.getCustomerId());
            if (customer.isEmpty()) {
                throw new IllegalArgumentException("Not found any customer with id " + req.getCustomerId());
            }
            if (!customer.get().getStore().equals(currentStore)) {
                throw new IllegalArgumentException("This customer not belongs to current store");
            }
            if (req.getAmount() <= 0 ) {
                throw new IllegalArgumentException("the amount can not be equal zero");
            }

            DebtPaymentHistory debtPaymentHistory = new DebtPaymentHistory();
            debtPaymentHistory.setCustomer(customer.get());
            debtPaymentHistory.setCreatedBy(currentUser.getUsername());
            debtPaymentHistory.setType(req.getType());
            debtPaymentHistory.setAmount(req.getAmount());
            debtPaymentHistory.setRecordDate(req.getRecordDate());
            debtPaymentHistory.setNote(req.getNote());

            DebtPaymentHistory savedDebtPaymentHistory = repo.save(debtPaymentHistory);
            updateDebtForCustomer(customer.get(), req);

            return modelMapper.map(savedDebtPaymentHistory, DebtPaymentResponse.class);

        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database integrity violation", e);
        } catch (DataAccessException e) {
            throw new RuntimeException("Data access exception occurred", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred", e);
        }
    }

    @Override
    public List<DebtPaymentResponse> getAllOwnerTransactionHistoryByCustomerAndDateRange(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = userService.getCurrentUser();
        Store store = currentUser.getStore();

        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isEmpty()) {
            throw new RuntimeException("Not found customer with id " + customerId);
        }
        if (!customer.get().getStore().equals(store)) {
            throw new RuntimeException("This customer not belongs to current store");
        }

        List<DebtPaymentHistory> list = repo.findByCustomerIdAndOptionalCreatedAtRange(customerId, startDate, endDate);

        return list.stream()
                .filter(history -> history.getType() == RecordType.OWNER_DEBT || history.getType() == RecordType.OWNER_PAID)
                .map(debtPaymentHistory -> modelMapper.map(debtPaymentHistory, DebtPaymentResponse.class))
                .collect(Collectors.toList());
    }

    private void updateDebtForCustomer(Customer customer, OwnerDebtPaymentHistoryReq req) {
        try {
            double oldDebt = customer.getTotalDebt();
            if (req.getType() == RecordType.OWNER_DEBT) {
                    customer.setTotalDebt(oldDebt - req.getAmount());
            } else {
                customer.setTotalDebt(oldDebt + req.getAmount());
            }
            customerRepository.save(customer);
        } catch (DataAccessException e) {
            throw new CustomServiceException("Fail to update customer: " + e.getMessage(), e);
        }
    }
}
