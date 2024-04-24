package vn.edu.fpt.be;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.fpt.be.dto.SaleInvoiceDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailRequest;
import vn.edu.fpt.be.dto.request.DebtPaymentRequest;
import vn.edu.fpt.be.dto.request.DebtRecordRequest;
import vn.edu.fpt.be.dto.request.PaymentRecordRequest;
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;
import vn.edu.fpt.be.dto.response.SaleInvoiceReportResponse;
import vn.edu.fpt.be.model.*;
import vn.edu.fpt.be.model.enums.RecordType;
import vn.edu.fpt.be.model.enums.SourceType;
import vn.edu.fpt.be.repository.*;
import vn.edu.fpt.be.service.*;
import vn.edu.fpt.be.service.impl.SaleInvoiceServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaleInvoiceServiceImplTest {

    @Mock
    private SaleInvoiceRepository saleInvoiceRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private SaleInvoiceDetailService saleInvoiceDetailService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private DebtPaymentHistoryService debtPaymentHistoryService;

    @Mock
    private PaymentRecordService paymentRecordService;

    @Mock
    private DebtRecordService debtRecordService;

    @InjectMocks
    private SaleInvoiceServiceImpl saleInvoiceService;

    private User user;
    private Store store;
    private Customer customer;
    private SaleInvoice saleInvoice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        store = new Store();
        user.setStore(store);
        customer = new Customer();
        saleInvoice = new SaleInvoice();
        saleInvoice.setId(1L);
        saleInvoice.setCustomer(customer);
        saleInvoice.setStore(store);
    }

    @Test
    void testCreateSaleInvoice() {
        List<SaleInvoiceDetailRequest> requests = Arrays.asList(
                new SaleInvoiceDetailRequest(1L, 10.0, 2.0),
                new SaleInvoiceDetailRequest(2L, 20.0, 3.0)
        );
        double pricePaid = 70.0;

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(userService.getCurrentUser()).thenReturn(user);
        when(saleInvoiceRepository.save(any(SaleInvoice.class))).thenReturn(saleInvoice);

        SaleInvoiceDTO result = saleInvoiceService.createSaleInvoice(customer.getId(), requests, pricePaid);

        assertNotNull(result);
        assertEquals(saleInvoice.getCustomer(), result.getCustomer());
        assertEquals(saleInvoice.getStore(), result.getStore());
        assertEquals(pricePaid, result.getPricePaid());
        assertEquals(0.0, result.getOldDebt());
        assertEquals(70.0, result.getTotalPrice());
        assertEquals(70.0, result.getTotalPayment());
        assertEquals(0.0, result.getNewDebt());

        verify(saleInvoiceDetailService, times(1)).saveSaleInvoiceDetail(requests, saleInvoice);
    }

    @Test
    void testGetSaleInvoiceByCustomer() {
        Long customerId = 1L;
        List<SaleInvoice> saleInvoices = Arrays.asList(saleInvoice);

        when(userService.getCurrentUser()).thenReturn(user);
        when(saleInvoiceRepository.findByCustomerIdAndStoreIdOrderByCreatedAtDesc(customerId, store.getId())).thenReturn(saleInvoices);

        List<CustomerSaleInvoiceResponse> result = saleInvoiceService.getSaleInvoiceByCustomer(customerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        CustomerSaleInvoiceResponse response = result.get(0);
        assertEquals(saleInvoice.getId(), response.getId());
        assertEquals(saleInvoice.getTotalPrice(), response.getTotalPrice());
        assertEquals(saleInvoice.getOldDebt(), response.getOldDebt());
        assertEquals(saleInvoice.getTotalPayment(), response.getTotalPayment());
        assertEquals(saleInvoice.getPricePaid(), response.getPricePaid());
        assertEquals(saleInvoice.getNewDebt(), response.getNewDebt());
    }

    @Test
    void testGetSaleInvoicesByFilter() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        String createdBy = "user1";
        Long customerId = 1L;
        List<SaleInvoice> invoices = Arrays.asList(saleInvoice);

        when(userService.getCurrentUser()).thenReturn(user);
        when(saleInvoiceRepository.findInvoicesByCriteria(startDate, endDate, createdBy, customerId, store.getId())).thenReturn(invoices);

        List<SaleInvoiceReportResponse> result = saleInvoiceService.getSaleInvoicesByFilter(startDate, endDate, createdBy, customerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        SaleInvoiceReportResponse response = result.get(0);
        assertEquals(saleInvoice.getId(), response.getId());
        assertEquals(saleInvoice.getCreatedBy(), response.getCreatedBy());
        assertEquals(saleInvoice.getTotalPrice(), response.getTotalPrice());
        assertEquals(saleInvoice.getOldDebt(), response.getOldDebt());
        assertEquals(saleInvoice.getTotalPayment(), response.getTotalPayment());
        assertEquals(saleInvoice.getPricePaid(), response.getPricePaid());
        assertEquals(saleInvoice.getNewDebt(), response.getNewDebt());
        assertEquals(saleInvoice.getCustomer(), response.getCustomer());
    }

    @Test
    void testGetSaleInvoiceById() {
        Long saleInvoiceId = 1L;

        when(userService.getCurrentUser()).thenReturn(user);
        when(saleInvoiceRepository.findById(saleInvoiceId)).thenReturn(Optional.of(saleInvoice));

        SaleInvoiceDTO result = saleInvoiceService.getSaleInvoiceById(saleInvoiceId);

        assertNotNull(result);
        assertEquals(saleInvoice.getId(), result.getId());
        assertEquals(saleInvoice.getCustomer(), result.getCustomer());
        assertEquals(saleInvoice.getStore(), result.getStore());
        assertEquals(saleInvoice.getPricePaid(), result.getPricePaid());
        assertEquals(saleInvoice.getOldDebt(), result.getOldDebt());
        assertEquals(saleInvoice.getTotalPrice(), result.getTotalPrice());
        assertEquals(saleInvoice.getTotalPayment(), result.getTotalPayment());
        assertEquals(saleInvoice.getNewDebt(), result.getNewDebt());
    }

    @Test
    void testGetRecentInvoicesByStoreId() {
        Long storeId = 1L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        List<SaleInvoice> invoices = Arrays.asList(saleInvoice);

        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getStore().getId()).thenReturn(storeId);
        when(saleInvoiceRepository.findRecentInvoicesByStoreId(storeId, startDate)).thenReturn(invoices);

        List<SaleInvoiceReportResponse> result = saleInvoiceService.getRecentInvoicesByStoreId();

        assertNotNull(result);
        assertEquals(1, result.size());
        SaleInvoiceReportResponse response = result.get(0);
        assertEquals(saleInvoice.getId(), response.getId());
        assertEquals(saleInvoice.getCreatedBy(), response.getCreatedBy());
        assertEquals(saleInvoice.getTotalPrice(), response.getTotalPrice());
        assertEquals(saleInvoice.getOldDebt(), response.getOldDebt());
        assertEquals(saleInvoice.getTotalPayment(), response.getTotalPayment());
        assertEquals(saleInvoice.getPricePaid(), response.getPricePaid());
        assertEquals(saleInvoice.getNewDebt(), response.getNewDebt());
        assertEquals(saleInvoice.getCustomer(), response.getCustomer());
    }

    @Test
    void testCreatePaymentRecordRequest() {
        double paymentAmount = 50.0;
        LocalDateTime recordDate = LocalDateTime.now();
        saleInvoice.setCreatedAt(recordDate);

        PaymentRecordRequest result = saleInvoiceService.createPaymentRecordRequest(saleInvoice, paymentAmount);

        assertNotNull(result);
        assertEquals(customer.getId(), result.getCustomerId());
        assertEquals(recordDate, result.getRecordDate());
        assertEquals(paymentAmount, result.getPaymentAmount());
        assertNotNull(result.getNote());
    }

    @Test
    void testCreateDebtRecordRequest() {
        double debtAmount = 100.0;
        LocalDateTime recordDate = LocalDateTime.now();
        saleInvoice.setCreatedAt(recordDate);

        DebtRecordRequest result = saleInvoiceService.createDebtRecordRequest(saleInvoice, debtAmount);

        assertNotNull(result);
        assertEquals(customer.getId(), result.getCustomerId());
        assertEquals(recordDate, result.getRecordDate());
        assertEquals(debtAmount, result.getDebtAmount());
        assertNotNull(result.getNote());
    }

    @Test
    void testFormatDateTime() {
        String dateTimeStr = "2023-04-24T12:34:56";
        String expected = "ngày 24 tháng 04 năm 2023 giờ 12:34:56";

        String result = SaleInvoiceServiceImpl.formatDateTime(dateTimeStr);

        assertEquals(expected, result);
    }
}