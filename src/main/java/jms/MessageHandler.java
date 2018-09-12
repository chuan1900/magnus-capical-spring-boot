/**
 * @file MessageHandler.java
 * @Author Nicole Chow
 * @date July 25, 2018
 * @brief Message handler for sending and receiving
 * 
 * Sends a trade request to OrderBroker
 * Parses received request replies, stores trades and alerts a strategy of the response
 */

package jms;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Queue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import enums.Action;
import enums.message;
import magnuscapital.JPATradeService;
import magnuscapital.Strategy;
import magnuscapital.StrategyHandler;
import magnuscapital.Trade;

public class MessageHandler implements MessageListener {
	private final static Logger LOGGER = Logger.getLogger(MessageHandler.class.getName());
	private FileHandler fh;

	/**
	 * *@brief receives response replies
	 * 
	 * @param message
	 *            from broker
	 * @return Void.
	 */
	public void onMessage(Message m) {
		try {
			fh = new FileHandler("MessageHandler.log");
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		LOGGER.setUseParentHandlers(false);
		LOGGER.addHandler(fh);
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);
		LOGGER.info("logging for message replies instantiated");

		try {
			JPATradeService service = StrategyHandler.getInstance().tradeService();
			TextMessage msg = (TextMessage) m;

			LOGGER.info("Reply: " + msg.getText());

			Trade t = null;
			for (Trade x : StrategyHandler.getInstance().getTrades()) {
				if (x.getId() == Integer.parseInt(msg.getJMSCorrelationID())) {
					t = x;
				}
			}

			if (t != null) {
				Strategy s = null;
				for (Strategy x : StrategyHandler.getInstance().getStrategies()) {
					if (t.getStrategy() == x.getId()) {
						s = x;
					}
				}
				if (msg.getText().contains("PARTIALLY")) {
					LOGGER.info("PARTIALLY FILLED");
					int start = msg.getText().toString().indexOf("<size>") + 6;
					int end = msg.getText().toString().indexOf("</size>");
					int size = Integer.parseInt(msg.getText().toString().substring(start, end));
					t.setNumShares(size);
					service.add(t);
					s.setBrokerMessage(message.PARTIAL_FILL);
					s.setCurrentShares(size);
					LOGGER.info("Partially FILLED from broker");
				} else if (msg.getText().contains("REJECTED")) {
					LOGGER.info("REJECTED");
					s.setBrokerMessage(message.DECLINED);
					LOGGER.info("rejected from broker");
				} else {
					LOGGER.info("FILLED");
					service.add(t);
					s.setBrokerMessage(message.APPROVED);
				}
				LOGGER.info(t.getId() + " " + t.getAction().toString() + " " + t.getTicker() + " " + t.getPrice() + " "
						+ t.getNumShares() + " " + t.getStrategyType().toString() + "\n\n");
				int i = StrategyHandler.getInstance().getTrades().indexOf(t);
				StrategyHandler.getInstance().getTrades().remove(i);
				s.MessageInterpreter();

			} else {
				LOGGER.info("NOT FOUND\n\n");
			}
		} catch (ClassCastException e) {
			LOGGER.log(Level.WARNING, "message unable to convert to TextMessage ", e);
			e.printStackTrace();
		} catch (JMSException e) {
			LOGGER.log(Level.WARNING, "JMS parsing exception ", e);
			e.printStackTrace();
		}
	}

	/**
	 * *@brief sends trade requests triggered by strategies
	 * @param requested trade, xml config file
	 * @return true is message sends, else false
	 */
	public static boolean initiateTrade(final Trade t, String appContext) {
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext(appContext);
			Destination destination = context.getBean("destination", Destination.class);
			final Destination replyTo = context.getBean("replyTo", Destination.class);
			JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("messageSender");

			jmsTemplate.send(destination, new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					String text = "";
					if (t.getAction().equals(Action.BUY)) {
						text = "<trade>\r\n<buy>true</buy>\r\n<id>" + Integer.toString(t.getId()) + "</id>\r\n<price>"
								+ Double.toString(t.getPrice()) + "</price>\r\n<size>" + Long.toString(t.getNumShares())
								+ "</size>\r\n<stock>" + t.getTicker() + "</stock>\r\n<whenAsDate>"
								+ "2018-07-31T11:33:22.801-04:00" + "</whenAsDate>\r\n</trade>";
					} else {
						text = "<trade>\r\n<buy>false</buy>\r\n<id>" + Integer.toString(t.getId()) + "</id>\r\n<price>"
								+ Double.toString(t.getPrice()) + "</price>\r\n<size>" + Long.toString(t.getNumShares())
								+ "</size>\r\n<stock>" + t.getTicker() + "</stock>\r\n<whenAsDate>"
								+ "2018-07-31T11:33:22.801-04:00" + "</whenAsDate>\r\n</trade>";
					}
					TextMessage message = session.createTextMessage(text);
					message.setJMSReplyTo(replyTo);
					message.setJMSCorrelationID(Integer.toString(t.getId()));
					LOGGER.info("Message body:\n" + message.getText());
					return message;
				}
			});
			LOGGER.info("sent SUCCESS");
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "sent FAIL", e);
			e.printStackTrace();
			return false;
		}
	}

}
