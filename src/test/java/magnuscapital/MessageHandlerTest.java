/**
 * @file MessageHandlerTest.java
 * @Author Nicole Chow
 * 
 * @date July 25, 2018
 * @brief Test class for StockInfo
 * 
 * Uses Junit to test integration of sending messages to ActiveMQ. 
 * Test to receive not implemented due to lower priorities from time/mocking complexity constraints
 *  
 */


package magnuscapital;

import jms.MessageHandler;

import static org.junit.Assert.*;
import java.io.FileNotFoundException;
import java.util.*;
import org.junit.Test;

import enums.Action;
import enums.StrategyType;

public class MessageHandlerTest {

	private StrategyHandler handler = StrategyHandler.getInstance();

	/** 
	 * *@brief checks that message is sent correctly
	 *  @return Void. 
	 */
	@Test
	public void sendMsgSuccess() throws Exception {
		Trade t = new Trade("TESTBUY", 2000, 88.0, Action.BUY, StrategyType.TWOMA, 1);
		handler.getTrades().add(t);
		assertTrue(MessageHandler.initiateTrade(t, "spring-beans-test.xml"));
	}

}
