/**
 * @file Stock.java
 * @Author Nivedha Mathiarasu
 * @date July 25, 2018
 * @brief Stock info for a specific ticker
 * 
 * Keeps information on ticker, timestamps, open/close, high/low, volumes
 *  
 */


package magnuscapital;

public class Stock {
	
	private String ticker;
	private String timeStamp;
	private Double open;
	private Double high;
	private Double low;
	private Double close;
	private int volume;
	

	public Stock() {
		
	}
	

	public Stock(String ticker, String timeStamp, double open, double high, double low, double close, int volume) {
		this.setTicker(ticker);
		this.setTimeStamp(timeStamp);
		this.setOpen(open);
		this.setHigh(high);
		this.setLow(low);
		this.setClose(close);
		this.setVolume(volume);
	}
	
	/**
	 * @brief Returns the stock's ticker
	 * @return ticker
	 */
	public String getTicker() {
		return ticker;
	}

	/** 
	 * *@brief Sets the stock's ticker
	 *  @param ticker the new ticker 
	 *  @return Void. 
	 */
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	/**
	 * @brief Returns the stock's timestamp
	 * @return timestamp
	 */
	public String getTimeStamp() {
		return timeStamp;
	}

	/** 
	 * *@brief Sets the stock's timestamp
	 *  @param timestamp the new timestamp
	 *  @return Void. 
	 */
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @brief Returns the stock's open price
	 * @return open
	 */
	public Double getOpen() {
		return open;
	}

	/** 
	 * *@brief Sets the stock's open price
	 *  @param open the new open 
	 *  @return Void. 
	 */
	public void setOpen(Double open) {
		this.open = open;
	}

	/**
	 * @brief Returns the stock's close price
	 * @return close
	 */
	public Double getClose() {
		return close;
	}

	/** 
	 * *@brief Sets the stock's close price
	 *  @param close the new close
	 *  @return Void. 
	 */
	public void setClose(Double close) {
		this.close = close;
	}

	/**
	 * @brief Returns the stock's high price
	 * @return high
	 */
	public Double getHigh() {
		return high;
	}

	/** 
	 * *@brief Sets the stock's high price
	 *  @param high the new high
	 *  @return Void. 
	 */
	public void setHigh(Double high) {
		this.high = high;
	}

	/**
	 * @brief Returns the stock's low price
	 * @return low
	 */
	public Double getLow() {
		return low;
	}

	/** 
	 * *@brief Sets the stock's low price
	 *  @param low the new low
	 *  @return Void. 
	 */
	public void setLow(Double low) {
		this.low = low;
	}	

	/**
	 * @brief Returns the stock's volume
	 * @return volume
	 */
	public int getVolume() {
		return volume;
	}

	/** 
	 * *@brief Sets the stock's volume
	 *  @param ticker the new volume
	 *  @return Void. 
	 */
	public void setVolume(int volume) {
		this.volume = volume;
	}

}
