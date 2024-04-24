package vn.edu.fpt.be;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.fpt.be.dto.request.DebtPaymentRequest;
import vn.edu.fpt.be.dto.request.DebtRecordRequest;
import vn.edu.fpt.be.dto.response.DebtRecordResponse;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.DebtRecord;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.RecordType;
import vn.edu.fpt.be.model.enums.SourceType;
import vn.edu.fpt.be.repository.CustomerRepository;
import vn.edu.fpt.be.repository.DebtRecordRepository;
import vn.edu.fpt.be.service.DebtPaymentHistoryService;
import vn.edu.fpt.be.service.UserService;
import vn.edu.fpt.be.service.impl.DebtRecordServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DebtRecordServiceImplTest {

    @Mock
    private DebtRecordRepository debtRecordRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserService userService;

    @Mock
    private DebtPaymentHistoryService debtPaymentHistoryService;

    @InjectMocks
    private DebtRecordServiceImpl debtRecordService;

    private User user;
    private Customer customer;
    private Store store;
    private DebtRecord debtRecord;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        customer = new Customer();
        store = new Store();
        user.setStore(store);
        customer.setStore(store);
        customer.setTotalDebt(0.0);
        debtRecord = new DebtRecord();
        debtRecord.setId(1L);
        debtRecord.setCustomer(customer);
    }

    @Test
    void testCreateDebtRecord() {
        DebtRecordRequest request = new DebtRecordRequest();
        request.setCustomerId(customer.getId());
        request.setDebtAmount(500.0);
        request.setRecordDate(LocalDateTime.now());
        request.setNote("Test debt record");

        when(userService.getCurrentUser()).thenReturn(user);
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(debtRecordRepository.save(any(DebtRecord.class))).thenReturn(debtRecord);

        DebtRecordResponse result = debtRecordService.createDebtRecord(request, SourceType.SALE_INVOICE, null);

        assertNotNull(result);
        assertEquals(debtRecord.getCustomer(), result.getCustomer());
        assertEquals(debtRecord.getDebtAmount(), result.getDebtAmount());
        assertEquals(debtRecord.getRecordDate(), result.getRecordDate());
        assertEquals(debtRecord.getNote(), result.getNote());

        verify(debtPaymentHistoryService, times(1)).saveDebtPaymentHistory(any(DebtPaymentRequest.class));
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testGetDebtRecordById() {
        Long debtRecordId = 1L;

        when(userService.getCurrentUser()).thenReturn(user);
        when(debtRecordRepository.findById(debtRecordId)).thenReturn(Optional.of(debtRecord));

        DebtRecordResponse result = debtRecordService.getDebtRecordById(debtRecordId);

        assertNotNull(result);
        assertEquals(debtRecord.getId(), result.getId());
        assertEquals(debtRecord.getCustomer(), result.getCustomer());
        assertEquals(debtRecord.getDebtAmount(), result.getDebtAmount());
        assertEquals(debtRecord.getRecordDate(), result.getRecordDate());
        assertEquals(debtRecord.getNote(), result.getNote());
    }

    @Test
    void testCreateDebtPaymentRequest() {
        DebtRecord debtRecord = new DebtRecord();
        debtRecord.setId(1L);
        debtRecord.setCustomer(customer);
        debtRecord.setDebtAmount(500.0);
        debtRecord.setRecordDate(LocalDateTime.now());
        debtRecord.setNote("Test debt record");

        DebtPaymentRequest result = debtRecordService.createDebtPaymentRequest(debtRecord, SourceType.SALE_INVOICE, null);

        assertNotNull(result);
        assertEquals(customer.getId(), result.getCustomerId());
        assertEquals(RecordType.SALE_INVOICE, result.getType());
        assertEquals(debtRecord.getDebtAmount(), result.getAmount());
        assertEquals(debtRecord.getId(), result.getSourceId());
        assertEquals(SourceType.SALE_INVOICE, result.getSourceType());
        assertEquals(debtRecord.getRecordDate(), result.getRecordDate());
        assertEquals(debtRecord.getNote(), result.getNote());
    }

}