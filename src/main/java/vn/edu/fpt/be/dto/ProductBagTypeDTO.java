package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.BagType;
import vn.edu.fpt.be.model.Product;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductBagTypeDTO {
    private Product product;
    private BagType bagType;

//    public ProductBagTypeDTO(Product product, BagType bagType) {
//        this.product = product;
//        this.bagType = bagType;
//    }
}
