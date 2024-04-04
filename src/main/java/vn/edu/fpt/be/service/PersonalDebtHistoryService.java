package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.request.PersonalDebtHistoryRequest;
import vn.edu.fpt.be.dto.request.PersonalDebtHistoryUpdateReq;
import vn.edu.fpt.be.dto.response.PersonalDebtHistoryResponse;
import vn.edu.fpt.be.model.enums.DebtType;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonalDebtHistoryService {
    PersonalDebtHistoryResponse createPersonalDebtHistory(Long personalDebtId, PersonalDebtHistoryRequest request);
    List<PersonalDebtHistoryResponse> listByPersonalDebt(Long personalDebtId);
    List<PersonalDebtHistoryResponse> filterByDateRangeAndType(Long personalDebtId, LocalDateTime startDate,
                                                               LocalDateTime endDate,
                                                               DebtType type);
    PersonalDebtHistoryResponse updatePersonalDebtHistory(Long historyId, PersonalDebtHistoryUpdateReq req);
}
