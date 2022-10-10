package com.market.stock.query.eventhandler;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.market.stock.domain.Stock;
import com.market.stock.domain.StockDocument;
import com.market.stock.query.GetStocksQuery;
import com.market.stock.repository.StockMongoRepository;
import com.market.stock.repository.StockRepository;
import com.market.stock.response.StockMongoResponse;
import com.market.stock.response.StockResponse;

/**
 * GetStocksQueryHandler
 * 
 * @author User
 *
 */
@Component
public class GetStocksQueryHandler {

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private StockMongoRepository stockMongoRepository;

	public List<Stock> getAllStocks() {
		return stockRepository.findAll();
	}

	/**
	 * getStocksByDateV2 - Get stock details from MongoDB Atlas
	 * 
	 * @param getStocksQuery getStocksQuery
	 * @return StockMongoResponse
	 */
	@QueryHandler
	public StockMongoResponse getStocksByDateV2(GetStocksQuery getStocksQuery) {

		StockMongoResponse response = new StockMongoResponse();

		// List<StockMongo> companyStocks =
		// stockMongoRepository.findByCompanyCode(getStocksQuery.getCompanyCode());
		// List<StockMongo> stocks = new ArrayList<>();

		List<StockDocument> stocks = stockMongoRepository.getStocksByDate(getStocksQuery.getCompanyCode(),
				getStocksQuery.getStartdate(), getStocksQuery.getEnddate());

		/*
		 * stocks = companyStocks.stream() .filter(p ->
		 * ((p.getStockStartDate().equals(getStocksQuery.getStartdate()) ||
		 * p.getStockStartDate().after(getStocksQuery.getStartdate())) &&
		 * (p.getStockStartDate().equals(getStocksQuery.getEnddate()) ||
		 * p.getStockStartDate().before(getStocksQuery.getEnddate()))))
		 * .collect(Collectors.toList());
		 */

		if (stocks != null && !stocks.isEmpty()) {
			List<Double> stockPrices = stocks.stream().map(StockDocument::getStockPrice).collect(Collectors.toList());

			Double minStockPrice = stockPrices.stream().min(Comparator.naturalOrder()).get();
			Double maxStockPrice = stockPrices.stream().max(Comparator.naturalOrder()).get();
			Double avgStockPrice = stockPrices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

			response.setStocks(stocks);
			response.setMinStockPrice(minStockPrice);
			response.setMaxStockPrice(maxStockPrice);
			response.setAvgStockPrice(avgStockPrice);
		}

		return response;
	}

	/**
	 * getStocksByDate - NOT IN USE - Get Stock details from MySql
	 * 
	 * @param getStocksQuery getStocksQuery
	 * @return StockResponse
	 */
	// @QueryHandler
	public StockResponse getStocksByDate(GetStocksQuery getStocksQuery) {

		StockResponse response = new StockResponse();

		List<Stock> stocks = stockRepository.getStocksByDate(getStocksQuery.getCompanyCode(),
				getStocksQuery.getStartdate(), getStocksQuery.getEnddate());

		if (stocks != null && !stocks.isEmpty()) {
			List<Double> stockPrices = stocks.stream().map(Stock::getStockPrice).collect(Collectors.toList());

			Double minStockPrice = stockPrices.stream().min(Comparator.naturalOrder()).get();
			Double maxStockPrice = stockPrices.stream().max(Comparator.naturalOrder()).get();
			Double avgStockPrice = stockPrices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

			response.setStocks(stocks);
			response.setMinStockPrice(minStockPrice);
			response.setMaxStockPrice(maxStockPrice);
			response.setAvgStockPrice(avgStockPrice);
		}
		return response;
	}

}
