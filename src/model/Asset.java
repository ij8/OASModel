package model;

public class Asset {
	double currentPrice;
	double coupon;
	double nPayment;
	double initBal;
	double payment;
	
	public Asset(double cp, double c, double n, double i)	{
		currentPrice = cp;
		coupon = c;
		nPayment = n;
		initBal = i;
		calcPayment(c,n,i);
	}
	
	/**
	 * This is a method to calculate the payment amount due each period of
	 * the asset without optionality
	 * @param c = coupon rate
	 * @param n = number of payments
	 * @param i = initial balance
	 * @return
	 */
	private double calcPayment(double c, double n, double i)	{
		double p = 0;
		double numerator = i;
		double denominator = 0;
		for(int t = 1; t <= n; t++)	{
			denominator = denominator + 1 / Math.pow(1 + c, t);
		}
		p = numerator/denominator;
		payment = p;
		return p;
	}
	
}
