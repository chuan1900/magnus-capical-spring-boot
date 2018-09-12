/**
 * @file JPATradeServiceTest.java
 * @Author Nicole Chow
 * @date July 25, 2018
 * @brief Test class for JPATradeService
 * 
 * Uses Junit to for integration testing of Trades in persistence.
 *  
 */

package magnuscapital;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import magnuscapital.Trade;
import magnuscapital.JPATradeService;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import enums.Action;
import enums.StrategyType;
import exceptions.NotFoundException;

public class JPATradeServiceTest {
	private StrategyHandler handler;

	private Strategy strategy;
	JPATradeService service;
	
	
	/** 
	 * *@brief instantiates strategy handler and JPATradeService
	 *  @return Void. 
	 */
	@Before
	public void setManagers(){
		handler = StrategyHandler.getInstance();
		service = handler.tradeService();
	}

	/** 
	 * *@brief successfully adds a trade
	 *  @return Void. 
	 */
	@Test
	public void addTrade() throws Exception {
		Trade trade = new Trade("LPK", 1000, 1.55, Action.BUY, StrategyType.BOLLINGER, 1);
		trade.setId(1);
		service.add(trade);
		assertNotNull(service.getByID(1));
		assertTrue(service.removeByID(1));
	}
	
	/** 
	 * *@brief unsuccessfully adds a trade with a duplicate Id
	 *  @return Void. 
	 */
	@Test(expected=javax.persistence.PersistenceException.class)
	public void duplicateId() throws Exception{
		Trade trade = new Trade("LPK", 1000, 1.55, Action.BUY, StrategyType.BOLLINGER, 1);
		trade.setId(1);
		service.add(trade);
		Trade trade2 = new Trade("LPK", 1000, 1.55, Action.BUY, StrategyType.BOLLINGER,1);
		trade2.setId(1);
		service.add(trade2);
		assertTrue(service.removeByID(1));
	}

	/** 
	 * *@brief successfully removes a trade by its Id
	 *  @return Void. 
	 */
	@Test(expected=NotFoundException.class)
	public void removeTradeID() throws Exception {
		Trade trade = new Trade("TBL", 1000, 1.67, Action.SELL, StrategyType.TWOMA, 1);
		trade.setId(2);
		service.add(trade);
		assertNotNull(service.getByID(2));
		assertTrue(service.removeByID(2));
		service.getByID(2);
	}
	
	/** 
	 * *@brief unsuccessfully removes a trade by its Id
	 *  @return Void. 
	 */
	@Test(expected = NotFoundException.class)
	public void removeTradeDNE() throws Exception{
		Trade trade = new Trade("LPK", 1000, 1.55, Action.BUY, StrategyType.BOLLINGER, 1);
		trade.setId(3);
		service.remove(trade);
	}
	
	/** 
	 * *@brief successfully removes a trade by the trade itself
	 *  @return Void. 
	 */
	@Test(expected=NotFoundException.class)
	public void removeTrade() throws Exception {
		Trade trade = new Trade("TBL", 1000, 1.67, Action.SELL, StrategyType.TWOMA, 1);
		trade.setId(2);
		service.add(trade);
		assertNotNull(service.getByID(2));
		service.remove(trade);
		service.getByID(2);
	}

	/** 
	 * *@brief unsuccessfully removes a trade by the trade itself
	 *  @return Void. 
	 */
	@Test(expected = NotFoundException.class)
	public void removeIdDNE() throws Exception{
		service.removeByID(3);
	}

	/** 
	 * *@brief unsuccessfully finds a nonexistent trade by Id
	 *  @return Void. 
	 */
	@Test(expected = NotFoundException.class)
	public void noId() throws Exception {
		service.getByID(3);
	}

	/** 
	 * *@brief successfully finds the maximum Id a trade has been given
	 *  @return Void. 
	 */
	@Test
	public void maxTradeId() throws Exception {
		Trade trade99 = new Trade("TBL", 1000, 1.67, Action.SELL, StrategyType.TWOMA,1);
		trade99.setId(99);
		service.add(trade99);
		assertEquals(99, service.getMaxID());
		assertTrue(service.removeByID(99));
	}
	
	/** 
	 * *@brief successfully updates a trade
	 *  @return Void. 
	 */
	@Test
	public void update() throws Exception{
		Trade trade = new Trade("TBL", 1000, 1.67, Action.SELL, StrategyType.TWOMA,1);
		trade.setId(4);
		service.add(trade);
		Trade x = service.getByID(4);
		x.setStrategyType(StrategyType.BOLLINGER);
		service.update(x);
		assertEquals(StrategyType.BOLLINGER, service.getByID(4).getStrategyType());
		assertTrue(service.removeByID(4));
	}
	
	/** 
	 * *@brief tests all trades in db can be returned in a list
	 *  @return Void. 
	 */
	@Test
	public void tradeCount() throws Exception {
		assertEquals(service.getCount(), service.getAll().size());
	}

}
