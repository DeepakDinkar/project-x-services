package com.Qomoi.Qomoi.Repository;

import com.Qomoi.Qomoi.Entity.UserEntity;
import com.Qomoi.Qomoi.Enum.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {


    Optional<UserEntity> findByEmail(String userName);

    UserEntity findByRole(Role user);
}
