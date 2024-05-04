package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.be.dto.ImportProductDetailRequest;
import vn.edu.fpt.be.dto.ImportProductInvoiceResponse;
import vn.edu.fpt.be.dto.request.DebtPaymentRequest;
import vn.edu.fpt.be.dto.request.DebtRecordRequest;
import vn.edu.fpt.be.dto.request.PaymentRecordRequest;
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;
import vn.edu.fpt.be.dto.response.ImportInvoiceReportResponse;
import vn.edu.fpt.be.dto.response.SupplierImportInvoiceResponse;
import vn.edu.fpt.be.exception.CustomServiceException;
import vn.edu.fpt.be.model.*;
import vn.edu.fpt.be.model.enums.RecordType;
import vn.edu.fpt.be.model.enums.SourceType;
import vn.edu.fpt.be.repository.CustomerRepository;
import vn.edu.fpt.be.repository.ImportProductInvoiceRepository;
import vn.edu.fpt.be.repository.SupplierRepository;
import vn.edu.fpt.be.service.DebtPaymentHistoryService;
import vn.edu.fpt.be.service.ImportProductInvoiceDetailService;
import vn.edu.fpt.be.service.ImportProductInvoiceService;
import vn.edu.fpt.be.service.UserService;

import javax.sound.midi.MidiFileFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportProductInvoiceServiceImpl implements ImportProductInvoiceService {
    private final ImportProductInvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ImportProductInvoiceDetailService detailService;
    private final UserService userService;
    private final DebtPaymentHistoryService debtPaymentHistoryService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    @Transactional
    public ImportProductInvoiceResponse importProduct(Long customerId, List<ImportProductDetailRequest> listProductDetails, Double pricePaid) {
        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

            User currentUser = userService.getCurrentUser();
            Store currentStore = currentUser.getStore();
            if (currentStore == null) {
                throw new IllegalArgumentException("Current user does not have an associated store.");
            }

            ImportProductInvoice invoice = new ImportProductInvoice();
            invoice.setCreatedBy(currentUser.getUsername());
            invoice.setCustomer(customer);
            invoice.setPricePaid(pricePaid);
            invoice.setStore(currentStore);
            double oldDebt = customer.getTotalDebt();
            if (oldDebt >= 0) {
                invoice.setOldDebt(0.0);
            } else {
                invoice.setOldDebt(oldDebt);
            }

            double totalInvoicePrice = calculateTotalInvoicePrice(listProductDetails);
            invoice.setTotalInvoicePrice(totalInvoicePrice);

            double totalPayment;
            double debtAmount = 0.0;
            double paymentAmount = 0.0;

            if (customer.getTotalDebt() == null || customer.getTotalDebt() >= 0) {
                totalPayment = totalInvoicePrice;
                if (pricePaid > totalPayment) {
                    throw new RuntimeException("Price paid can not greater than total payment");
                }
                if (pricePaid < totalPayment) {
                    debtAmount = totalPayment - pricePaid;
                }
            } else {
                totalPayment = totalInvoicePrice - customer.getTotalDebt();
                if (pricePaid > totalPayment) {
                    throw new RuntimeException("Price paid can not greater than total payment");
                }
                if (pricePaid > totalInvoicePrice) {
                    paymentAmount = pricePaid - totalInvoicePrice;
                }
                if (pricePaid < totalInvoicePrice) {
                    debtAmount = totalInvoicePrice - pricePaid;
                }
            }
            invoice.setTotalPayment(totalPayment);

            double newDebt = 0.0;
            if (debtAmount == 0.0 && paymentAmount == 0.0) {
                newDebt = oldDebt;
            }
            if (debtAmount != 0.0) {
                newDebt = oldDebt - debtAmount;
            }
            if (paymentAmount != 0.0) {
                newDebt = oldDebt + paymentAmount;
            }
            invoice.setNewDebt(newDebt);

            if (debtAmount == 0.0 && paymentAmount == 0.0) {
                ImportProductInvoice savedInvoice = invoiceRepository.save(invoice);
                saveNewDebtForCustomer(customer, newDebt);
                detailService.saveImportProductInvoiceDetail(listProductDetails, savedInvoice);
            }

            if (debtAmount != 0.0) {
                ImportProductInvoice savedInvoice = invoiceRepository.save(invoice);
                detailService.saveImportProductInvoiceDetail(listProductDetails, savedInvoice);
                DebtPaymentRequest debtPaymentRequest = createDebtPaymentRequest(savedInvoice, RecordType.OWNER_DEBT, SourceType.IMPORT_INVOICE, debtAmount);
                debtPaymentHistoryService.saveDebtPaymentHistory(debtPaymentRequest);
                updateDebtForCustomer(customer, RecordType.OWNER_DEBT, debtAmount);
            }

            if (paymentAmount != 0.0) {
                ImportProductInvoice savedInvoice = invoiceRepository.save(invoice);
                detailService.saveImportProductInvoiceDetail(listProductDetails, savedInvoice);
                DebtPaymentRequest debtPaymentRequest = createDebtPaymentRequest(savedInvoice, RecordType.OWNER_PAID, SourceType.IMPORT_INVOICE, paymentAmount);
                debtPaymentHistoryService.saveDebtPaymentHistory(debtPaymentRequest);
                updateDebtForCustomer(customer, RecordType.OWNER_PAID, paymentAmount);
            }

            return modelMapper.map(invoice, ImportProductInvoiceResponse.class);
        } catch (ArithmeticException e) {
            // Handle arithmetic issues
            throw new CustomServiceException("Arithmetic error: " + e.getMessage(), e);
        } catch (Exception e) {
            // Handle unexpected exceptions
            throw new CustomServiceException("An unexpected error occurred: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierImportInvoiceResponse> getImportInvoiceByCustomer(Long supplierId) {
        try {
            User currentUser = userService.getCurrentUser();

            List<ImportProductInvoice> importInvoices = invoiceRepository.findByCustomerIdAndStoreIdOrderByCreatedAtDesc(supplierId, currentUser.getStore().getId());

            return importInvoices.stream().map(importInvoice -> SupplierImportInvoiceResponse.builder()
                            .id(importInvoice.getId())
                            .createdAt(importInvoice.getCreatedAt())
                            .totalPrice(importInvoice.getTotalInvoicePrice())
                            .oldDebt(importInvoice.getOldDebt())
                            .totalPayment(importInvoice.getTotalPayment())
                            .pricePaid(importInvoice.getPricePaid())
                            .newDebt(importInvoice.getNewDebt())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving import invoices for supplier: " + supplierId, e);
        }
    }

    @Override
    public List<ImportInvoiceReportResponse> getImportInvoicesByFilter(LocalDateTime startDate, LocalDateTime endDate, String createdBy, Long supplierId) {
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
            List<ImportProductInvoice> invoices = invoiceRepository.findInvoicesByCriteria(startDate, endDate, createdBy, supplierId, currentStore.getId());
            return invoices.stream().map(invoice -> ImportInvoiceReportResponse.builder()
                            .id(invoice.getId())
                            .createdAt(invoice.getCreatedAt())
                            .createdBy(invoice.getCreatedBy())
                            .totalPrice(invoice.getTotalInvoicePrice())
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
    public ImportProductInvoiceResponse getImportInvoiceById(Long importInvoiceId) {
        User currentUser = userService.getCurrentUser();
        Optional<ImportProductInvoice> invoice = invoiceRepository.findById(importInvoiceId);
        if (invoice.isEmpty()) {
            throw new RuntimeException("Not found any import invoice with id " + importInvoiceId);
        }
        if (!invoice.get().getStore().equals(currentUser.getStore())) {
            throw new RuntimeException("this import invoice not belong to current store");
        }

        return modelMapper.map(invoice.get(), ImportProductInvoiceResponse.class);
    }

    private double calculateTotalInvoicePrice(List<ImportProductDetailRequest> details) {
        double totalInvoicePrice = 0.0;
        for (ImportProductDetailRequest detail : details) {
            totalInvoicePrice += (detail.getImportPrice() * detail.getQuantity());
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
    private void updateDebtForCustomer(Customer customer, RecordType recordType, double amount) {
        try {
            double oldDebt = customer.getTotalDebt();
            if (recordType == RecordType.OWNER_DEBT) {
                customer.setTotalDebt(oldDebt - amount);
            } else if (recordType == RecordType.OWNER_PAID) {
                customer.setTotalDebt(oldDebt + amount);
            }
            customerRepository.save(customer);
        } catch (DataAccessException e) {
            throw new CustomServiceException("Fail to update customer: " + e.getMessage(), e);
        }
    }
    public DebtPaymentRequest createDebtPaymentRequest(ImportProductInvoice invoice, RecordType recordType, SourceType sourceType, double amount) {
        DebtPaymentRequest debtPaymentRequest = new DebtPaymentRequest();
        debtPaymentRequest.setCustomerId(invoice.getCustomer().getId());
        debtPaymentRequest.setType(recordType);
        debtPaymentRequest.setAmount(amount);
        debtPaymentRequest.setSourceId(invoice.getId());
        debtPaymentRequest.setSourceType(sourceType);
        debtPaymentRequest.setRecordDate(invoice.getCreatedAt().plusHours(7));
        if (recordType == RecordType.OWNER_DEBT) {
            debtPaymentRequest.setNote("Khoản vay được tạo từ hóa đơn nhập hàng mã " + invoice.getId() + " vào " + formatDateTime(String.valueOf(invoice.getCreatedAt().plusHours(7))));
        } else if (recordType == RecordType.OWNER_PAID) {
            debtPaymentRequest.setNote("Khoản trả được tạo từ hóa đơn nhập hàng mã " + invoice.getId() + " vào " + formatDateTime(String.valueOf(invoice.getCreatedAt().plusHours(7))));
        }
        return debtPaymentRequest;
    }

    public static String formatDateTime(String dateTimeStr) {
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'ngày' dd 'tháng' MM 'năm' yyyy 'giờ' HH:mm:ss");
        return dateTime.format(formatter);
    }
}
