package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.be.dto.ImportProductDetailRequest;
import vn.edu.fpt.be.dto.SaleInvoiceDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailRequest;
import vn.edu.fpt.be.dto.request.DebtPaymentRequest;
import vn.edu.fpt.be.dto.request.DebtRecordRequest;
import vn.edu.fpt.be.dto.request.PaymentRecordRequest;
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;
import vn.edu.fpt.be.dto.response.SaleInvoiceReportResponse;
import vn.edu.fpt.be.exception.CustomServiceException;
import vn.edu.fpt.be.exception.EntityNotFoundException;
import vn.edu.fpt.be.model.*;
import vn.edu.fpt.be.model.enums.RecordType;
import vn.edu.fpt.be.model.enums.SourceType;
import vn.edu.fpt.be.repository.*;
import vn.edu.fpt.be.service.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleInvoiceServiceImpl implements SaleInvoiceService {
    private final SaleInvoiceRepository saleInvoiceRepository;
    private final CustomerRepository customerRepository;
    private final SaleInvoiceDetailService saleInvoiceDetailService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final DebtPaymentHistoryService debtPaymentHistoryService;
    private final PaymentRecordService paymentRecordService;
    private final DebtRecordService debtRecordService;
    private final ModelMapper modelMapper = new ModelMapper();


    @Override
    @Transactional
    public SaleInvoiceDTO createSaleInvoice(Long customerId, List<SaleInvoiceDetailRequest> requests, double pricePaid) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + customerId));
        User currentUser = userService.getCurrentUser();
        Store currentStore = currentUser.getStore();
        if (currentStore == null) {
            throw new IllegalArgumentException("Current user does not have an associated store.");
        }
        //
        SaleInvoice initSaleInvoice = new SaleInvoice();
        initSaleInvoice.setCustomer(customer);
        initSaleInvoice.setStore(currentStore);
        initSaleInvoice.setPricePaid(pricePaid);
        double oldDebt = customer.getTotalDebt();
        if (oldDebt <= 0) {
            initSaleInvoice.setOldDebt(0.0);
        } else {
            initSaleInvoice.setOldDebt(oldDebt);
        }
        initSaleInvoice.setCreatedBy(currentUser.getUsername());


        double totalPrice = calculateTotalInvoicePrice(requests);
        initSaleInvoice.setTotalPrice(totalPrice);

        double totalPayment;
        double debtAmount = 0.0;
        double paymentAmount = 0.0;
        if (customer.getTotalDebt() == null || customer.getTotalDebt() <= 0) {
            totalPayment = totalPrice;
            if (pricePaid > totalPayment) {
                throw new RuntimeException("Price paid can not greater than total payment");
            }
            if (pricePaid < totalPayment) {
                debtAmount = totalPayment - pricePaid;
            }
        } else {
            totalPayment = totalPrice + customer.getTotalDebt();
            if (pricePaid > totalPayment) {
                throw new RuntimeException("Price paid can not greater than total payment");
            }
            if (pricePaid > totalPrice) {
                paymentAmount = pricePaid - totalPrice;
            }
            if (pricePaid < totalPrice) {
                debtAmount = totalPrice - pricePaid;
            }
        }
        initSaleInvoice.setTotalPayment(totalPayment);

        double newDebt = 0.0;
        if (debtAmount == 0.0 && paymentAmount == 0.0) {
            newDebt = oldDebt;
        }
        if (debtAmount != 0.0) {
            newDebt = oldDebt + debtAmount;
        }
        if (paymentAmount != 0.0) {
            newDebt = oldDebt - paymentAmount;
        }
        initSaleInvoice.setNewDebt(newDebt);

        if (debtAmount == 0.0 && paymentAmount == 0.0) {
            SaleInvoice savedSaleInvoice = saleInvoiceRepository.save(initSaleInvoice);
            saveNewDebtForCustomer(customer, newDebt);
            saleInvoiceDetailService.saveSaleInvoiceDetail(requests, savedSaleInvoice);
        }

        if (debtAmount != 0.0) {
            SaleInvoice savedSaleInvoice = saleInvoiceRepository.save(initSaleInvoice);
//            saveNewDebtForCustomer(customer, newDebt);
            saleInvoiceDetailService.saveSaleInvoiceDetail(requests, savedSaleInvoice);
            DebtRecordRequest debtRecordRequest = createDebtRecordRequest(savedSaleInvoice, debtAmount);
            debtRecordService.createDebtRecord(debtRecordRequest, SourceType.SALE_INVOICE, savedSaleInvoice.getId());
        }

        if (paymentAmount != 0.0) {
            SaleInvoice savedSaleInvoice = saleInvoiceRepository.save(initSaleInvoice);
//            saveNewDebtForCustomer(customer, newDebt);
            saleInvoiceDetailService.saveSaleInvoiceDetail(requests, savedSaleInvoice);
            PaymentRecordRequest paymentRecordRequest = createPaymentRecordRequest(savedSaleInvoice, paymentAmount);
            paymentRecordService.createPaymentRecord(paymentRecordRequest, SourceType.SALE_INVOICE, savedSaleInvoice.getId());
        }

        return modelMapper.map(initSaleInvoice, SaleInvoiceDTO.class);
    }

    private double calculateTotalInvoicePrice(List<SaleInvoiceDetailRequest> details) {
        double totalInvoicePrice = 0.0;
        for (SaleInvoiceDetailRequest detail : details) {
            totalInvoicePrice += (detail.getUnitPrice() * detail.getQuantity());
        }
        return totalInvoicePrice;
    }

    private void saveNewDebtForCustomer(Customer customer, double newDebt) {
        try {
            customer.setTotalDebt(newDebt);
            customerRepository.save(customer);
        } catch (DataAccessException e) {
            throw new CustomServiceException("Fail to update customer: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerSaleInvoiceResponse> getSaleInvoiceByCustomer(Long customerId) {
        try {
            User currentUser = userService.getCurrentUser();

            List<SaleInvoice> saleInvoices = saleInvoiceRepository.findByCustomerIdAndStoreIdOrderByCreatedAtDesc(customerId, currentUser.getStore().getId());

            // Convert SaleInvoice entities to CustomerSaleInvoiceResponse DTOs
            return saleInvoices.stream().map(saleInvoice -> CustomerSaleInvoiceResponse.builder()
                            .id(saleInvoice.getId())
                            .createdAt(saleInvoice.getCreatedAt())
                            .totalPrice(saleInvoice.getTotalPrice())
                            .oldDebt(saleInvoice.getOldDebt())
                            .totalPayment(saleInvoice.getTotalPayment())
                            .pricePaid(saleInvoice.getPricePaid())
                            .newDebt(saleInvoice.getNewDebt())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving sale invoices for customer: " + customerId, e);
        }
    }

    @Override
    public List<SaleInvoiceReportResponse> getSaleInvoicesByFilter(LocalDateTime startDate, LocalDateTime endDate, String createdBy, Long customerId) {
        try {
            User currentUser = userService.getCurrentUser();
            Store currentStore = currentUser.getStore();
            List<SaleInvoice> invoices = saleInvoiceRepository.findInvoicesByCriteria(startDate, endDate, createdBy, customerId, currentStore.getId());
            return invoices.stream().map(invoice -> SaleInvoiceReportResponse.builder()
                            .id(invoice.getId())
                            .createdAt(invoice.getCreatedAt())
                            .createdBy(invoice.getCreatedBy())
                            .totalPrice(invoice.getTotalPrice())
                            .oldDebt(invoice.getOldDebt())
                            .totalPayment(invoice.getTotalPayment())
                            .pricePaid(invoice.getPricePaid())
                            .newDebt(invoice.getNewDebt())
                            .customer(invoice.getCustomer())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomServiceException("Error accessing the database", e);
        }
    }

    @Override
    public SaleInvoiceDTO getSaleInvoiceById(Long saleInvoiceId) {
        User currentUser = userService.getCurrentUser();
        Optional<SaleInvoice> saleInvoice = saleInvoiceRepository.findById(saleInvoiceId);
        if (saleInvoice.isEmpty()) {
            throw new RuntimeException("Not found any sale invoice with id " + saleInvoiceId);
        }
        if (!saleInvoice.get().getStore().equals(currentUser.getStore())) {
            throw new RuntimeException("this sale invocie not belong to current store");
        }

        return modelMapper.map(saleInvoice.get(), SaleInvoiceDTO.class);
    }

    @Override
    public List<SaleInvoiceReportResponse> getRecentInvoicesByStoreId() {
        User currentUser = userService.getCurrentUser();
        Long storeId = currentUser.getStore().getId();
        if (storeId == null) {
            throw new RuntimeException("Store can not be null");
        }
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        List<SaleInvoice> invoices = saleInvoiceRepository.findRecentInvoicesByStoreId(storeId, startDate);
        return invoices.stream().map(invoice -> SaleInvoiceReportResponse.builder()
                        .id(invoice.getId())
                        .createdAt(invoice.getCreatedAt())
                        .createdBy(invoice.getCreatedBy())
                        .totalPrice(invoice.getTotalPrice())
                        .oldDebt(invoice.getOldDebt())
                        .totalPayment(invoice.getTotalPayment())
                        .pricePaid(invoice.getPricePaid())
                        .newDebt(invoice.getNewDebt())
                        .customer(invoice.getCustomer())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<SaleInvoiceReportResponse> getAllSaleInvoiceByCurrentStore() {
        User currentUser = userService.getCurrentUser();
        Long storeId = currentUser.getStore().getId();
        if (storeId == null) {
            throw new RuntimeException("Store can not be null");
        }

        List<SaleInvoice> invoices = saleInvoiceRepository.findAllByStoreIdOrderByCreatedAtAsc(storeId);
        return invoices.stream().map(invoice -> SaleInvoiceReportResponse.builder()
                        .id(invoice.getId())
                        .createdAt(invoice.getCreatedAt())
                        .createdBy(invoice.getCreatedBy())
                        .totalPrice(invoice.getTotalPrice())
                        .oldDebt(invoice.getOldDebt())
                        .totalPayment(invoice.getTotalPayment())
                        .pricePaid(invoice.getPricePaid())
                        .newDebt(invoice.getNewDebt())
                        .customer(invoice.getCustomer())
                        .build())
                .collect(Collectors.toList());
    }

    public PaymentRecordRequest createPaymentRecordRequest(SaleInvoice saleInvoice, double amount) {
        PaymentRecordRequest paymentRecordRequest = new PaymentRecordRequest();
        paymentRecordRequest.setCustomerId(saleInvoice.getCustomer().getId());
        paymentRecordRequest.setRecordDate(saleInvoice.getCreatedAt().plusHours(7));
        paymentRecordRequest.setPaymentAmount(amount);
        paymentRecordRequest.setNote("Khoản thanh toán từ hóa đơn " + saleInvoice.getId() + " vào " + formatDateTime(String.valueOf(saleInvoice.getCreatedAt().plusHours(7))));
        return paymentRecordRequest;
    }

    public DebtRecordRequest createDebtRecordRequest(SaleInvoice saleInvoice, double amount) {
        DebtRecordRequest debtRecordRequest = new DebtRecordRequest();
        debtRecordRequest.setCustomerId(saleInvoice.getCustomer().getId());
        debtRecordRequest.setRecordDate(saleInvoice.getCreatedAt().plusHours(7));
        debtRecordRequest.setDebtAmount(amount);
        debtRecordRequest.setNote("Khoản nợ từ hóa đơn " + saleInvoice.getId() + " vào " + formatDateTime(String.valueOf(saleInvoice.getCreatedAt().plusHours(7))));
        return debtRecordRequest;
    }

    public static String formatDateTime(String dateTimeStr) {
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'ngày' dd 'tháng' MM 'năm' yyyy 'giờ' HH:mm:ss");
        return dateTime.format(formatter);
    }
}
