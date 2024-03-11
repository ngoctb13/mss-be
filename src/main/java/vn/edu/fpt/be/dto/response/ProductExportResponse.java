package vn.edu.fpt.be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Product;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductExportResponse {
    private Product product; //sản phẩm
    private Double totalExportQuantity; // tổng số lượng đã bán
    private Double totalExportPrice; // tổng tiền của số lượng đã bán = totalExportQuantity * unitPrice
    private Double totalFunds; // tổng tiền vốn của số lượng đã bán = totalExportQuantity * importPrice
    private Double totalProfit; // tổng lợi nhuận  = totalExportPrice - totalFunds
}
