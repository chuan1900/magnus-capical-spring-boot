package demo.webmvcjsp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import enums.Action;
import enums.Trigger;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import magnuscapital.Trade;
import magnuscapital.JPAStrategyService;
// import magnuscapital.newStrategy;
import magnuscapital.JPATradeService;
import magnuscapital.StockInfo;
import magnuscapital.Strategy;
import magnuscapital.StrategyHandler;
import enums.StrategyType;

@CrossOrigin(origins = "http://localhost:4200")
@Controller
public class PricingController {
	private EntityManagerFactory factory = Persistence.createEntityManagerFactory("PersistenceTest");
	private EntityManager em = factory.createEntityManager();

	private JPATradeService x = new JPATradeService(factory);
	private ArrayList<Trade> trades= new ArrayList<>();
	
	private EntityManagerFactory factory2 = Persistence.createEntityManagerFactory("PersistenceTest");
	private EntityManager em2 = factory2.createEntityManager();

	private JPAStrategyService y = new JPAStrategyService(factory2);
	private ArrayList<Strategy> strategies= new ArrayList<>();

	@RequestMapping(value = "/dashboard/home")
	@ResponseBody
	public String getStock() {
		System.out.println("INSIDE GET STOCK FN");
		String prices = "TEST STRING";
		System.out.println(prices);
		return "{\"name\":\"John\", \"age\":30}";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/trades")
	@ResponseBody
	public ArrayList<Trade> tradeList() {

		System.out.println("INSIDE TRADE LIST FN");
		//Trade trade = new Trade("LPK", 1000, 1.55, Action.BUY, StrategyType.BOLLINGER, 1);
		//x.add(trade);
		System.out.println("trades count: " + x.getCount());
		trades = (ArrayList<Trade>) x.getAll();
		return trades;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/strategies")
	@ResponseBody
	public ArrayList<Strategy> strategyList() {
		System.out.println("INSIDE STRATEGY LIST FN");
		strategies = (ArrayList<Strategy>)y.getAll();
		return strategies;
	}

	// @CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(method = RequestMethod.POST, value = "/runTWOMA")
	@ResponseBody
	public String runTWOMA(@RequestBody String params) throws IOException {
		System.out.println("RUN TWOMA STRATEGY");
		
		String[] value = params.split("_");
		String ticker = value[0];
		long shares = Integer.parseInt(value[1]);
		int longPeriod = Integer.parseInt(value[2]);
		int shortPeriod = Integer.parseInt(value[3]);
		
		StockInfo.getInstance().getPricingData("http://incanada1.conygre.com:9080/prices", ticker, 1);

		Strategy s1 = new Strategy(ticker, shares, StrategyType.TWOMA, longPeriod, shortPeriod, 0);
		StrategyHandler.getInstance().handler();

		StrategyHandler.getInstance().setT(Trigger.CONTINUE);
		return "{\"name\":\"twoMA\", \"age\":30}";
	}

	// @CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(method = RequestMethod.POST, value = "/runBB")
	@ResponseBody
	public void runBB(@RequestBody String params) throws IOException {
		
		System.out.println("RUN BB STRATEGY");
		
		String[] value = params.split("_");
		String ticker = value[0];
		long shares = Integer.parseInt(value[1]);
		int longPeriod = 0;
		int shortPeriod = 0;
		int period = Integer.parseInt(value[4]);
		StockInfo.getInstance().getPricingData("http://incanada1.conygre.com:9080/prices", ticker, 1);
		
		
		Strategy s1 = new Strategy(ticker, shares, StrategyType.BOLLINGER, longPeriod, shortPeriod, period);

		StrategyHandler.getInstance().handler();
		StrategyHandler.getInstance().setT(Trigger.CONTINUE);
	}
	
	//@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(method = RequestMethod.GET, value = "/stopStrategy")
	@ResponseBody
	public String stopStrategy() {

		System.out.println("Stop STRATEGY");
//
		StrategyHandler handle = StrategyHandler.getInstance();
//		StrategyType stype = StrategyType.TWOMA;
//		period = 0;
////		longPeriod = 7;
////		shortPeriod = 3;
//		Strategy s1 = new Strategy(ticker, shares, stype, longPeriod, shortPeriod, period);
		
		handle.handler();
		handle.setT(Trigger.USER_INTERRUPT);
		return "{\"name\":\"stop\", \"age\":30}";
	}

	public static void main(String[] args) {
		System.out.println("Runnin main");
		StrategyHandler hanlder = StrategyHandler.getInstance();

		PricingController pc = new PricingController();

		EntityManagerFactory factory1 = Persistence.createEntityManagerFactory("PersistenceTest");
		EntityManager em1 = factory1.createEntityManager();

		JPATradeService x = new JPATradeService(factory1);
		Trade trade = new Trade("NULL", 0, 0, Action.NONE, StrategyType.BOLLINGER, 1);
		trade.setId(1);
		x.add(trade);

		pc.tradeList();
		System.out.println("trade list: " + pc.tradeList().toString());

	}
}
