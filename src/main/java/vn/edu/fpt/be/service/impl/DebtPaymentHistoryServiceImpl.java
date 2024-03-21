package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.request.DebtPaymentRequest;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.DebtPaymentHistory;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.repository.CustomerRepository;
import vn.edu.fpt.be.repository.DebtPaymentHistoryRepository;
import vn.edu.fpt.be.service.DebtPaymentHistoryService;
import vn.edu.fpt.be.service.UserService;

import java.util.Optional;

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
            debtPaymentHistory.setAmount(request.getAmount());
            debtPaymentHistory.setRecordDate(request.getRecordDate());
            debtPaymentHistory.setNote(request.getNote());

            repo.save(debtPaymentHistory);
        } catch (Exception e) {
            throw new RuntimeException("Error save debt payment history record: ", e);
        }

    }
}
