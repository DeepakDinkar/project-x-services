package com.qomoi.repository;

import com.qomoi.entity.AddToCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AddToCartRepository extends JpaRepository<AddToCart,Long> {

    @Query("SELECT nextval('add_to_cart_s')")
    Long findSequence();

    AddToCart findBySecretKey(String key);
}
