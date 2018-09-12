/**
 * @file JPATradeServiceTest.java
 * @Author Nicole Chow
 * @date July 25, 2018
 * @brief Test class for JPAStrategyService
 * 
 * Uses Junit to for integration testing of Strategy in persistence.
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
import exceptions.NotFoundException;

public class JPAStrategyServiceTest {
	private StrategyHandler handler;
	private JPAStrategyService service;
	
	/** 
	 * *@brief instantiates strategy handler and JPAStrategyService
	 *  @return Void. 
	 */
	@Before
	public void setManagers(){
		handler = StrategyHandler.getInstance();
		service = handler.strategyService();
	}

	/** 
	 * *@brief successfully adds a strategy
	 *  @return Void. 
	 */
	@Test
	public void addStrategy() throws Exception {
		Strategy strategy = new Strategy();
		strategy.setId(10);
		service.add(strategy);
		assertNotNull(service.getByID(10));
		assertTrue(service.removeByID(10));
	}
	
	/** 
	 * *@brief unsuccessfully adds a strategy
	 *  @return Void. 
	 */
	@Test(expected=javax.persistence.PersistenceException.class)
	public void duplicateId() throws Exception{
		Strategy strategy = new Strategy();
		strategy.setId(1);
		service.add(strategy);
		Strategy strategy2 = new Strategy();
		strategy2.setId(1);
		service.add(strategy2);
		assertTrue(service.removeByID(1));
	}

	/** 
	 * *@brief successfully removes a strategy by its Id
	 *  @return Void. 
	 */
	@Test(expected=NotFoundException.class)
	public void removeStrategyID() throws Exception {
		Strategy strategy = new Strategy();
		strategy.setId(2);
		service.add(strategy);
		assertNotNull(service.getByID(2));
		service.removeByID(2);
		service.getByID(2);
	}
	
	/** 
	 * *@brief unsuccessfully removes a strategy by its Id
	 *  @return Void. 
	 */
	@Test(expected = NotFoundException.class)
	public void removeStrategyDNE() throws Exception{
		Strategy strategy = new Strategy();
		strategy.setId(3);
		service.remove(strategy);
	}
	
	/** 
	 * *@brief successfully removes a strategy by strategy itself
	 *  @return Void. 
	 */
	@Test(expected=NotFoundException.class)
	public void removeStrategy() throws Exception {
		Strategy strategy = new Strategy();
		strategy.setId(2);
		service.add(strategy);
		assertNotNull(service.getByID(2));
		service.remove(strategy);
		service.getByID(2);
	}

	/** 
	 * *@brief unsuccessfully removes a strategy by strategy itself
	 *  @return Void. 
	 */
	@Test(expected = NotFoundException.class)
	public void removeIdDNE() throws Exception{
		service.removeByID(3);
	}

	/** 
	 * *@brief unsuccessfully finds a nonexistent strategy by Id
	 *  @return Void. 
	 */
	@Test(expected = NotFoundException.class)
	public void noId() throws Exception {
		service.getByID(3);
	}

	/** 
	 * *@brief successfully finds the maximum Id a strategy has been given
	 *  @return Void. 
	 */
	@Test
	public void maxTradeId() throws Exception {
		Strategy strategy99 = new Strategy();
		strategy99.setId(99);
		service.add(strategy99);
		assertEquals(99, service.getMaxID());
		assertTrue(service.removeByID(99));
	}
	
	/** 
	 * *@brief successfully updates a strategy
	 *  @return Void. 
	 */
	@Test
	public void update() throws Exception{
		Strategy strategy = new Strategy();
		strategy.setId(4);
		service.add(strategy);
		Strategy x = service.getByID(4);
		x.setTicker("CHG");
		service.update(x);
		assertEquals("CHG", service.getByID(4).getTicker());	
		assertTrue(service.removeByID(4));
	}
	
	/** 
	 * *@brief tests all strategies in db can be returned in a list
	 *  @return Void. 
	 */
	@Test
	public void strategyCount() throws Exception {
		assertEquals(service.getCount(), service.getAll().size());
	}

}
