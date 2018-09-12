package magnuscapital;

import java.util.ArrayList;

import enums.StrategyType;

//import StrategyAlgos.TwoMovingAvg;

public class StrategyTest {
	public static void main(String [] args) throws Exception{
		System.out.println("Hope this works");
		StockInfo info = StockInfo.getInstance();
		
		System.out.println(info.getPricingData("http://incanada1.conygre.com:9080/prices", "AAPL", 5));

		StrategyHandler handle = StrategyHandler.getInstance();
//		TwoMovingAvg TMA1 = new TwoMovingAvg(4,3,"APPL",1000);
//		TwoMovingAvg TMA2 = new TwoMovingAvg(4,2,"APPL",1089);
		
		Strategy s1 = new Strategy("APPL",1090,StrategyType.TWOMA,4,2,0);
		Strategy s2 = new Strategy("APPL",1070,StrategyType.TWOMA,4,3,0);
		handle.handler();
		
		
		
	}

}
