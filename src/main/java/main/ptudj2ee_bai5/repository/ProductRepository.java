package main.ptudj2ee_bai5.repository;

import main.ptudj2ee_bai5.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
