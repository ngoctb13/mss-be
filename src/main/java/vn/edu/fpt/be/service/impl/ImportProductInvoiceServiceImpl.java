package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.be.dto.ImportProductDetailRequest;
import vn.edu.fpt.be.dto.ImportProductInvoiceResponse;
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;
import vn.edu.fpt.be.dto.response.SupplierImportInvoiceResponse;
import vn.edu.fpt.be.exception.CustomServiceException;
import vn.edu.fpt.be.model.*;
import vn.edu.fpt.be.repository.ImportProductInvoiceRepository;
import vn.edu.fpt.be.repository.SupplierRepository;
import vn.edu.fpt.be.service.ImportProductInvoiceDetailService;
import vn.edu.fpt.be.service.ImportProductInvoiceService;
import vn.edu.fpt.be.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportProductInvoiceServiceImpl implements ImportProductInvoiceService {
    private final ImportProductInvoiceRepository invoiceRepository;
    private final SupplierRepository supplierRepository;
    private final ImportProductInvoiceDetailService detailService;
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    @Transactional
    public ImportProductInvoiceResponse importProduct(Long supplierId, List<ImportProductDetailRequest> listProductDetails, Double pricePaid) {
        try {
            Supplier supplier = supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found with ID: " + supplierId));

            User currentUser = userService.getCurrentUser();
            Store currentStore = currentUser.getStore();
            if (currentStore == null) {
                throw new IllegalArgumentException("Current user does not have an associated store.");
            }

            ImportProductInvoice invoice = new ImportProductInvoice();
            invoice.setCreatedBy(currentUser.getUsername());
            invoice.setSupplier(supplier);
            invoice.setPricePaid(pricePaid);
            invoice.setStore(currentStore);
            invoice.setOldDebt(supplier.getTotalDebt());

            double totalInvoicePrice = calculateTotalInvoicePrice(listProductDetails);
            invoice.setTotalInvoicePrice(totalInvoicePrice);
            double totalPayment;
            if (supplier.getTotalDebt() == null) {
                totalPayment = totalInvoicePrice;
            } else {
                totalPayment = totalInvoicePrice + supplier.getTotalDebt();
            }
            invoice.setTotalPayment(totalPayment);

            double newDebt = totalPayment - pricePaid;
            invoice.setNewDebt(newDebt);

            ImportProductInvoice savedInvoice = invoiceRepository.save(invoice);

            saveNewDebtForSupplier(supplier, savedInvoice.getNewDebt());
            detailService.saveImportProductInvoiceDetail(listProductDetails, savedInvoice);

            return modelMapper.map(savedInvoice, ImportProductInvoiceResponse.class);
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
    public List<SupplierImportInvoiceResponse> getImportInvoiceBySupplier(Long supplierId) {
        try {
            User currentUser = userService.getCurrentUser();

            List<ImportProductInvoice> importInvoices = invoiceRepository.findBySupplierIdAndStoreIdOrderByCreatedAtDesc(supplierId, currentUser.getStore().getId());

            // Convert SaleInvoice entities to CustomerSaleInvoiceResponse DTOs
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
            // Handle the exception based on your application's requirement
            // For example, log the error and throw a custom exception or return an error response
            // Log the error (using a logging framework like SLF4J)
            // Logger.error("Error retrieving sale invoices for customer: {}", customerId, e);
            throw new RuntimeException("Error retrieving import invoices for supplier: " + supplierId, e);
        }
    }

    private double calculateTotalInvoicePrice(List<ImportProductDetailRequest> details) {
        double totalInvoicePrice = 0.0;
        for (ImportProductDetailRequest detail : details) {
            totalInvoicePrice += (detail.getImportPrice() * detail.getQuantity());
        }
        return totalInvoicePrice;
    }

    private void saveNewDebtForSupplier(Supplier supplier, double newDebt) {
        try {
            supplier.setTotalDebt(newDebt);
            supplierRepository.save(supplier);
        } catch (DataAccessException e) {
            throw new CustomServiceException("Fail to update supplier: " + e.getMessage(), e);
        }
    }
}
