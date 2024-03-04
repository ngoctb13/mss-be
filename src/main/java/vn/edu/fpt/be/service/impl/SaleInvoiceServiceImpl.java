package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.be.dto.*;
import vn.edu.fpt.be.exception.CustomServiceException;
import vn.edu.fpt.be.model.*;
import vn.edu.fpt.be.repository.*;
import vn.edu.fpt.be.service.SaleInvoiceDetailService;
import vn.edu.fpt.be.service.SaleInvoiceService;
import vn.edu.fpt.be.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleInvoiceServiceImpl implements SaleInvoiceService {
    private final SaleInvoiceRepository saleInvoiceRepository;
    private final CustomerRepository customerRepository;
    private final SaleInvoiceDetailService saleInvoiceDetailService;
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    @Transactional
    public SaleInvoiceResponse saveSaleInvoice(Long customerId, List<SaleInvoiceDetailRequest> requests, Double pricePaid) {
        try {
            Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Supplier not found with ID: " + customerId));
            User currentUser = userService.getCurrentUser();

            SaleInvoice saleInvoice = new SaleInvoice();
            saleInvoice.setCreatedBy(currentUser.getUsername());
            saleInvoice.setCustomer(customer);
            saleInvoice.setPricePaid(pricePaid);
            double totalInvoicePrice = calculateTotalInvoicePrice(requests);
            saleInvoice.setTotalPayment(totalInvoicePrice);
            double totalPayment;
            if (customer.getTotalDebt() == null) {
                totalPayment = totalInvoicePrice;
            } else {
                totalPayment = totalInvoicePrice + customer.getTotalDebt();
            }
            saleInvoice.setTotalPayment(totalPayment);
            double newDebt = totalPayment - pricePaid;
            saleInvoice.setNewDebt(newDebt);
            SaleInvoice saveInvoice= saleInvoiceRepository.save(saleInvoice);
            saveNewDebtForCustomer(customer, saveInvoice.getNewDebt());
            saleInvoiceDetailService.saveSaleInvoiceDetail(requests, saveInvoice);
            return modelMapper.map(saveInvoice, SaleInvoiceResponse.class);
        } catch (ArithmeticException e) {
            throw new CustomServiceException("Arithmetic error: " + e.getMessage(), e);
        } catch (Exception e) {
            // Handle unexpected exceptions
            throw new CustomServiceException("An unexpected error occurred: " + e.getMessage(), e);
        }
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
            throw new CustomServiceException("Fail to update supplier: " + e.getMessage(), e);
        }
    }
}
