package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.be.dto.request.PersonalDebtHistoryRequest;
import vn.edu.fpt.be.dto.request.PersonalDebtHistoryUpdateReq;
import vn.edu.fpt.be.dto.response.PersonalDebtHistoryResponse;
import vn.edu.fpt.be.dto.response.PersonalDebtResponse;
import vn.edu.fpt.be.model.PersonalDebt;
import vn.edu.fpt.be.model.PersonalDebtHistory;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.DebtType;
import vn.edu.fpt.be.repository.PersonalDebtHistoryRepository;
import vn.edu.fpt.be.repository.PersonalDebtRepository;
import vn.edu.fpt.be.service.PersonalDebtHistoryService;
import vn.edu.fpt.be.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalDebtHistoryServiceImpl implements PersonalDebtHistoryService {
    private final PersonalDebtHistoryRepository personalDebtHistoryRepository;
    private final PersonalDebtRepository personalDebtRepository;
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    @Transactional
    public PersonalDebtHistoryResponse createPersonalDebtHistory(Long personalDebtId, PersonalDebtHistoryRequest request) {
        try {
            PersonalDebt personalDebt = personalDebtRepository.findById(personalDebtId)
                    .orElseThrow(() -> new IllegalArgumentException("PersonalDebt not found"));
            if (request.getAmount() <= 0) {
                throw new IllegalArgumentException("The amount can not be greater and equal zero");
            }
            // Cập nhật khoản nợ
            updatePersonalDebtAmount(personalDebt, request.getType(), request.getAmount(), request.getNote());

            PersonalDebtHistory personalDebtHistory = new PersonalDebtHistory();
            personalDebtHistory.setPersonalDebt(personalDebt);
            personalDebtHistory.setAmount(request.getAmount());
            personalDebtHistory.setType(request.getType());
            if (request.getNote() == null || request.getNote().isEmpty()) {
                if (request.getType() == DebtType.DEBT) {
                    personalDebtHistory.setNote("Khoản nợ được thêm " + formatDateTime(String.valueOf(LocalDateTime.now())));
                } else if (request.getType() == DebtType.PAID) {
                    personalDebtHistory.setNote("Khoản thanh toán được trả " + formatDateTime(String.valueOf(LocalDateTime.now())));
                }
            } else {
                personalDebtHistory.setNote(request.getNote());
            }

            PersonalDebtHistory savedPersonalDebtHistory = personalDebtHistoryRepository.save(personalDebtHistory);
            return PersonalDebtHistoryResponse.builder()
                    .id(savedPersonalDebtHistory.getId())
                    .createdAt(savedPersonalDebtHistory.getCreatedAt())
                    .amount(savedPersonalDebtHistory.getAmount())
                    .type(savedPersonalDebtHistory.getType())
                    .note(savedPersonalDebtHistory.getNote())
                    .personalDebt(personalDebt)
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
    public List<PersonalDebtHistoryResponse> listByPersonalDebt(Long personalDebtId) {
        if (personalDebtId == null) {
            throw new IllegalArgumentException("personalDebtId can not be null");
        }
        User currentUser = userService.getCurrentUser();
        Store currentStore = currentUser.getStore();

        Optional<PersonalDebt> personalDebt = personalDebtRepository.findById(personalDebtId);
        if (personalDebt.isEmpty()) {
            throw new IllegalArgumentException("Can not found any personal debt with id " + personalDebtId);
        }
        if (!personalDebt.get().getStore().equals(currentStore)) {
            throw new IllegalArgumentException("This personal debt not belongs to current store");
        }

        List<PersonalDebtHistory> personalDebtHistories = personalDebtHistoryRepository.findByPersonalDebtIdOrderByCreatedAtDesc(personalDebtId);

        return personalDebtHistories.stream()
                .map(history -> modelMapper.map(history, PersonalDebtHistoryResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonalDebtHistoryResponse> filterByDateRangeAndType(Long personalDebtId, LocalDateTime startDate, LocalDateTime endDate, DebtType type) {
        try {
            if (personalDebtId == null) {
                throw new IllegalArgumentException("personalDebtId can not be null");
            }
            User currentUser = userService.getCurrentUser();
            Store currentStore = currentUser.getStore();

            Optional<PersonalDebt> personalDebt = personalDebtRepository.findById(personalDebtId);
            if (personalDebt.isEmpty()) {
                throw new IllegalArgumentException("Can not found any personal debt with id " + personalDebtId);
            }
            if (!personalDebt.get().getStore().equals(currentStore)) {
                throw new IllegalArgumentException("This personal debt not belongs to current store");
            }

            List<PersonalDebtHistory> histories = personalDebtHistoryRepository.findByDateRangeAndType(personalDebtId, startDate, endDate, type);

            return histories.stream()
                    .map(history -> modelMapper.map(history, PersonalDebtHistoryResponse.class))
                    .collect(Collectors.toList());
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database integrity violation", e);
        } catch (DataAccessException e) {
            throw new RuntimeException("Data access exception occurred", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred", e);
        }
    }

    @Override
    @Transactional
    public PersonalDebtHistoryResponse updatePersonalDebtHistory(Long historyId, PersonalDebtHistoryUpdateReq req) {
        try {
            if (historyId == null) {
                throw new IllegalArgumentException("history id can not be null");
            }
            if (req.getAmount() == null || req.getAmount() == 0) {
                throw new IllegalArgumentException("the amount cannot be null or equal zero");
            }
            User currentUser  = userService.getCurrentUser();
            Store currentStore = currentUser.getStore();
            Optional<PersonalDebtHistory> history = personalDebtHistoryRepository.findById(historyId);
            if (history.isEmpty()) {
                throw new RuntimeException("Not found any personal debt history with id " + historyId);
            }
            if (!history.get().getPersonalDebt().getStore().equals(currentStore)) {
                throw new RuntimeException("This personal debt history not belong to current store");
            }

            updateCurrentPersonalDebtAmount(history.get(), req.getAmount());

            history.get().setAmount(req.getAmount());
            history.get().setNote(req.getNote());

            PersonalDebtHistory updatedHistory = personalDebtHistoryRepository.save(history.get());

            return modelMapper.map(updatedHistory, PersonalDebtHistoryResponse.class);

        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database integrity violation", e);
        } catch (DataAccessException e) {
            throw new RuntimeException("Data access exception occurred", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred", e);
        }
    }

    private void updatePersonalDebtAmount(PersonalDebt personalDebt, DebtType type, Double amount, String note) {
        double updatedAmount = personalDebt.getAmount();
        String preNote = "Khoản nợ ban đầu";
        if (type == DebtType.DEBT) {
            if (Objects.equals(note, preNote)) {
                updatedAmount = amount;
            } else {
                updatedAmount += amount;
            }
        } else if (type == DebtType.PAID) {
            updatedAmount -= amount;
        }

        if (updatedAmount < 0) {
            throw new IllegalArgumentException("The debt amount cannot be negative after payment.");
        }

        personalDebt.setAmount(updatedAmount);
        personalDebtRepository.save(personalDebt);
    }

    private void updateCurrentPersonalDebtAmount(PersonalDebtHistory history ,double newAmount) {
        try {
            PersonalDebt personalDebt = history.getPersonalDebt();
            double oldPersonalDebtAmount = personalDebt.getAmount();
            if (history.getType() == DebtType.DEBT) {
                if (newAmount > history.getAmount()) {
                    personalDebt.setAmount(oldPersonalDebtAmount + (newAmount - history.getAmount()));
                } else {
                    personalDebt.setAmount(oldPersonalDebtAmount - (history.getAmount() - newAmount));
                }
            } else if (history.getType() == DebtType.PAID) {
                if (newAmount > history.getAmount()) {
                    personalDebt.setAmount(oldPersonalDebtAmount - (newAmount - history.getAmount()));
                } else {
                    personalDebt.setAmount(oldPersonalDebtAmount + (history.getAmount() - newAmount));
                }
            }
            personalDebtRepository.save(personalDebt);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database integrity violation", e);
        } catch (DataAccessException e) {
            throw new RuntimeException("Data access exception occurred", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred", e);
        }
    }

    public static String formatDateTime(String dateTimeStr) {
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'ngày' dd 'tháng' MM 'năm' yyyy 'giờ' HH:mm:ss");
        return dateTime.format(formatter);
    }
}
