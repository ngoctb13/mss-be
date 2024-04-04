package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.request.PersonalDebtCreateRequest;
import vn.edu.fpt.be.dto.response.PersonalDebtResponse;
import vn.edu.fpt.be.service.PersonalDebtService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personal-debt")
public class PersonalDebtController {
    private final PersonalDebtService personalDebtService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> createPersonalDebt(@RequestBody PersonalDebtCreateRequest request) {
        try {
            PersonalDebtResponse personalDebtResponse = personalDebtService.createPersonalDebt(request);
            return new ResponseEntity<>(personalDebtResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> getAllPersonalDebtByStore() {
        try {
            List<PersonalDebtResponse> personalDebtResponses = personalDebtService.getAllPersonalDebtOfStore();
            return new ResponseEntity<>(personalDebtResponses, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
