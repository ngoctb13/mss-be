package vn.edu.fpt.be.repository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.be.model.Product;

import java.util.List;
//import j.persistence.EntityNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Logger logger = LoggerFactory.getLogger(ProductRepository.class);

    @Query("SELECT p, b FROM Product p " +
            ",ProductBagType pb " +
            ",BagType b " +
            "WHERE p.productId = pb.product.productId " +
            "AND b.bagTypeId = pb.bagType.bagTypeId")
    List<Object[]> findAllInformationOfProduct();

//    default List<Product> findAllProductsSafely() {
//        try {
//            return findAllInformationOfProduct();
//        } catch (Exception e) {
//            logger.error("Error occurred while fetching product information: ", e);
//            throw new EntityNotFoundException("Product information could not be retrieved.");
//        }
//    }
}