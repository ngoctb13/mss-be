package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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

}
