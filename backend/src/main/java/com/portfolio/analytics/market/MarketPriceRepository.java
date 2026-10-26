package com.portfolio.analytics.market;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MarketPriceRepository extends JpaRepository<MarketPrice, Long> {

    @Query("SELECT mp FROM MarketPrice mp WHERE mp.asset.symbol = :symbol ORDER BY mp.timestamp DESC")
    Page<MarketPrice> findBySymbolOrderByTimestampDesc(@Param("symbol") String symbol, Pageable pageable);

    @Query("SELECT mp FROM MarketPrice mp WHERE mp.asset.symbol = :symbol AND mp.timestamp BETWEEN :startDate AND :endDate ORDER BY mp.timestamp ASC")
    List<MarketPrice> findBySymbolAndDateRange(
            @Param("symbol") String symbol,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

    @Query(value = "SELECT mp FROM MarketPrice mp WHERE mp.asset.symbol = :symbol ORDER BY mp.timestamp DESC")
    List<MarketPrice> findTop2BySymbolOrderByTimestampDesc(@Param("symbol") String symbol, Pageable pageable);

    @Query("SELECT mp FROM MarketPrice mp WHERE mp.asset.id = :assetId ORDER BY mp.timestamp DESC")
    Optional<MarketPrice> findLatestByAssetId(@Param("assetId") Long assetId);

    @Query("SELECT mp FROM MarketPrice mp WHERE mp.asset.symbol = :symbol ORDER BY mp.timestamp DESC LIMIT 1")
    Optional<MarketPrice> findLatestBySymbol(@Param("symbol") String symbol);
}
