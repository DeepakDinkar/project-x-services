package com.Qomoi1.Repository;


import com.Qomoi1.Entity.UserEntity;
import com.Qomoi1.Enum.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {


    Optional<UserEntity> findByEmail(String userName);

    UserEntity findByRole(Role user);
}
