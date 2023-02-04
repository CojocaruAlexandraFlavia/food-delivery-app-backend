package com.example.fooddelivery.repository;

import com.example.fooddelivery.model.UserAddress;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    Optional<UserAddress> findByAddress(String address);
    List<UserAddress> findByClientUserId(Long id);
    @NotNull Optional<UserAddress> findById(@NotNull Long addressId);
}
