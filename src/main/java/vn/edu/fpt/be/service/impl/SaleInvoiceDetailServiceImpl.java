package vn.edu.fpt.be.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.ImportProductDetailResponse;
import vn.edu.fpt.be.dto.SaleInvoiceDetailDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailRequest;
import vn.edu.fpt.be.exception.CustomServiceException;
import vn.edu.fpt.be.exception.EntityNotFoundException;
import vn.edu.fpt.be.model.*;
import vn.edu.fpt.be.model.jsonDetail.ProductDetailJson;
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
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public void saveSaleInvoiceDetail(List<SaleInvoiceDetailRequest> request, SaleInvoice invoice) {

    }

    private SaleInvoiceDetailRequest saveSingleSaleInvoiceDetailRequest(SaleInvoiceDetailRequest saleInvoiceDetailRequest, SaleInvoice saleInvoice) {
        try {
            User currentUser = userService.getCurrentUser();
            Product product = productRepository.findById(saleInvoiceDetailRequest.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + saleInvoiceDetailRequest.getProductId()));
            SaleInvoiceDetail saleInvoiceDetail = new SaleInvoiceDetail();
            saleInvoiceDetail.setCreatedBy(currentUser.getUsername());
            saleInvoiceDetail.setProductDetailsAtTimeOfImport(convertToJson(product));
            saleInvoiceDetail.setProduct(product);
            saleInvoiceDetail.setQuantity(saleInvoiceDetailRequest.getQuantity());
            saleInvoiceDetail.setUnitPrice(saleInvoiceDetailRequest.getUnitPrice());
            saleInvoiceDetail.setTotalPrice(saleInvoiceDetailRequest.getQuantity() * saleInvoiceDetailRequest.getUnitPrice());
            saleInvoiceDetail.setSaleInvoice(saleInvoice);
            SaleInvoiceDetail saveSaleInvoiceDetail = saleInvoiceDetailRepository.save(saleInvoiceDetail);
            updateProductInventory(product, saleInvoiceDetail.getQuantity());
            return modelMapper.map(saveSaleInvoiceDetail, SaleInvoiceDetailRequest.class);
        } catch (EntityNotFoundException e) {
            throw new CustomServiceException(e.getMessage(), e);
        } catch (Exception e) {
            throw new CustomServiceException("An error occurred while saving the invoice detail: " + e.getMessage(), e);
        }
    }

    private void updateProductInventory(Product product, double quantity) {
        double currentInventory = product.getInventory() == null ? 0 : product.getInventory();

        product.setInventory(currentInventory - quantity);

        productRepository.save(product);
    }

    private String convertToJson(Product product) {
        ProductDetailJson productDetailJson = modelMapper.map(product, ProductDetailJson.class);
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(productDetailJson);
            return jsonString;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
