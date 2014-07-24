package model;

import java.util.ArrayList;
import java.util.List;

public class OASCalculator {
	
	double myOAS;
	List<SpotRate> mySpots;
	List<PrepaymentRate> myPrepays;
	Asset myAsset;
	
	int numIterations = 5000;
	double oasS = .0001; //1 basis point
	double oasF = .01;	 //100 basis points
	double oasStep = (oasF - oasS) / numIterations;
	
	public OASCalculator(List<SpotRate> s, List<PrepaymentRate> p, Asset a)	{
		mySpots = s;
		myPrepays = p;
		myAsset = a;
		calculateOAS();
	}
	
	public double calculateOAS(){
		double oas = 0;
		if(mySpots == null || myPrepays == null || myAsset == null)	{
			return oas;
		}

		oas = oasS;
		double currentOAS = oas;
		double diff = 100;
		for(int i = 0; i < numIterations; i++)	{
			double pv = calcPresentValue(currentOAS);
			if(Math.abs(pv - myAsset.currentPrice) < diff)	{
				diff = Math.abs(pv - myAsset.currentPrice);
				oas = currentOAS;
			}
			else	{
				currentOAS += oasStep;
			}
		}
		myOAS = oas;
		return oas;
	}
	
	private double calcPresentValue(double oas)		{
		double presentVal = 0;
		List<Double> discountedCashflows = calcDiscountedCashflows(mySpots, myPrepays, myAsset, oas);
		for(Double d : discountedCashflows)	{
			presentVal += d;
		}
		return presentVal;
	}
	
	/**
	 * 
	 * Note: this method is not efficient because calculations involved with cashflows are repeated
	 * @param s
	 * @param p
	 * @param a
	 * @param oas
	 * @return
	 */
	private List<Double> calcDiscountedCashflows(List<SpotRate> s, List<PrepaymentRate> p, Asset a, double oas)	{
		List<Double> discountedCashflows = new ArrayList<Double>();
		double remainingBalance = a.initBal;
		for(int t = 1; t <= a.nPayment; t++)	{
			double discountRate = 1 / Math.pow((1 + s.get(t).value + oas),t);
			
			double amort = calcAmortPayment(a, remainingBalance);
			double interest = calcInterestPayment(a, amort, remainingBalance);
			double runoff = calcRunoff(p.get(t), amort, remainingBalance);
			double cashflow = calcCashflow(amort, interest, runoff);
			
			discountedCashflows.add(cashflow * discountRate);
			
			remainingBalance = remainingBalance - amort - runoff;
		}
		return discountedCashflows;
	}
	
	private double calcCashflow(double amort, double interest, double runoff){
		return amort + interest + runoff;
	}
	
	/**
	 * Finds the amortized payment of principal
	 * based on the asset information and the remaining balance
	 * @param a
	 * @param rb
	 * @return
	 */
	private double calcAmortPayment(Asset a, double rb)	{
		if(a.payment - (rb * a.coupon) <= rb)	{
			return a.payment - (rb * a.coupon);
		}
		return rb;
	}

	/**
	 * Method to find interest payment by finding amortization
	 * and using asset info along with remaining balance
	 * @param a
	 * @param rb
	 * @return
	 */
	private double calcInterestPayment(Asset a, double amort, double rb)	{
		if(rb > 0)	{
			return a.payment - amort;
		}
		return 0;
	}
	
	/**
	 * Method to find runoff by using prepayment and finding amortization
	 * @param p
	 * @param a
	 * @param rb
	 * @return
	 */
	private double calcRunoff(PrepaymentRate p, double amort, double rb)	{
		if(rb - amort > 0)	{
			return p.value * (rb - amort);
		}
		return 0;
	}
	
	public static void main(String[] args)	{
		List<SpotRate> spots = new ArrayList<SpotRate>();
		List<PrepaymentRate> prepays = new ArrayList<PrepaymentRate>();
		Asset asset = new Asset(105, .04, 360, 100);
		OASCalculator o = new OASCalculator(spots, prepays, asset);
		o.calculateOAS();
		System.out.println(o.myOAS);
	}
	
}
