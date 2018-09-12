/**
 * @file Trade.java
 * @Author Nicole Chow
 * @date July 25, 2018
 * @brief Entity for trades
 * 
 * Keeps information on ids, tickers, number of shares,  action, and strategy.
 * Stored in Trades table
 *  
 */

package magnuscapital;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import enums.Action;
import enums.StrategyType;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;

@SuppressWarnings("serial")
@Entity
@Table(name = "Trades")
public class Trade implements Serializable {

	@Id
	@Column(name = "ID")
	private int id;

	@Column(name = "Ticker")
	private String ticker;

	@Column(name = "Shares")
	private long numShares;

	@Column(name = "Price")
	private double price;

	@Column(name = "Action")
	private Action action;

	@Column(name = "StrategyType")
	private StrategyType strategyType;

	@Column(name = "Strategy")
	private int sId;

	public Trade() {
	}

	public Trade(String ticker, long shares, double price, Action action, StrategyType strategyType, int strategy) {
		setId(new Integer(StrategyHandler.getInstance().getCorrelationId()));
		StrategyHandler.getInstance().incCorelationid();
		setTicker(ticker);
		setNumShares(shares);
		setPrice(price);
		setAction(action);
		setStrategyType(strategyType);
		setStrategy(strategy);
	}

	/**
	 * @brief Returns the  trade's id
	 * @return id
	 */
	public int getId() {
		return this.id;
	}

	/** 
	 * *@brief Sets the Trade's id
	 *  @param id the new  Id 
	 *  @return Void. 
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @brief Returns the  trade's ticker
	 * @return ticker
	 */
	public String getTicker() {
		return ticker;
	}

	/** 
	 * *@brief Sets the Trade's ticker
	 *  @param ticker the new ticker
	 *  @return Void. 
	 */
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	/**
	 * @brief Returns the  trade's shares
	 * @return numShares
	 */
	public long getNumShares() {
		return numShares;
	}

	/** 
	 * *@brief Sets the Trade's number of shares
	 *  @param numShares the new share numbers
	 *  @return Void. 
	 */
	public void setNumShares(long numShares) {
		this.numShares = numShares;
	}

	/**
	 * @brief Returns the  trade's price
	 * @return price
	 */
	public double getPrice() {
		return price;
	}

	/** 
	 * *@brief Sets the Trade's price
	 *  @param price the new price
	 *  @return Void. 
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @brief Returns the  trade's action enum (BUY or SELL)
	 * @return action
	 */
	public Action getAction() {
		return action;
	}

	/** 
	 * *@brief Sets the Trade's action enum (BUY or SELL)
	 *  @param action the new action
	 *  @return Void. 
	 */
	public void setAction(Action action) {
		this.action = action;
	}

	/**
	 * @brief Returns the  trade's strategy type enum (TWOMA or BOLLINGER)
	 * @return strategyType
	 */
	public StrategyType getStrategyType() {
		return strategyType;
	}

	/** 
	 * *@brief Sets the Trade's strategy type enum (TWOMA or BOLLINGER)
	 *  @param strategyType the new strategy type
	 *  @return Void. 
	 */
	public void setStrategyType(StrategyType strategyType) {
		this.strategyType = strategyType;
	}

	/**
	 * @brief Returns the  trade's strategy Id
	 * @return sId
	 */
	public int getStrategy() {
		return sId;
	}

	/** 
	 * *@brief Sets the Trade's strategy id
	 *  @param sId the new strategy's id
	 *  @return Void. 
	 */
	public void setStrategy(int strategy) {
		this.sId = strategy;
	}

}
