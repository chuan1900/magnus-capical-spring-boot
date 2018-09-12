/**
 * @file StrategyHandler.java
 * @Author Sarthak Bansal
 * @date July 25, 2018
 * @brief singleton handler for all strategies
 * 
 * Instantiates service classes
 * Sends triggers for strategies to execute
 * Keeps a list of all strategies and trades not yet approved/rejected
 * Manages Ids of strategies and trades for db
 *  
 */


package magnuscapital;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import enums.StrategyType;
import enums.Trigger;

public class StrategyHandler {
	private final static Logger LOGGER = Logger.getLogger(StrategyHandler.class.getName());
	private FileHandler fh;
	private ArrayList<Trade> trades;
	private Queue<Strategy> strategies;
	private static StrategyHandler singleInstance;
	private int correlationId;
	private int strategyId;
	private Trigger t;
	
	private EntityManagerFactory factory;
	private EntityManager em;
	private StrategyHandler handler;
	private JPATradeService serviceT;
	private JPAStrategyService serviceS;

	private StrategyHandler() {
		try {
			fh = new FileHandler("StrategyHandler.log");
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
		
		trades = new ArrayList<Trade>();
		strategies = new LinkedList<Strategy>();
		factory = Persistence.createEntityManagerFactory("PersistenceTest");
		em = factory.createEntityManager();
		serviceT = new JPATradeService(factory);
		serviceS = new JPAStrategyService(factory);
		
		if(serviceT.getCount() == 0) {
			correlationId = 1;
		} else {
			correlationId = serviceT.getMaxID() + 1;
		}
		if(serviceS.getCount() == 0) {
			strategyId = 1;
		} else {
			strategyId = serviceS.getMaxID() + 1;
		}
		
		LOGGER.info("starting trade Id = " + correlationId);
		LOGGER.info("starting strategy Id = " + strategyId);
	}
	
	/**
	 * @brief Returns the singleton
	 * @return StrategyHandler
	 */
	public static StrategyHandler getInstance() {
		if (singleInstance == null) {
			singleInstance = new StrategyHandler();
		}
		return singleInstance;
	}
	
	/**
	 * @brief Returns the TradeService class
	 * @return JPATradeService
	 */
	public JPATradeService tradeService() {
		return serviceT;
	}
	
	/**
	 * @brief Returns the StrategyService class
	 * @return JPAStrategyService
	 */
	public JPAStrategyService strategyService() {
		return serviceS;
	}
	
	/**
	 * @brief Returns the trigger indicating next stratey's action
	 * @return trigger
	 */
	public Trigger getT() {
		return t;
	}

	/** 
	 * *@brief Sets the trigger indicating next action
	 *  @param Trigger
	 *  @return Void. 
	 */
	public void setT(Trigger t) {
		this.t = t;
	}

	/**
	 * @brief Returns a Continue Trigger
	 * @return Trigger.CONTINUE
	 */
	public Trigger getTrigger() {
		return Trigger.CONTINUE;
	}

	/** 
	 * *@brief increments id for next trade
	 *  @return Void. 
	 */
	public void incCorelationid() {
		correlationId++;
	}
	
	/** 
	 * *@brief increments id for next strategy
	 *  @return Void. 
	 */
	public void incStrategyId() {
		strategyId++;
	}
	
	/**
	 * @brief Returns the next id to assign a strategy
	 * @return strategyId
	 */
	public int getStrategyId() {
		return strategyId;
	}
	
	/**
	 * @brief Returns the next id to assign a trade/request
	 * @return correlationId
	 */
	public int getCorrelationId() {
		return correlationId;
	}

	/**
	 * @brief Returns the list of trades not yet responded to
	 * @return trades
	 */
	public List<Trade> getTrades() {
		return trades;
	}
	
	/**
	 * @brief Returns the list of all strategies
	 * @return strategies
	 */
	public Queue<Strategy> getStrategies(){
		return strategies;
	}
	
	/*
	public String getTicker() {
		return "AAPL";
	}
	*/
	
	/** 
	 * *@brief adds a strategy to list of strategies
	 *  @param Strategy
	 *  @return Void. 
	 */
	public void addToList(Strategy s) {
		strategies.add(s);
	}

	/** 
	 * *@brief triggers continuation of strategy depending on user action/threshold
	 *  @return Void. 
	 */
	public void handler() throws IOException {
		Trigger t;
		ArrayList<Double>lowPriceList ;
		ArrayList<Double>highPriceList ;
		do {
			t = getTrigger();
			lowPriceList = StockInfo.getInstance().listLowPrices(getTicker());
			highPriceList = StockInfo.getInstance().listHighPrices(getTicker());
			for (Strategy s : strategies) {
				s.execute(lowPriceList, highPriceList);
			}		
			try {
				System.out.println("sleeping");
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (t.equals(Trigger.CONTINUE));

		if (t.equals(Trigger.THRESHOLD)) {
			System.out.println("Thersold limit reached");
			return;
		} else if (t.equals(Trigger.USER_INTERRUPT)) {
			LOGGER.info("User interrupted");
			return;
		}
	}


}
