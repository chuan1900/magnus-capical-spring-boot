/**
 * @file StockInfo.java
 * @Author Nivedha Mathiarasu
 * @date July 25, 2018
 * @brief Singleton to consolidate stock information
 * 
 * Keeps information on tickers, and their prices over specified periods of time
 * Implemented with hashmap
 *  
 */

package magnuscapital;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.*;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class StockInfo {
	private final static Logger LOGGER = Logger.getLogger(StockInfo.class.getName());
	private FileHandler fh;
	private HashMap<String, ArrayList<Stock>> currentPrices;
	private static ArrayList<Stock> stocks;
	private static Boolean headerLine = false;
	private static StockInfo singleInstance = null;

	private StockInfo() {
		currentPrices = new HashMap<String, ArrayList<Stock>>();
		stocks = new ArrayList<Stock>();
		try {
			fh = new FileHandler("StockInfo.log");
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		LOGGER.setUseParentHandlers(false);
		LOGGER.addHandler(fh);
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);
		LOGGER.info("Singleton instantiated");
	}

	public static StockInfo getInstance() {
		if (singleInstance == null) {
			singleInstance = new StockInfo();
		}
		return singleInstance;
	}

	/**
	 * @brief Populates the hashmap with ticker and it's price values
	 * @param ticker
	 * @param priceline
	 *            the line of info to be parsed
	 * @return currentPrices
	 */
	public Map<String, ArrayList<Stock>> populateMap(String ticker, String priceLine) {
		String split = ",";

		try {
			String[] value = priceLine.split(split);
			if (value[0].equals("timestamp") && value[1].equals("open") && value[2].equals("high")
					&& value[3].equals("low") && value[4].equals("close") && value[5].equals("volume")) {
				StockInfo.getInstance().headerLine = true;
			} else {
				StockInfo.getInstance().headerLine = false;
			}

			System.out.println("priceLine: " + priceLine);

			if (StockInfo.getInstance().headerLine == true) {
				System.out.println("HEADERLINE = TRUE : DO NOT STORE IN HASHMAP");
			} else {
				System.out.println("HEADERLINE = FALSE");
				String timeStamp = value[0];
				double open = Double.parseDouble(value[1]);
				double high = Double.parseDouble(value[2]);
				double low = Double.parseDouble(value[3]);
				double close = Double.parseDouble(value[4]);
				int volume = Integer.parseInt(value[5]);

				Stock stock = new Stock(ticker, timeStamp, open, high, low, close, volume);
				StockInfo.getInstance().stocks.add(stock);
				StockInfo.getInstance().currentPrices.put(ticker, StockInfo.getInstance().stocks);
			}
			LOGGER.info(ticker + " loaded into map.");

		} catch (NumberFormatException e) {
			LOGGER.log(Level.WARNING, "Number format exception", e);
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception occur", e);
			e.printStackTrace();
		}
		return StockInfo.getInstance().currentPrices;
	}

	/**
	 * @brief Obtain list of prices from hashmap given the ticker
	 * @param ticker
	 * @return ArrayList<Stock> if ticker in map, else null
	 */
	public ArrayList<Stock> queryStock(String ticker) {
		if (currentPrices.isEmpty()) {
			System.out.println("Hashmap empty: no prices available");
			return null;
		} else if (currentPrices.containsKey(ticker)) {
			ArrayList<Stock> result = new ArrayList<Stock>();
			result = currentPrices.get(ticker);
			System.out.println("TICKER: " + ticker);
			for (Stock s : result) {
				System.out.println("TIMESTAMP: " + s.getTimeStamp());
				System.out.println("OPEN: " + s.getOpen());
				System.out.println("HIGH: " + s.getHigh());
				System.out.println("LOW: " + s.getLow());
				System.out.println("CLOSE: " + s.getClose());
				System.out.println("VOLUME: " + s.getVolume());
			}
			LOGGER.info(ticker + " query success");
			return result;
		} else {
			LOGGER.info(ticker + " doesn't exist in map");
			return null;
		}
	}

	/**
	 * @brief Obtain list of low prices from hashmap given the ticker
	 * @param ticker
	 * @return ArrayList<Double> if ticker in map, else null
	 */
	public ArrayList<Double> listLowPrices(String ticker) {
		ArrayList<Double> list = new ArrayList<Double>();
		if (currentPrices.containsKey(ticker)) {
			ArrayList<Stock> result = new ArrayList<Stock>();
			result = currentPrices.get(ticker);
			for (Stock s : result) {
				list.add(s.getLow());
			}
		}
		LOGGER.info(ticker + " low prices list returned");
		return list;
	}

	/**
	 * @brief Obtain list of high prices from hashmap given the ticker
	 * @param ticker
	 * @return ArrayList<high> if ticker in map, else null
	 */
	public ArrayList<Double> listHighPrices(String ticker) {
		ArrayList<Double> list = new ArrayList<Double>();
		if (currentPrices.containsKey(ticker)) {
			ArrayList<Stock> result = new ArrayList<Stock>();
			result = currentPrices.get(ticker);
			for (Stock s : result) {
				list.add(s.getHigh());
			}
		}
		LOGGER.info(ticker + " high prices list returned");
		return list;
	}

	/**
	 * @brief Get pricing data from REST API given the ticker and periods
	 * @param priceURL url to grab data from
	 * @param ticker the ticker of the stocks
	 * @param periods number of periods to query
	 * @return true if data successfully grabbed, else false
	 * @throws IOException 
	 */
	public Boolean getPricingData(String priceURL, String tickerSymbol, int periods) throws IOException{
		BufferedReader rd = null;
		try {
			priceURL = priceURL + "/" + tickerSymbol + "?" + "periods=" + periods;
			URL url = new URL(priceURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;

			while ((line = rd.readLine()) != null) {
				StockInfo.getInstance().populateMap(tickerSymbol, line);
			}
			LOGGER.info(tickerSymbol + " from " + priceURL + "successfully grabbed.");
			return true;
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error reading " + tickerSymbol + " from url", e);
			return false;
		} finally {
			rd.close();
		}
	}

}
