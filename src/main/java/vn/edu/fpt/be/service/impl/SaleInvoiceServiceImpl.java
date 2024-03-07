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
import vn.edu.fpt.be.exception.CustomServiceException;
import vn.edu.fpt.be.exception.EntityNotFoundException;
import vn.edu.fpt.be.model.*;
import vn.edu.fpt.be.repository.*;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.SaleInvoiceDetailService;
import vn.edu.fpt.be.service.SaleInvoiceService;
import vn.edu.fpt.be.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SaleInvoiceServiceImpl implements SaleInvoiceService {
    private final SaleInvoiceRepository saleInvoiceRepository;
    private final CustomerRepository customerRepository;
    private final SaleInvoiceDetailRepository saleInvoiceDetailRepository;
    private final ProductRepository productRepository;
    private final SaleInvoiceDetailService saleInvoiceDetailService;
    private final UserRepository userRepository;
    private UserService userService;
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
}
