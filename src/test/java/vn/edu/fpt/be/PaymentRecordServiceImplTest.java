package vn.edu.fpt.be;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.fpt.be.dto.request.DebtPaymentRequest;
import vn.edu.fpt.be.dto.request.PaymentRecordRequest;
import vn.edu.fpt.be.dto.response.PaymentRecordResponse;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.PaymentRecord;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.RecordType;
import vn.edu.fpt.be.model.enums.SourceType;
import vn.edu.fpt.be.repository.CustomerRepository;
import vn.edu.fpt.be.repository.PaymentRecordRepository;
import vn.edu.fpt.be.service.DebtPaymentHistoryService;
import vn.edu.fpt.be.service.UserService;
import vn.edu.fpt.be.service.impl.PaymentRecordServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentRecordServiceImplTest {

    @Mock
    private PaymentRecordRepository paymentRecordRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserService userService;

    @Mock
    private DebtPaymentHistoryService debtPaymentHistoryService;

    @InjectMocks
    private PaymentRecordServiceImpl paymentRecordService;

    private User user;
    private Customer customer;
    private Store store;
    private PaymentRecord paymentRecord;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        customer = new Customer();
        store = new Store();
        user.setStore(store);
        customer.setStore(store);
        customer.setTotalDebt(1000.0);
        paymentRecord = new PaymentRecord();
        paymentRecord.setId(1L);
        paymentRecord.setCustomer(customer);
    }

    @Test
    void testCreatePaymentRecord() {
        PaymentRecordRequest request = new PaymentRecordRequest();
        request.setCustomerId(customer.getId());
        request.setPaymentAmount(500.0);
        request.setRecordDate(LocalDateTime.now());
        request.setNote("Test payment record");

        when(userService.getCurrentUser()).thenReturn(user);
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(paymentRecordRepository.save(any(PaymentRecord.class))).thenReturn(paymentRecord);

        PaymentRecordResponse result = paymentRecordService.createPaymentRecord(request, SourceType.SALE_INVOICE, null);

        assertNotNull(result);
        assertEquals(paymentRecord.getCustomer(), result.getCustomer());
        assertEquals(paymentRecord.getPaymentAmount(), result.getPaymentAmount());
        assertEquals(paymentRecord.getRecordDate(), result.getRecordDate());
        assertEquals(paymentRecord.getNote(), result.getNote());

        verify(debtPaymentHistoryService, times(1)).saveDebtPaymentHistory(any(DebtPaymentRequest.class));
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testGetPaymentRecordById() {
        Long paymentRecordId = 1L;

        when(userService.getCurrentUser()).thenReturn(user);
        when(paymentRecordRepository.findById(paymentRecordId)).thenReturn(Optional.of(paymentRecord));

        PaymentRecordResponse result = paymentRecordService.getPaymentRecordById(paymentRecordId);

        assertNotNull(result);
        assertEquals(paymentRecord.getId(), result.getId());
        assertEquals(paymentRecord.getCustomer(), result.getCustomer());
        assertEquals(paymentRecord.getPaymentAmount(), result.getPaymentAmount());
        assertEquals(paymentRecord.getRecordDate(), result.getRecordDate());
        assertEquals(paymentRecord.getNote(), result.getNote());
    }

    @Test
    void testCreateDebtPaymentRequest() {
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setId(1L);
        paymentRecord.setCustomer(customer);
        paymentRecord.setPaymentAmount(500.0);
        paymentRecord.setRecordDate(LocalDateTime.now());
        paymentRecord.setNote("Test payment record");

        DebtPaymentRequest result = paymentRecordService.createDebtPaymentRequest(paymentRecord, SourceType.SALE_INVOICE, null);

        assertNotNull(result);
        assertEquals(customer.getId(), result.getCustomerId());
        assertEquals(RecordType.PAYMENT, result.getType());
        assertEquals(paymentRecord.getPaymentAmount(), result.getAmount());
        assertEquals(paymentRecord.getId(), result.getSourceId());
        assertEquals(SourceType.SALE_INVOICE, result.getSourceType());
        assertEquals(paymentRecord.getRecordDate(), result.getRecordDate());
        assertEquals(paymentRecord.getNote(), result.getNote());
    }

    @Test
    void testFormatDateTime() {
        String dateTimeStr = "2023-04-24T12:34:56";
        String expected = "ngày 24 tháng 04 năm 2023 giờ 12:34:56";

        String result = PaymentRecordServiceImpl.formatDateTime(dateTimeStr);

        assertEquals(expected, result);
    }

}