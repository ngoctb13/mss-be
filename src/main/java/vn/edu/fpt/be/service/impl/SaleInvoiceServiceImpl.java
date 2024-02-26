package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.SaleInvoiceDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailRequest;
import vn.edu.fpt.be.model.*;
import vn.edu.fpt.be.repository.*;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.SaleInvoiceDetailService;
import vn.edu.fpt.be.service.SaleInvoiceService;

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
    private final ModelMapper modelMapper = new ModelMapper();

    public User getCurrentUser() {
        UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());
        if (currentUser.isEmpty()) {
            throw new RuntimeException("Authenticated user not found.");
        }
        return currentUser.get();
    }
    @Override
    public SaleInvoiceDTO createSaleInvoice(Long customerId, List<SaleInvoiceDetailRequest> requests, double pricePaid) {
        User currentUser = getCurrentUser();
        //
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isEmpty()) {
            throw new RuntimeException("Customer not found.");
        }
        //
        SaleInvoice initSaleInvoice = new SaleInvoice();
        initSaleInvoice.setCustomer(customer.get());
        initSaleInvoice.setCreatedBy(currentUser.getUsername());
        //
        List<SaleInvoiceDetailDTO> invoiceDetailDTOList = saleInvoiceDetailService.createSaleInvoiceDetail(requests);
        double totalPrice = 0;
        for (SaleInvoiceDetailDTO saleInvoiceDetailDTO : invoiceDetailDTOList) {
            totalPrice = totalPrice + saleInvoiceDetailDTO.getTotalPrice();
        }
        initSaleInvoice.setTotalPrice(totalPrice);
        initSaleInvoice.setOldDebt(customer.get().getTotalDebt());
        initSaleInvoice.setTotalPayment(totalPrice + customer.get().getTotalDebt());
        initSaleInvoice.setPricePaid(pricePaid);
        initSaleInvoice.setNewDebt(totalPrice + customer.get().getTotalDebt() - pricePaid);

        SaleInvoice savedSaleInvoice = saleInvoiceRepository.save(initSaleInvoice);
        customer.get().setTotalDebt(savedSaleInvoice.getNewDebt());

        for (SaleInvoiceDetailDTO saleInvoiceDetailDTO : invoiceDetailDTOList) {
            SaleInvoiceDetail saleInvoiceDetail = modelMapper.map(saleInvoiceDetailDTO, SaleInvoiceDetail.class);
            saleInvoiceDetail.setSaleInvoice(savedSaleInvoice);
            Product detailProduct = saleInvoiceDetail.getProduct();
            detailProduct.setInventory(detailProduct.getInventory() - saleInvoiceDetailDTO.getQuantity());
            productRepository.save(detailProduct);
            saleInvoiceDetailRepository.save(saleInvoiceDetail);
        }
        return modelMapper.map(initSaleInvoice, SaleInvoiceDTO.class);
    }
}
