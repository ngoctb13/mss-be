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
public class ProductRequest {
    private String description;
    private double import_price;
    private String product_name;
    private double retail_price;
    private String unit;
    private double wholesale_price;
    private String unitBagType;
    private double weight;


    public ProductRequest(Product product, BagType bagType) {
    }
}
