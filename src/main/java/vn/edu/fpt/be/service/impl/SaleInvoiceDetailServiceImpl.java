package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.SaleInvoiceDetailDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailRequest;
import vn.edu.fpt.be.model.Product;
import vn.edu.fpt.be.model.SaleInvoiceDetail;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.repository.ProductRepository;
import vn.edu.fpt.be.repository.SaleInvoiceDetailRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.SaleInvoiceDetailService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SaleInvoiceDetailServiceImpl implements SaleInvoiceDetailService {
    private final SaleInvoiceDetailRepository saleInvoiceDetailRepository;
    private final ProductRepository productRepository;
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
    public List<SaleInvoiceDetailDTO> createSaleInvoiceDetail(List<SaleInvoiceDetailRequest> requests) {
        List<SaleInvoiceDetailDTO> createdDetails = new ArrayList<>();

        for (SaleInvoiceDetailRequest request : requests) {
            SaleInvoiceDetailDTO createdDetail = createSingleSaleInvoiceDetail(request);
            createdDetails.add(createdDetail);
        }
        return createdDetails;
    }

    private SaleInvoiceDetailDTO createSingleSaleInvoiceDetail(SaleInvoiceDetailRequest request) {
        User currentUser = getCurrentUser();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + request.getProductId()));

        SaleInvoiceDetail saleInvoiceDetail = new SaleInvoiceDetail();
        saleInvoiceDetail.setProduct(product);
        saleInvoiceDetail.setQuantity(request.getQuantity());
        saleInvoiceDetail.setUnitPrice(request.getUnitPrice());
        saleInvoiceDetail.setTotalPrice(request.getQuantity() * request.getUnitPrice());
        saleInvoiceDetail.setCreatedBy(currentUser.getUsername());

        SaleInvoiceDetail savedDetail = saleInvoiceDetailRepository.save(saleInvoiceDetail);

        return modelMapper.map(savedDetail, SaleInvoiceDetailDTO.class);
    }
}
