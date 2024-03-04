package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.*;
import vn.edu.fpt.be.model.*;
import vn.edu.fpt.be.repository.*;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.SaleInvoiceDetailService;
import vn.edu.fpt.be.service.SupplierDebtDetailService;
import vn.edu.fpt.be.service.SupplierInvoiceService;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class SupplierInvoiceServiceImpl implements SupplierInvoiceService {
    private final SupplierRepository supplierRepository;
    private final SupplierDebtInvoiceRepository supplierDebtInvoiceRepository;
    private final SupplierDebtDetailRepository supplierDebtDetailRepository;
    private final UserRepository userRepository;
    private final SupplierDebtDetailService supplierDebtDetailService;
    private final ProductRepository productRepository;
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
    public SupplierDebtInvoiceDTO createSaleInvoice(Long supplierId, List<SupplierDebtDetailRequest> requests, double pricePaid) {
        User currentUser = getCurrentUser();
        //
        Optional<Supplier> supplier = supplierRepository.findById(supplierId);
        if (supplier.isEmpty()) {
            throw new RuntimeException("Customer not found.");
        }
        SupplierDebtInvoice initSupplierDebtInvoice = new SupplierDebtInvoice();
        initSupplierDebtInvoice.setSupplier(supplier.get());
        initSupplierDebtInvoice.setCreatedBy(currentUser.getUsername());
        List<SupplierDebtDetailDTO> invoiceDetailDTOList= supplierDebtDetailService.createDebtDetail(requests);
        double totalPrice= 0;
        for (SupplierDebtDetailDTO supplierDebtDetailDTO : invoiceDetailDTOList){
            totalPrice= totalPrice+ supplierDebtDetailDTO.getTotalPrice();
        }
        initSupplierDebtInvoice.setTotalPrice(totalPrice);
        initSupplierDebtInvoice.setOldDebt(supplier.get().getTotalDebt());
        initSupplierDebtInvoice.setTotalPayment(totalPrice+ supplier.get().getTotalDebt());
        initSupplierDebtInvoice.setPricePaid(pricePaid);
        initSupplierDebtInvoice.setNewDebt(totalPrice+ supplier.get().getTotalDebt()- pricePaid);
        SupplierDebtInvoice saveDebtInvoice = supplierDebtInvoiceRepository.save(initSupplierDebtInvoice);
        supplier.get().setTotalDebt(saveDebtInvoice.getNewDebt());
        for (SupplierDebtDetailDTO supplierDebtInvoiceDTO: invoiceDetailDTOList){
            SupplierDebtDetail supplierDebtDetail = modelMapper.map(supplierDebtInvoiceDTO, SupplierDebtDetail.class );
            supplierDebtDetail.setSupplierDebtInvoice(saveDebtInvoice);
            Product detailProduct = supplierDebtDetail.getProduct();
            detailProduct.setInventory(detailProduct.getInventory() - supplierDebtDetail.getQuantity());
            productRepository.save(detailProduct);
            supplierDebtDetailRepository.save(supplierDebtDetail);
        }
        return modelMapper.map(initSupplierDebtInvoice, SupplierDebtInvoiceDTO.class);
    }
}
