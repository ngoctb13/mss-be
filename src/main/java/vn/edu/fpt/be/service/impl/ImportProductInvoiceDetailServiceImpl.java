package vn.edu.fpt.be.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.ImportProductDetailRequest;
import vn.edu.fpt.be.dto.ImportProductDetailResponse;
import vn.edu.fpt.be.dto.SaleInvoiceDetailDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailRequest;
import vn.edu.fpt.be.dto.response.ImportInvoiceDetailResponse;
import vn.edu.fpt.be.exception.CustomServiceException;
import vn.edu.fpt.be.exception.EntityNotFoundException;
import vn.edu.fpt.be.model.*;
import vn.edu.fpt.be.model.jsonDetail.ProductDetailJson;
import vn.edu.fpt.be.repository.ImportProductInvoiceDetailRepository;
import vn.edu.fpt.be.repository.ImportProductInvoiceRepository;
import vn.edu.fpt.be.repository.ProductRepository;
import vn.edu.fpt.be.repository.StorageLocationRepository;
import vn.edu.fpt.be.service.ImportProductInvoiceDetailService;
import vn.edu.fpt.be.service.UserService;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportProductInvoiceDetailServiceImpl implements ImportProductInvoiceDetailService {
    private final UserService userService;
    private final ImportProductInvoiceDetailRepository repo;
    private final ProductRepository productRepository;
    private final ImportProductInvoiceRepository importProductInvoiceRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ModelMapper modelMapper = new ModelMapper();
    @Override
    public void saveImportProductInvoiceDetail(List<ImportProductDetailRequest> requests, ImportProductInvoice invoice) {
        for (ImportProductDetailRequest request : requests) {
            ImportProductDetailResponse savedDetail = saveSingleProductInvoiceDetail(request, invoice);
        }
    }

    @Override
    public List<ImportInvoiceDetailResponse> getDetailsOfImportInvoice(Long importInvoiceId) {
        try {
            User currentUser = userService.getCurrentUser();
            Optional<ImportProductInvoice> importInvoice = importProductInvoiceRepository.findById(importInvoiceId);
            if (importInvoice.isEmpty()) {
                throw new IllegalArgumentException("import invoice cannot be null!");
            }
            if (!importInvoice.get().getStore().equals(currentUser.getStore())) {
                throw new IllegalArgumentException("import invoice not belong to current store");
            }

            List<ImportProductInvoiceDetail> details = repo.findByImportProductInvoiceId(importInvoiceId);

            return details.stream().map(detail -> ImportInvoiceDetailResponse.builder()
                            .id(detail.getId())
                            .product(detail.getProduct())
                            .importProductInvoice(detail.getImportProductInvoice())
                            .quantity(detail.getQuantity())
                            .importPrice(detail.getImportPrice())
                            .totalPrice(detail.getTotalPrice())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while fetching data: " + e.getMessage(), e);
        }
    }

    private ImportProductDetailResponse saveSingleProductInvoiceDetail(ImportProductDetailRequest importProductDetailRequest, ImportProductInvoice invoice) {

        try {
            User currentUser = userService.getCurrentUser();
            Product product = productRepository.findById(importProductDetailRequest.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + importProductDetailRequest.getProductId()));

            ImportProductInvoiceDetail importDetail = new ImportProductInvoiceDetail();
            importDetail.setCreatedBy(currentUser.getUsername());
            importDetail.setProductDetailsAtTimeOfImport(convertToJson(product));
            importDetail.setProduct(product);
            importDetail.setQuantity(importProductDetailRequest.getQuantity());
            importDetail.setImportPrice(importProductDetailRequest.getImportPrice());
            importDetail.setTotalPrice(importProductDetailRequest.getImportPrice() * importProductDetailRequest.getQuantity());
            importDetail.setImportProductInvoice(invoice);
            ImportProductInvoiceDetail savedImportDetail = repo.save(importDetail);

            updateProductInventoryAndImportPrice(product,importProductDetailRequest.getQuantity(),importProductDetailRequest.getImportPrice());

            return modelMapper.map(savedImportDetail, ImportProductDetailResponse.class);
        } catch (EntityNotFoundException e) {
            throw new CustomServiceException(e.getMessage(), e);
        } catch (Exception e) {
            throw new CustomServiceException("An error occurred while saving the invoice detail: " + e.getMessage(), e);
        }
    }

    private void updateProductInventoryAndImportPrice(Product product, double quantity, double importPrice) {
        double currentInventory = product.getInventory() == null ? 0 : product.getInventory();

        product.setInventory(currentInventory + quantity);
        product.setImportPrice(importPrice);

        productRepository.save(product);
    }

    private String convertToJson(Product product) {
        ProductDetailJson productDetailJson =  modelMapper.map(product, ProductDetailJson.class);
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(productDetailJson);
            return jsonString;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
