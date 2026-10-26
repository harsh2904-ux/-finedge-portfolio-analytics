package com.portfolio.analytics.holding;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioHoldingRepository extends JpaRepository<PortfolioHolding, Long> {

    List<PortfolioHolding> findByPortfolioId(Long portfolioId);

    Optional<PortfolioHolding> findByPortfolioIdAndAssetId(Long portfolioId, Long assetId);

    Optional<PortfolioHolding> findByIdAndPortfolioId(Long id, Long portfolioId);

    boolean existsByPortfolioIdAndAssetId(Long portfolioId, Long assetId);
}
