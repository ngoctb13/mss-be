package vn.edu.fpt.be.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.ImportProductDetailResponse;
import vn.edu.fpt.be.dto.SaleInvoiceDetailDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailRequest;
import vn.edu.fpt.be.dto.response.ProductExportResponse;
import vn.edu.fpt.be.dto.response.ProductSalesResponse;
import vn.edu.fpt.be.exception.CustomServiceException;
import vn.edu.fpt.be.model.*;
import vn.edu.fpt.be.model.jsonDetail.ProductDetailJson;
import vn.edu.fpt.be.repository.ProductRepository;
import vn.edu.fpt.be.repository.SaleInvoiceDetailRepository;
import vn.edu.fpt.be.repository.SaleInvoiceRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.SaleInvoiceDetailService;
import vn.edu.fpt.be.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleInvoiceDetailServiceImpl implements SaleInvoiceDetailService {
    private final SaleInvoiceDetailRepository saleInvoiceDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SaleInvoiceRepository saleInvoiceRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ModelMapper modelMapper = new ModelMapper();
    @Override
    public void saveSaleInvoiceDetail(List<SaleInvoiceDetailRequest> requests, SaleInvoice invoice) {
        for (SaleInvoiceDetailRequest request : requests) {
            SaleInvoiceDetailDTO createdDetail = saveSingleSaleInvoiceDetail(request, invoice);
        }
    }

    @Override
    public List<ProductExportResponse> productExportReport(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            User currentUser = userService.getCurrentUser();
            Long currentStoreId = currentUser.getStore().getId();
            if (currentStoreId == null) {
                throw new RuntimeException("Current user not have store yet!");
            }

            List<SaleInvoiceDetail> details = saleInvoiceDetailRepository.findByCriteria(customerId, startDate, endDate, currentStoreId);
            Map<Long, ProductExportResponse> responseMap = new HashMap<>();

            for (SaleInvoiceDetail detail : details) {
                Long productId = detail.getProduct().getId();
                ProductExportResponse response = responseMap.getOrDefault(productId, new ProductExportResponse(detail.getProduct(), 0.0, 0.0, 0.0, 0.0));

                double quantity = detail.getQuantity();
                double unitPrice = detail.getUnitPrice();
                double importPrice = extractImportPrice(detail.getProductDetailsAtTimeOfBuy()); // Giả sử bạn có phương thức này để lấy giá nhập từ JSON

                response.setTotalExportQuantity(response.getTotalExportQuantity() + quantity);
                response.setTotalExportPrice(response.getTotalExportPrice() + (quantity * unitPrice));
                response.setTotalFunds(response.getTotalFunds() + (quantity * importPrice));
                response.setTotalProfit(response.getTotalExportPrice() - response.getTotalFunds());

                responseMap.put(productId, response);
            }
            return new ArrayList<>(responseMap.values());
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while fetching data: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SaleInvoiceDetailDTO> getDetailsOfSaleInvoice(Long saleInvoiceId) {
        try {
            User currentUser = userService.getCurrentUser();
            Optional<SaleInvoice> saleInvoice = saleInvoiceRepository.findById(saleInvoiceId);
            if (saleInvoice.isEmpty()) {
                throw new IllegalArgumentException("Sale invoice cannot be null!");
            }
            if (!saleInvoice.get().getStore().equals(currentUser.getStore())) {
                throw new IllegalArgumentException("Sale invoice not belong to current store");
            }

            List<SaleInvoiceDetail> details = saleInvoiceDetailRepository.findBySaleInvoiceId(saleInvoiceId);

            return details.stream().map(detail -> SaleInvoiceDetailDTO.builder()
                            .id(detail.getId())
                            .product(detail.getProduct())
                            .saleInvoice(detail.getSaleInvoice())
                            .quantity(detail.getQuantity())
                            .unitPrice(detail.getUnitPrice())
                            .totalPrice(detail.getTotalPrice())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while fetching data: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ProductSalesResponse> getProductSalesByMonthAndYear(int month, int year) {
        try {
            User currentUser = userService.getCurrentUser();
            Long currentStoreId = currentUser.getStore().getId();
            if (currentStoreId == null) {
                throw new RuntimeException("Current user not have store yet!");
            }

            List<SaleInvoiceDetail> details = saleInvoiceDetailRepository.findByStoreIdAndMonthAndYear(currentStoreId, month, year);
            Map<Long, ProductSalesResponse> responseMap = new HashMap<>();

            for (SaleInvoiceDetail detail : details) {
                Long productId = detail.getProduct().getId();
                ProductSalesResponse response = responseMap.getOrDefault(productId, new ProductSalesResponse(detail.getProduct().getProductName(), 0.0));

                double quantity = detail.getQuantity();

                response.setTotalSaleQuantity(response.getTotalSaleQuantity() + quantity);

                responseMap.put(productId, response);
            }
            return new ArrayList<>(responseMap.values());
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while fetching data: " + e.getMessage(), e);
        }
    }

    private double extractImportPrice(String productDetailsAtTimeOfBuy) {
        try {
            JsonNode rootNode = objectMapper.readTree(productDetailsAtTimeOfBuy);
            JsonNode importPriceNode = rootNode.path("importPrice");
            if (!importPriceNode.isMissingNode()) {
                return importPriceNode.asDouble();
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return 0;
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
            saleInvoiceDetail.setProductDetailsAtTimeOfBuy(convertToJson(product));
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
