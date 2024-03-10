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
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;
import vn.edu.fpt.be.dto.response.SaleInvoiceReportResponse;
import vn.edu.fpt.be.exception.CustomServiceException;
import vn.edu.fpt.be.exception.EntityNotFoundException;
import vn.edu.fpt.be.model.*;
import vn.edu.fpt.be.repository.*;
import vn.edu.fpt.be.service.SaleInvoiceDetailService;
import vn.edu.fpt.be.service.SaleInvoiceService;
import vn.edu.fpt.be.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleInvoiceServiceImpl implements SaleInvoiceService {
    private final SaleInvoiceRepository saleInvoiceRepository;
    private final CustomerRepository customerRepository;
    private final SaleInvoiceDetailService saleInvoiceDetailService;
    private final UserRepository userRepository;
    private final UserService userService;
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
        initSaleInvoice.setOldDebt(customer.getTotalDebt());
        initSaleInvoice.setCreatedBy(currentUser.getUsername());
        double totalPrice = calculateTotalInvoicePrice(requests);
        initSaleInvoice.setTotalPrice(totalPrice);

        double totalPayment;
        if (customer.getTotalDebt() == null) {
            totalPayment = totalPrice;
        } else {
            totalPayment = totalPrice + customer.getTotalDebt();
        }
        initSaleInvoice.setTotalPayment(totalPayment);

        double newDebt = totalPayment - pricePaid;
        initSaleInvoice.setNewDebt(newDebt);

        SaleInvoice savedSaleInvoice = saleInvoiceRepository.save(initSaleInvoice);
        saveNewDebtForCustomer(customer, newDebt);
        saleInvoiceDetailService.saveSaleInvoiceDetail(requests, savedSaleInvoice);

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
            // Handle the exception based on your application's requirement
            // For example, log the error and throw a custom exception or return an error response
            // Log the error (using a logging framework like SLF4J)
            // Logger.error("Error retrieving sale invoices for customer: {}", customerId, e);
            throw new RuntimeException("Error retrieving sale invoices for customer: " + customerId, e);
        }
    }

    @Override
    public List<SaleInvoiceReportResponse> getSaleInvoicesByFilter(LocalDateTime startDate, LocalDateTime endDate, String createdBy, Long customerId) {
        try {
            User currentUser = userService.getCurrentUser();
            Store currentStore = currentUser.getStore();
            if (startDate == null) {
                // Trả về danh sách rỗng hoặc ném lỗi nếu startDate không được cung cấp
                throw new IllegalArgumentException("Start date must be provided");
            }
            if (endDate == null) {
                endDate = LocalDateTime.now();
            }
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
}
