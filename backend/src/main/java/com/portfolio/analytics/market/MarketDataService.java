package com.portfolio.analytics.market;

import com.portfolio.analytics.asset.Asset;
import com.portfolio.analytics.asset.AssetService;
import com.portfolio.analytics.common.PagedResponse;
import com.portfolio.analytics.exception.ResourceNotFoundException;
import com.portfolio.analytics.market.dto.LatestPriceResponse;
import com.portfolio.analytics.market.dto.MarketHistoryResponse;
import com.portfolio.analytics.market.dto.MarketPriceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class MarketDataService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MarketDataService.class);


    private final MarketPriceRepository marketPriceRepository;
    private final AssetService assetService;
    public MarketDataService(MarketPriceRepository marketPriceRepository, AssetService assetService) {
        this.marketPriceRepository = marketPriceRepository;
        this.assetService = assetService;
    }


    @Transactional(readOnly = true)
    public LatestPriceResponse getLatestPrice(String symbol) {
        String sym = symbol.toUpperCase();
        Asset asset = assetService.getAssetEntityBySymbol(sym);

        List<MarketPrice> latestTwo = marketPriceRepository.findTop2BySymbolOrderByTimestampDesc(sym, PageRequest.of(0, 2));
        if (latestTwo.isEmpty()) {
            throw new ResourceNotFoundException("No market price available for symbol: " + sym);
        }

        MarketPrice current = latestTwo.get(0);
        Double change = 0.0;
        Double changePercent = 0.0;

        if (latestTwo.size() > 1) {
            MarketPrice previous = latestTwo.get(1);
            change = current.getClose() - previous.getClose();
            if (previous.getClose() > 0) {
                changePercent = (change / previous.getClose()) * 100.0;
            }
        }

        return LatestPriceResponse.builder()
                .symbol(asset.getSymbol())
                .name(asset.getName())
                .currency(asset.getCurrency())
                .price(current.getClose())
                .change(Math.round(change * 100.0) / 100.0)
                .changePercent(Math.round(changePercent * 1000.0) / 1000.0)
                .asOf(current.getTimestamp())
                .build();
    }

    @Transactional(readOnly = true)
    public MarketHistoryResponse getHistory(String symbol, Instant startDate, Instant endDate, Pageable pageable) {
        String sym = symbol.toUpperCase();
        Asset asset = assetService.getAssetEntityBySymbol(sym);

        List<MarketPriceDto> dtos;
        int total;

        if (startDate != null && endDate != null) {
            List<MarketPrice> list = marketPriceRepository.findBySymbolAndDateRange(sym, startDate, endDate);
            total = list.size();
            dtos = list.stream().map(MarketPriceDto::fromEntity).toList();
        } else {
            Page<MarketPrice> page = marketPriceRepository.findBySymbolOrderByTimestampDesc(sym, pageable);
            total = (int) page.getTotalElements();
            dtos = page.getContent().stream().map(MarketPriceDto::fromEntity).toList();
        }

        return MarketHistoryResponse.builder()
                .symbol(asset.getSymbol())
                .currency(asset.getCurrency())
                .totalRecords(total)
                .data(dtos)
                .build();
    }
}
