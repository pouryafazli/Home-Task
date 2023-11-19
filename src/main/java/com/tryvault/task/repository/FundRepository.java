package com.tryvault.task.repository;

import com.tryvault.task.entity.Fund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface FundRepository extends JpaRepository<Fund, Long> {

    @Query("SELECT f FROM Fund f WHERE f.fundId.customerId IN :customerIds AND f.time >= :startDate ")
    List<Fund> findByCustomerIdsAndDate(@Param("customerIds") Set<Long> customerIds, @Param("startDate") LocalDateTime startDate);


}
