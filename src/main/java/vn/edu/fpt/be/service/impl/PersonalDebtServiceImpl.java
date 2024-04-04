package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.be.dto.ProductDTO;
import vn.edu.fpt.be.dto.request.PersonalDebtCreateRequest;
import vn.edu.fpt.be.dto.request.PersonalDebtHistoryRequest;
import vn.edu.fpt.be.dto.response.PersonalDebtResponse;
import vn.edu.fpt.be.model.PersonalDebt;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.DebtType;
import vn.edu.fpt.be.repository.PersonalDebtRepository;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.service.PersonalDebtHistoryService;
import vn.edu.fpt.be.service.PersonalDebtService;
import vn.edu.fpt.be.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalDebtServiceImpl implements PersonalDebtService {
    private final PersonalDebtRepository personalDebtRepository;
    private final StoreRepository storeRepository;
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private PersonalDebtHistoryService personalDebtHistoryService;
    @Override
    @Transactional
    public PersonalDebtResponse createPersonalDebt(PersonalDebtCreateRequest request) {
        try {
            User currentUser = userService.getCurrentUser();
            Store currentStore = currentUser.getStore();

            PersonalDebt personalDebt = new PersonalDebt();
            if (request.getCreditorName() == null) {
                throw new IllegalArgumentException("The creditor name can not be null");
            }
            personalDebt.setCreditorName(request.getCreditorName());
            personalDebt.setCreditorPhone(request.getCreditorPhone());
            personalDebt.setCreditorAddress(request.getCreditorAddress());
            if (request.getAmount() <= 0) {
                throw new IllegalArgumentException("The amount must be greater than zero");
            }
            personalDebt.setAmount(request.getAmount());
            if (request.getNote() == null) {
                throw new IllegalArgumentException("The note can not be null");
            }
            personalDebt.setNote(request.getNote());
            personalDebt.setStore(currentStore);

            PersonalDebt createdPersonalDebt = personalDebtRepository.save(personalDebt);

            PersonalDebtHistoryRequest historyRequest = PersonalDebtHistoryRequest.builder()
                    .amount(personalDebt.getAmount())
                    .type(DebtType.DEBT)
                    .note("Khoản nợ ban đầu")
                    .build();
            personalDebtHistoryService.createPersonalDebtHistory(createdPersonalDebt.getId(), historyRequest);

            return PersonalDebtResponse.builder()
                    .id(createdPersonalDebt.getId())
                    .createdAt(createdPersonalDebt.getCreatedAt())
                    .creditorName(createdPersonalDebt.getCreditorName())
                    .creditorPhone(createdPersonalDebt.getCreditorPhone())
                    .creditorAddress(createdPersonalDebt.getCreditorAddress())
                    .amount(createdPersonalDebt.getAmount())
                    .note(createdPersonalDebt.getNote())
                    .store(createdPersonalDebt.getStore())
                    .build();
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database integrity violation", e);
        } catch (DataAccessException e) {
            throw new RuntimeException("Data access exception occurred", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred", e);
        }
    }

    @Override
    public List<PersonalDebtResponse> getAllPersonalDebtOfStore() {
        User currentUser = userService.getCurrentUser();
        Store currentStore = currentUser.getStore();
        if (currentStore == null) {
            throw new IllegalArgumentException("Store can not be null");
        }
        List<PersonalDebt> personalDebts = personalDebtRepository.findByStoreIdOrderByCreatedAtDesc(currentStore.getId());

        return personalDebts.stream()
                .map(debt -> modelMapper.map(debt, PersonalDebtResponse.class))
                .collect(Collectors.toList());
    }
}
