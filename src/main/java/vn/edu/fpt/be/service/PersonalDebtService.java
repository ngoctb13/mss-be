package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.request.PersonalDebtCreateRequest;
import vn.edu.fpt.be.dto.response.PersonalDebtResponse;

import java.util.List;

public interface PersonalDebtService {
    PersonalDebtResponse createPersonalDebt(PersonalDebtCreateRequest request);
    List<PersonalDebtResponse> getAllPersonalDebtOfStore();
}
