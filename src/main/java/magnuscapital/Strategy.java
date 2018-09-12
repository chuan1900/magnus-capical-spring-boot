/**
 * @file Strategy.java
 * @Author Sarthak Bansal
 * @Author Yican Shi
 * @date July 25, 2018
 * @brief Entity for strategies
 * 
 * Keeps information on ids, tickers, number of shares,  action, and long/short periods
 * Calculates final profits/ROI
 * executes twoMA or Bollinger strategy depending on its type
 * Stored in Strategies table
 *  
 */

package magnuscapital;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.Persistence;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.tuple.entity.EntityMetamodel.GenerationStrategyPair;

import StrategyAlgos.MovingAverage;
import enums.Action;
import enums.StrategyPosition;
import enums.StrategyType;
import enums.message;
import jms.MessageHandler;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.persistence.Column;

@SuppressWarnings("serial")
@Entity
@Table(name = "Strategy")
public class Strategy implements Serializable {
	
	@Id
	@Column(name = "ID")
	private int id;

	@Column(name = "Ticker")
	private String ticker;

	@Column(name = "Number_of_Shares")
	private long shares;

	@Column(name = "Strategy")
	protected StrategyType type;

	@Column(name = "longAverage")
	private double longAvg;

	@Column(name = "shortAverage")
	private double shortAvg;

	@Column(name = "longPeriod")
	private int longPeriod;

	@Column(name = "shortPeriod")
	private int shortPeriod;

	@Transient
	private MovingAverage l;

	@Transient
	private MovingAverage s;

	@Column(name = "BBperiod")
	private int period;

	@Column(name = "mean")
	private double mean;

	@Transient
	private message BrokerMessage;
	
	@Transient
	private double lastOpenPrice;
	
	@Transient
	private long currentShares;
	
	@Transient
	private StrategyPosition currentPosition;
	
	@Transient
	private Action currentAction;
	
	@Transient
	private final static Logger LOGGER = Logger.getLogger(Strategy.class.getName());
	
	@Transient
	private FileHandler fh;


	public Strategy() {
		id = new Integer(StrategyHandler.getInstance().getStrategyId());
		StrategyHandler.getInstance().addToList(this);
	}

	public Strategy(String Ticker, long Shares, StrategyType stype, int longPeriod, int shortPeriod, int period) {
		try {
			fh = new FileHandler("Strategy.log");
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		LOGGER.setUseParentHandlers(false);
		LOGGER.addHandler(fh);
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);

		id = new Integer(StrategyHandler.getInstance().getStrategyId());
		StrategyHandler.getInstance().incStrategyId();
		this.ticker = Ticker;
		this.shares = Shares;
		this.type = stype;
		if (stype.equals(StrategyType.TWOMA)) {
			this.longPeriod = longPeriod;
			this.shortPeriod = shortPeriod;
			longAvg = 0;
			shortAvg = 0;
			l = new MovingAverage(longPeriod);
			s = new MovingAverage(shortPeriod);
			period = 0;

		} else if (stype.equals(StrategyType.BOLLINGER)) {
			longPeriod = 0;
			shortPeriod = 0;
			l = null;
			s = null;
			currentPosition = StrategyPosition.CLOSE;
			this.period = period;
		}
		longAvg = 0;
		shortAvg = 0;
		mean = 0;
		currentPosition = StrategyPosition.CLOSE;
		StrategyHandler.getInstance().addToList(this);
		StrategyHandler.getInstance().strategyService().add(this);
	}

