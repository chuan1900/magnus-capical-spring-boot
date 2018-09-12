/**
 * @file StockInfoTest.java
 * @Author Nivedha Mathiarasu
 * @Author Nicole Chow
 * 
 * @date July 25, 2018
 * @brief Test class for StockInfo
 * 
 * Uses Junit to test StockInfo. The last 2 tests are integration tests with  the price service
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


public class StockInfoTest {
	private StockInfo info;
	
	/** 
	 * *@brief populates map with MFST info
	 *  @return Void. 
	 */
	@Before
	public void setUp() {
		info = StockInfo.getInstance();
		info.populateMap("MSFT", "2018-07-25 13:24:15.514,55.1902,55.3929,54.1098,54.3071,54313");
		info.populateMap("MSFT", "2018-07-25 13:24:30.514,54.9235,56.0198,54.9235,55.0730,83870");
		info.populateMap("MSFT", "2018-07-25 13:24:45.514,55.5860,55.9869,54.8536,55.6832,47325");
		
	}
	
	/** 
	 * *@brief checks that map was populated correctly
	 *  @return Void. 
	 */
	@Test
	public void populateSuccess() {
		assertEquals(3, info.queryStock("MSFT").size());
		assertEquals(3, info.listHighPrices("MSFT").size());
		assertEquals(3, info.listLowPrices("MSFT").size());
	}
	
	/** 
	 * *@brief check that a nonexistent ticker return empty lists
	 *  @return Void. 
	 */
	@Test
	public void ticketDNE() {
		assertNull(info.queryStock("AAAA"));
		assertEquals(0, info.listHighPrices("AAAA").size());
		assertEquals(0, info.listLowPrices("AAAA").size());
	}
	
	/** 
	 * *@brief integration test that grabs valid info from site
	 *  @return Void. 
	 */
	@Test 
	public void loadSuccess() throws Exception {
		assertTrue(info.getPricingData("http://incanada1.conygre.com:9080/prices", "AAPL", 2));
		assertNotNull(info.queryStock("AAPL"));
	}
	
	/** 
	 * *@brief integration test that grabs invalid info from site
	 *  @return Void. 
	 */
	@Test(expected=Exception.class) //integration
	public void loadFail() throws Exception {
		info.getPricingData("http://incanada1.conygre.com:9080", "GOOG", 2);

	}



}
