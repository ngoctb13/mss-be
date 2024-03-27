package vn.edu.fpt.be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StorageLocationForProductRequest {
    private Long storageLocationIds; // Danh sách các ID vị trí lưu trữ
    private Long productId;
}
