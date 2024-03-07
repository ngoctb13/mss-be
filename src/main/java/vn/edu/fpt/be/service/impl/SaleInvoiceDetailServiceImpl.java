package vn.edu.fpt.be.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.ImportProductDetailResponse;
import vn.edu.fpt.be.dto.SaleInvoiceDetailDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailRequest;
import vn.edu.fpt.be.exception.CustomServiceException;
import vn.edu.fpt.be.model.Product;
import vn.edu.fpt.be.model.SaleInvoice;
import vn.edu.fpt.be.model.SaleInvoiceDetail;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.repository.ProductRepository;
import vn.edu.fpt.be.repository.SaleInvoiceDetailRepository;
import vn.edu.fpt.be.repository.SaleInvoiceRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.SaleInvoiceDetailService;
import vn.edu.fpt.be.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SaleInvoiceDetailServiceImpl implements SaleInvoiceDetailService {
    private final SaleInvoiceDetailRepository saleInvoiceDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();
    @Override
    public void saveSaleInvoiceDetail(List<SaleInvoiceDetailRequest> requests, SaleInvoice invoice) {
        for (SaleInvoiceDetailRequest request : requests) {
            SaleInvoiceDetailDTO createdDetail = saveSingleSaleInvoiceDetail(request, invoice);
        }
    }

    private SaleInvoiceDetailDTO saveSingleSaleInvoiceDetail(SaleInvoiceDetailRequest request, SaleInvoice invoice) {
        try {
            User currentUser = userService.getCurrentUser();
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + request.getProductId()));
            if (invoice == null) {
                throw new IllegalArgumentException("Invoice cannot be null");
            }
            // Check if the requested quantity is available
            if (request.getQuantity() > product.getInventory()) {
                throw new CustomServiceException("Requested quantity exceeds available stock for product ID: " + request.getProductId());
            }

            SaleInvoiceDetail saleInvoiceDetail = new SaleInvoiceDetail();
            saleInvoiceDetail.setCreatedBy(currentUser.getUsername());
            saleInvoiceDetail.setProduct(product);
            saleInvoiceDetail.setSaleInvoice(invoice);
            saleInvoiceDetail.setQuantity(request.getQuantity());
            saleInvoiceDetail.setUnitPrice(request.getUnitPrice());
            saleInvoiceDetail.setTotalPrice(request.getQuantity() * request.getUnitPrice());

            SaleInvoiceDetail savedDetail = saleInvoiceDetailRepository.save(saleInvoiceDetail);
            updateProductQuantity(product, savedDetail.getQuantity());

            return modelMapper.map(savedDetail, SaleInvoiceDetailDTO.class);
        } catch (Exception e) {
            throw new CustomServiceException("An error occurred while saving the invoice detail: " + e.getMessage(), e);
        }
    }

    private void updateProductQuantity(Product product, double quantity) {
        try {
            double updatedQuantity = product.getInventory() - quantity;
            if (updatedQuantity < 0) {
                throw new CustomServiceException("Insufficient stock after sale.");
            }
            product.setInventory(updatedQuantity);
            productRepository.save(product);
        } catch (DataAccessException e) {
            // Handle database access related exceptions
            throw new CustomServiceException("Error accessing database: " + e.getMessage(), e);
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            throw new RuntimeException("An unexpected error occurred while updating product quantity: " + e.getMessage(), e);
        }
    }
}
