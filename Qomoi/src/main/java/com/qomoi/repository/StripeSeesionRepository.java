package com.qomoi.repository;

import com.qomoi.entity.StripeSeesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StripeSeesionRepository extends JpaRepository<StripeSeesion, Long> {
    StripeSeesion findByPaymentIntent(String payIntent);
}
