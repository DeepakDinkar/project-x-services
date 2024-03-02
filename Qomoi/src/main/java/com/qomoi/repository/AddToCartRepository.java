package com.qomoi.repository;

import com.qomoi.entity.AddToCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
@Repository
public interface AddToCartRepository extends JpaRepository<AddToCart,Long> {

    @Query("SELECT nextval('add_to_cart_s')")
    Long findSequence();

    AddToCart findBySecretKey(String key);

    @Transactional
    @Modifying
    @Query(value = "delete from cart_data where secret_key = :secretKey",
            nativeQuery = true)
    void deleteSecretKey(String secretKey);
}
