package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.request.PersonalDebtHistoryRequest;
import vn.edu.fpt.be.dto.request.PersonalDebtHistoryUpdateReq;
import vn.edu.fpt.be.dto.response.PersonalDebtHistoryResponse;
import vn.edu.fpt.be.model.enums.DebtType;
import vn.edu.fpt.be.service.PersonalDebtHistoryService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personal-debt-history")
public class PersonalDebtHistoryController {
    private final PersonalDebtHistoryService personalDebtHistoryService;

    @PostMapping("/create/{personalDebtId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> createPersonalDebtHistory(@PathVariable Long personalDebtId, @RequestBody PersonalDebtHistoryRequest request) {
        try {
            PersonalDebtHistoryResponse personalDebtHistoryResponse = personalDebtHistoryService.createPersonalDebtHistory(personalDebtId, request);
            return new ResponseEntity<>(personalDebtHistoryResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/all/{personalDebtId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> getAllPersonalDebt(@PathVariable Long personalDebtId) {
        try {
            List<PersonalDebtHistoryResponse> responses = personalDebtHistoryService.listByPersonalDebt(personalDebtId);
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/filter-date-type/{personalDebtId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> getByDateRangeAndType(@PathVariable Long personalDebtId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                @RequestParam(required = false) DebtType type) {
        try {
            List<PersonalDebtHistoryResponse> responses = personalDebtHistoryService.filterByDateRangeAndType(personalDebtId ,startDate, endDate, type);
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{historyId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> updatePersonalDebtHistory(@PathVariable Long historyId, @RequestBody PersonalDebtHistoryUpdateReq req) {
        try {
            PersonalDebtHistoryResponse updatedHistory = personalDebtHistoryService.updatePersonalDebtHistory(historyId, req);
            return new ResponseEntity<>(updatedHistory, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
