package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.StorageLocationDTO;
import vn.edu.fpt.be.dto.StorageLocationRequest;
import vn.edu.fpt.be.dto.request.StorageLocationForProductRequest;
import vn.edu.fpt.be.dto.response.ProductLocationResponse;

import java.util.List;

public interface StorageLocationService {
    StorageLocationDTO createStorageLocation(StorageLocationRequest storageLocationRequest);
    StorageLocationDTO updateStorageLocation(StorageLocationRequest storageLocationRequest, Long storageLocationId);
    List<StorageLocationDTO> getByStore();
    StorageLocationDTO deactivate(Long storageLocationId);
    ProductLocationResponse listProductLocation();
    StorageLocationDTO addOrUpdateNewStorageLocation(StorageLocationForProductRequest storageLocationRequest);
    StorageLocationDTO addNewStorageLocation(StorageLocationForProductRequest storageLocationRequest);
    List<StorageLocationDTO> findLocationProduct();
}