	/** 
	 * *@brief Analysis for Two Moving Averages
	 *  @param list of lows and highs
	 *  @return Void. 
	 */
	public void TwoMAExecute(ArrayList<Double> lowPriceList, ArrayList<Double> highPriceList) {
		for (int i = 0; i < lowPriceList.size(); i++) {
			double stockPrice = (lowPriceList.get(i) + highPriceList.get(i)) / 2;
			l.addData(stockPrice);
			s.addData(stockPrice);
			longAvg = l.getMean();
			shortAvg = s.getMean();

			if (getCurrentPosition().equals(StrategyPosition.CLOSE)) {
				LOGGER.info("currently closed - id = " + this.getId());
				if (shortAvg > longAvg) {
					setCurrentPosition(StrategyPosition.OPENING);
					LOGGER.info("opening short - id = " + this.getId());
					Trade newTrade = new Trade(ticker, shares, stockPrice, Action.BUY, StrategyType.TWOMA, this.id);
					StrategyHandler.getInstance().getTrades().add(newTrade);
					MessageHandler.initiateTrade(newTrade, "spring-beans.xml");
					setLastOpenPrice(stockPrice);
					setCurrentAction(Action.LONG);
					LOGGER.info("BUY, LONG, id = " + this.getId() + " new trade id = " + newTrade.getId());
				} else if (shortAvg < longAvg) {
					setCurrentPosition(StrategyPosition.OPENING);
					LOGGER.info("opening long - id = " + this.getId());
					Trade newTrade = new Trade(ticker, shares, stockPrice, Action.SELL, StrategyType.TWOMA, this.id);
					StrategyHandler.getInstance().getTrades().add(newTrade);
					MessageHandler.initiateTrade(newTrade, "spring-beans.xml");
					setLastOpenPrice(stockPrice);
					setCurrentAction(Action.SHORT);
					LOGGER.info("SELL, SHORT, id = " + this.getId() + " new trade id = " + newTrade.getId());
				} else {
					LOGGER.info("NO POSITION id = " + this.getId());
				}
			} else if (getCurrentPosition().equals(StrategyPosition.OPEN)) {
				LOGGER.info(" currently open - id = " + this.getId());
				double percentage = ((i - getLastOpenPrice()) / getLastOpenPrice()) * 100;
				if (percentage > 30 || percentage < -30) {
					LOGGER.info("closed IF open - id = " + this.getId());
					setCurrentPosition(StrategyPosition.CLOSE);
				}
			}
		}
	}

	/** 
	 * *@brief Analysis for Bollinger Bands
	 *  @param list of lows and highs
	 *  @return Void. 
	 */
	public void BBExecute(ArrayList<Double> lowPriceList, ArrayList<Double> highPriceList) {
		double total_average = 0;
		ArrayList<Double> temp = new ArrayList<Double>();
		for (int i = 0; i < lowPriceList.size(); i++) {
			double stockPrice = (lowPriceList.get(i) + highPriceList.get(i)) / 2;
			temp.add(stockPrice);
			total_average += stockPrice;
			if (i >= period - 1) {
				double total_bollinger = 0;
				double average = total_average / period;

				for (int x = i; x > (i - period); x--) {
					total_bollinger += Math.pow((temp.get(x) - average), 2);
				}
				double stdev = Math.sqrt(total_bollinger / period);
				double h = (stockPrice - mean) / stdev;

				if (getCurrentPosition().equals(StrategyPosition.CLOSE)) {
					LOGGER.info("currently closed id = " + this.getId());
					if (h < -2) {
						setCurrentPosition(StrategyPosition.OPENING);
						Trade newTrade = new Trade(ticker, shares, stockPrice, Action.BUY, StrategyType.BOLLINGER,
								this.id);
						StrategyHandler.getInstance().getTrades().add(newTrade);
						MessageHandler.initiateTrade(newTrade, "spring-beans.xml");
						setLastOpenPrice(stockPrice);
						LOGGER.info("BUY id = " + this.getId() + " new trade id = " + newTrade.getId());
						setCurrentAction(Action.LONG);
					} else if (h > 2) {
						LOGGER.info("Opening -  id = " + this.getId());
						setCurrentPosition(StrategyPosition.OPENING);
						Trade newTrade = new Trade(ticker, shares, stockPrice, Action.SELL, StrategyType.BOLLINGER,
								this.id);
						StrategyHandler.getInstance().getTrades().add(newTrade);
						MessageHandler.initiateTrade(newTrade, "spring-beans.xml");
						setLastOpenPrice(stockPrice);
						LOGGER.info("SELL id = " + this.getId() + " new trade id = " + newTrade.getId());
						setCurrentAction(Action.SHORT);
					}
				} else if (getCurrentPosition().equals(StrategyPosition.OPEN)) {
					LOGGER.info("currently open - id = " + this.getId());
					double percentage = ((i - getLastOpenPrice()) / getLastOpenPrice()) * 100;
					// if price is in between the threshold values
					if (percentage > 30 || percentage < -30) {
						setCurrentPosition(StrategyPosition.CLOSE);
						LOGGER.info("Closed -  id = " + this.getId());
					}
				} else {
					LOGGER.info("NO POSITION id = " + this.getId());
				}
			}
		}
	}

