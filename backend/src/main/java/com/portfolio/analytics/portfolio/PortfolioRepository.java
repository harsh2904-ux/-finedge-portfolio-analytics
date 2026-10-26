package com.portfolio.analytics.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findByUserId(Long userId);

    Optional<Portfolio> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);

    @Query("SELECT p FROM Portfolio p LEFT JOIN FETCH p.holdings h LEFT JOIN FETCH h.asset WHERE p.id = :id")
    Optional<Portfolio> findByIdWithHoldings(@Param("id") Long id);
}
