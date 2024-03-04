package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.SaleInvoiceDetailDTO;
import vn.edu.fpt.be.dto.SupplierDebtDetailDTO;
import vn.edu.fpt.be.dto.SupplierDebtDetailRequest;
import vn.edu.fpt.be.model.Product;
import vn.edu.fpt.be.model.SupplierDebtDetail;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.repository.ProductRepository;
import vn.edu.fpt.be.repository.SaleInvoiceDetailRepository;
import vn.edu.fpt.be.repository.SupplierDebtDetailRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.ProductService;
import vn.edu.fpt.be.service.SupplierDebtDetailService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class SupplierDebtDetailServiceImpl implements SupplierDebtDetailService {
    private final SupplierDebtDetailRepository supplierDebtDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
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
    public List<SupplierDebtDetailDTO> createDebtDetail(List<SupplierDebtDetailRequest> supplierDebtDetailRequests) {
        List<SupplierDebtDetailDTO> createDetails= new ArrayList<>();
        for (SupplierDebtDetailRequest supplierDebtDetailRequest: supplierDebtDetailRequests){
            SupplierDebtDetailDTO createDetail = creatSingleDebtDetail(supplierDebtDetailRequest);
            createDetails.add(createDetail);
        }
        return createDetails;
    }
    private SupplierDebtDetailDTO creatSingleDebtDetail(SupplierDebtDetailRequest supplierDebtDetailRequest){
        User currentUser = getCurrentUser();
        Product product = productRepository.findById(supplierDebtDetailRequest.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + supplierDebtDetailRequest.getProductId()));
        SupplierDebtDetail supplierDebtDetail = getSupplierDebtDetail(supplierDebtDetailRequest, product, currentUser);
        SupplierDebtDetail saveSupplierDebtDetail = supplierDebtDetailRepository.save(supplierDebtDetail);
        return modelMapper.map(saveSupplierDebtDetail, SupplierDebtDetailDTO.class);

    }

    private static SupplierDebtDetail getSupplierDebtDetail(SupplierDebtDetailRequest supplierDebtDetailRequest, Product product, User currentUser) {
        SupplierDebtDetail supplierDebtDetail = new SupplierDebtDetail();
        supplierDebtDetail.setProduct(product);
        supplierDebtDetail.setDistance(supplierDebtDetailRequest.getDistance());
        supplierDebtDetail.setQuantity(supplierDebtDetail.getQuantity());
        supplierDebtDetail.setTotalPrice(supplierDebtDetailRequest.getDistance() * supplierDebtDetail.getQuantity()* supplierDebtDetailRequest.getUnitPricePerDistance());
        supplierDebtDetail.setCreatedBy(currentUser.getUsername());
        return supplierDebtDetail;
    }
}