	/** 
	 * *@brief Determines which strategy execute should be called
	 *  @param list of lows and highs
	 *  @return Void. 
	 * @throws IOException 
	 */
	public void execute(ArrayList<Double> lowPriceList, ArrayList<Double> highPriceList) throws IOException {
		StockInfo.getInstance().getPricingData("http://incanada1.conygre.com:9080/prices", ticker, 1);
		if (getType().equals(StrategyType.TWOMA)) {
			LOGGER.info("TWOMA2");
			TwoMAExecute(lowPriceList, highPriceList);
		} else {
			BBExecute(lowPriceList, highPriceList);
		}
	}

	/** 
	 * *@brief Determines next action based on open/close results from broker
	 *  @return Void. 
	 */
	public void MessageInterpreter() {
		LOGGER.info("message interpreter called - id = " + this.getId());
		if (currentPosition.equals(StrategyPosition.OPENING) && BrokerMessage.equals(message.APPROVED)) {
			currentPosition = StrategyPosition.OPEN;
			LOGGER.info("open from approved -  id = " + this.getId());
		} else if (currentPosition.equals(StrategyPosition.OPENING) && BrokerMessage.equals(message.DECLINED)) {
			currentPosition = StrategyPosition.CLOSE;
			LOGGER.info("closed from rejection - id = " + this.getId());
		} else if (currentPosition.equals(StrategyPosition.OPENING) && BrokerMessage.equals(message.PARTIAL_FILL)) {
			currentPosition = StrategyPosition.OPEN;
			LOGGER.info("open from partial fill -  id = " + this.getId());
			Trade newTrade = null;
			if (currentAction.equals(Action.LONG)) {
				newTrade = new Trade(ticker, currentShares, lastOpenPrice, Action.SELL, StrategyType.TWOMA, this.id);
			} else {
				newTrade = new Trade(ticker, currentShares, lastOpenPrice, Action.BUY, StrategyType.TWOMA, this.id);
			}
			LOGGER.info("Trade initiated, trade id = " + newTrade.getId());
			StrategyHandler.getInstance().getTrades().add(newTrade);
			MessageHandler.initiateTrade(newTrade, "spring-beans.xml");
		} else {
			LOGGER.info("positoin not valid - id  = " + this.getId());
		}
	}

	/**
	 * @brief Returns the strategy's id
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/** 
	 * *@brief sets strategy's id
	 *  @param id
	 *  @return Void. 
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @brief Returns the strategy's ticker
	 * @return ticker
	 */
	public String getTicker() {
		return ticker;
	}

	/** 
	 * *@brief sets strategy's ticker
	 *  @param ticker
	 *  @return Void. 
	 */
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	/**
	 * @brief Returns the strategy's shares
	 * @return shares
	 */
	public long getShares() {
		return shares;
	}

	/** 
	 * *@brief sets strategy's shares
	 *  @param shares
	 *  @return Void. 
	 */
	public void setShares(long shares) {
		this.shares = shares;
	}

	/**
	 * @brief Returns the strategy's type
	 * @return StrategyType
	 */
	public StrategyType getType() {
		return type;
	}

	/** 
	 * *@brief sets strategy's type
	 *  @param type
	 *  @return Void. 
	 */
	public void setType(StrategyType type) {
		this.type = type;
	}

	/**
	 * @brief Returns the strategy's long average
	 * @return longAvg
	 */
	public double getLongAvg() {
		return longAvg;
	}

	/** 
	 * *@brief sets strategy's long average
	 *  @param longAvg
	 *  @return Void. 
	 */
	public void setLongAvg(double longAvg) {
		this.longAvg = longAvg;
	}

	/**
	 * @brief Returns the strategy's short average
	 * @return shortAvg
	 */
	public double getShortAvg() {
		return shortAvg;
	}

	/** 
	 * *@brief sets strategy's short average
	 *  @param shortAvg
	 *  @return Void. 
	 */
	public void setShortAvg(double shortAvg) {
		this.shortAvg = shortAvg;
	}

	/**
	 * @brief Returns the strategy's long period
	 * @return longPeriod
	 */
	public int getLongPeriod() {
		return longPeriod;
	}

	/** 
	 * *@brief sets strategy's long period
	 *  @param longPeriod
	 *  @return Void. 
	 */
	public void setLongPeriod(int longPeriod) {
		this.longPeriod = longPeriod;
	}

	/**
	 * @brief Returns the strategy's short period
	 * @return shortPeriod
	 */
	public int getShortPeriod() {
		return shortPeriod;
	}

	/** 
	 * *@brief sets strategy's short period
	 *  @param shortPeriod
	 *  @return Void. 
	 */
	public void setShortPeriod(int shortPeriod) {
		this.shortPeriod = shortPeriod;
	}

