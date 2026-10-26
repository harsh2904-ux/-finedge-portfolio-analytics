package com.portfolio.analytics.asset;

import com.portfolio.analytics.asset.dto.AssetResponse;
import com.portfolio.analytics.asset.dto.CreateAssetRequest;
import com.portfolio.analytics.asset.dto.UpdateAssetRequest;
import com.portfolio.analytics.common.PagedResponse;
import com.portfolio.analytics.exception.DuplicateResourceException;
import com.portfolio.analytics.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssetService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AssetService.class);


    private final AssetRepository assetRepository;
    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }


    @Transactional(readOnly = true)
    public PagedResponse<AssetResponse> getAssets(String assetClass, String country, String exchange, Pageable pageable) {
        Specification<Asset> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(assetClass)) {
                predicates.add(cb.equal(cb.upper(root.get("assetClass")), assetClass.toUpperCase()));
            }
            if (StringUtils.hasText(country)) {
                predicates.add(cb.equal(cb.upper(root.get("country")), country.toUpperCase()));
            }
            if (StringUtils.hasText(exchange)) {
                predicates.add(cb.equal(cb.upper(root.get("exchange")), exchange.toUpperCase()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Asset> page = assetRepository.findAll(spec, pageable);
        return PagedResponse.from(page.map(AssetResponse::fromAsset));
    }

    @Transactional(readOnly = true)
    public AssetResponse getAssetById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + id));
        return AssetResponse.fromAsset(asset);
    }

    @Transactional(readOnly = true)
    public Asset getAssetEntityById(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Asset getAssetEntityBySymbol(String symbol) {
        return assetRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with symbol: " + symbol));
    }

    @Transactional
    public AssetResponse createAsset(CreateAssetRequest request) {
        String symbol = request.getSymbol().trim().toUpperCase();
        if (assetRepository.existsBySymbol(symbol)) {
            throw new DuplicateResourceException("Asset with symbol " + symbol + " already exists.");
        }

        String providerSymbol = StringUtils.hasText(request.getProviderSymbol())
                ? request.getProviderSymbol().trim().toUpperCase()
                : symbol;

        Asset asset = Asset.builder()
                .symbol(symbol)
                .providerSymbol(providerSymbol)
                .name(request.getName().trim())
                .assetClass(request.getAssetClass().trim().toUpperCase())
                .sector(request.getSector())
                .currency(StringUtils.hasText(request.getCurrency()) ? request.getCurrency().trim().toUpperCase() : "INR")
                .exchange(request.getExchange())
                .country(request.getCountry())
                .isActive(true)
                .build();

        Asset saved = assetRepository.save(asset);
        log.info("Created new asset ID={} symbol={}", saved.getId(), saved.getSymbol());
        return AssetResponse.fromAsset(saved);
    }

    @Transactional
    public AssetResponse updateAsset(Long id, UpdateAssetRequest request) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + id));

        if (StringUtils.hasText(request.getName())) {
            asset.setName(request.getName().trim());
        }
        if (StringUtils.hasText(request.getAssetClass())) {
            asset.setAssetClass(request.getAssetClass().trim().toUpperCase());
        }
        if (request.getSector() != null) {
            asset.setSector(request.getSector());
        }
        if (StringUtils.hasText(request.getCurrency())) {
            asset.setCurrency(request.getCurrency().trim().toUpperCase());
        }
        if (request.getExchange() != null) {
            asset.setExchange(request.getExchange());
        }
        if (request.getCountry() != null) {
            asset.setCountry(request.getCountry());
        }
        if (request.getIsActive() != null) {
            asset.setActive(request.getIsActive());
        }

        Asset updated = assetRepository.save(asset);
        log.info("Updated asset ID={} symbol={}", updated.getId(), updated.getSymbol());
        return AssetResponse.fromAsset(updated);
    }

    @Transactional
    public void deleteAsset(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + id));
        assetRepository.delete(asset);
        log.info("Deleted asset ID={} symbol={}", id, asset.getSymbol());
    }
}
