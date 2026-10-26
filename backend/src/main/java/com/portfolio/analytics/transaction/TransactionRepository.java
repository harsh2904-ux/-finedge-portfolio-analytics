package com.portfolio.analytics.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByPortfolioIdOrderByTransactionDateDesc(Long portfolioId);

    Page<Transaction> findByPortfolioIdOrderByTransactionDateDesc(Long portfolioId, Pageable pageable);

    Optional<Transaction> findByIdAndPortfolioId(Long id, Long portfolioId);
}