	/**
	 * @brief Returns the strategy's long Moving Average
	 * @return MovingAverage
	 */
	public MovingAverage getL() {
		return l;
	}

	/** 
	 * *@brief sets strategy's long moving average
	 *  @param MovingAverage
	 *  @return Void. 
	 */
	public void setL(MovingAverage l) {
		this.l = l;
	}

	/**
	 * @brief Returns the strategy's short Moving Average
	 * @return MovingAverage
	 */
	public MovingAverage getS() {
		return s;
	}

	/** 
	 * *@brief sets strategy's short moving average
	 *  @param MovingAverage
	 *  @return Void. 
	 */
	public void setS(MovingAverage s) {
		this.s = s;
	}

	/**
	 * @brief Returns the strategy's period
	 * @return period
	 */
	public int getPeriod() {
		return period;
	}

	/** 
	 * *@brief sets strategy's period
	 *  @param period
	 *  @return Void. 
	 */
	public void setPeriod(int period) {
		this.period = period;
	}

	/**
	 * @brief Returns the strategy's mean
	 * @return mean
	 */
	public double getMean() {
		return mean;
	}

	/** 
	 * *@brief sets strategy's mean
	 *  @param mean
	 *  @return Void. 
	 */
	public void setMean(double mean) {
		this.mean = mean;
	}

	/**
	 * @brief Returns the strategy's broker response
	 * @return message
	 */
	public message getBrokerMessage() {
		return BrokerMessage;
	}

	/** 
	 * *@brief sets strategy's broker response message
	 *  @param Message
	 *  @return Void. 
	 */
	public void setBrokerMessage(message brokerMessage) {
		BrokerMessage = brokerMessage;
	}

	/**
	 * @brief Returns the  strategy's last open price
	 * @return lastOpenPrice
	 */
	public double getLastOpenPrice() {
		return lastOpenPrice;
	}

	/** 
	 * *@brief sets strategy's last open price
	 *  @param lastOpenPrice
	 *  @return Void. 
	 */
	public void setLastOpenPrice(double lastOpenPrice) {
		this.lastOpenPrice = lastOpenPrice;
	}

	/**
	 * @brief Returns the strategy's current shares
	 * @return currentShares
	 */
	public long getCurrentShares() {
		return currentShares;
	}

	/** 
	 * *@brief sets strategy's current shares
	 *  @param currentShares
	 *  @return Void. 
	 */
	public void setCurrentShares(long currentShares) {
		this.currentShares = currentShares;
	}

	/**
	 * @brief Returns the strategy's current position
	 * @return StrategyPosition
	 */
	public StrategyPosition getCurrentPosition() {
		return currentPosition;
	}

	/** 
	 * *@brief sets strategy's current position
	 *  @param StrategyPosition
	 *  @return Void. 
	 */
	public void setCurrentPosition(StrategyPosition currentPosition) {
		this.currentPosition = currentPosition;
	}

	/**
	 * @brief Returns the strategy's current action
	 * @return currentAction
	 */
	public Action getCurrentAction() {
		return currentAction;
	}

	/** 
	 * *@brief sets strategy's currentAction
	 *  @param Action
	 *  @return Void. 
	 */
	public void setCurrentAction(Action currentAction) {
		this.currentAction = currentAction;
	}

	/** 
	 * *@brief calculates profits/losses of a strategy
	 *  @return profit 
	 */
	public Double profit() {
		Double profit = (double) 0;
		JPATradeService s = StrategyHandler.getInstance().tradeService();
		List<Trade> list = s.getAll();
		for (Trade x : list) {
			if (x.getStrategy() == this.id) {
				if (x.getAction().equals(Action.BUY)) {
					profit -= x.getNumShares()*x.getPrice();
				} else {
					profit += x.getNumShares()*x.getPrice();
				}
			}
		}
		return profit;
	}
	
	/** 
	 * *@brief calculates return on investment of a strategy
	 *  @return ROI
	 */
	public Double roi() {
		Double investments = (double) 0;
		Double gains = (double) 0;
		JPATradeService s = StrategyHandler.getInstance().tradeService();
		List<Trade> list = s.getAll();
		for (Trade x : list) {
			if (x.getStrategy() == this.id) {
				if (x.getAction().equals(Action.BUY)) {
					investments += x.getNumShares()*x.getPrice();
				} else {
					gains += x.getNumShares()*x.getPrice();
				}
			}
		}
		return (gains-investments)/investments;
	}

}
