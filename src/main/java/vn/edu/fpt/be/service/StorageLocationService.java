package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.StorageLocationDTO;
import vn.edu.fpt.be.dto.StorageLocationRequest;

import java.util.List;

public interface StorageLocationService {
    StorageLocationDTO createStorageLocation(StorageLocationRequest storageLocationRequest);
    StorageLocationDTO updateStorageLocation(StorageLocationRequest storageLocationRequest, Long storageLocationId);
    List<StorageLocationDTO> getByStore(Long storeId);
    StorageLocationDTO deactivate(Long storageLocationId);
}
