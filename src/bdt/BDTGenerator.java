package bdt;
public class BDTGenerator {
	public class TreeNode {
		public double myValue;
		public TreeNode down; // holds smaller tree nodes
		public TreeNode up; // holds larger tree nodes

		public TreeNode(double val) { 
			myValue = val; 
			down = null;
			up = null;
		}
		
	}
	
	//Inputs
	double periodL 		= .5;
	int term 			= 4;
	int numPeriods		= (int)(term/periodL);
	double[] initRate 	=	{	.06,	.072,	.0815,	.08836	};
	double[] initVol	=	{	.15,	.15,	.15,	.15		};
	double[] zSpread	= new double[numPeriods];
	double[] zCpnRate	= new double[numPeriods];
	
	/**
	public TreeNode initTree()	{
		TreeNode root = null;
		//Initialize the root node
		if(initRate.length > 0){
			root = new TreeNode(initRate[0]);
		}
		TreeNode current = new TreeNode(root.myValue);
		//Nested forloops to populate additional nodes of the tree
		for(int n = 2; n <= numPeriods; n++)	{
			double z = zSpread[n-1];
			double r = initRate[n-1];
			TreeNode nextLeft = current.left;
			TreeNode nextRight = current.right;
			for(int k = 1; k <= n; k++)	{
				current.left = new TreeNode(calcInitNodeVal(n,k,z,r));
			}
		}
		return root;
	}
	*/
	
	//Start curlevel at 1
	public TreeNode initTree(int curLevel)	{
		TreeNode root = null;
		double z;
		double r;
		if(numPeriods == 1)	{
			return new TreeNode(initRate[0]);
		}
		else if(numPeriods == 2)	{
			z = zSpread[1];
			r = initRate[1];
			root = new TreeNode(initRate[0]);
			root.down = new TreeNode(calcInitNodeVal(curLevel,1,z,r));
			root.up = new TreeNode(calcInitNodeVal(curLevel,2,z,r));
			return root;
		}
		
		return root;
	}
	private double calcInitNodeVal(int n, int k, double z, double r)	{
		double sum = 0;
		for(int i = 0; i < n; i++)	{
			sum = sum + getCombin(n-1, i) * Math.pow(z, i);
		}
		double numerator = Math.pow(2, n-1) * r;
		
		return (numerator/sum) * Math.pow(z, k-1);
	}
	
	private double getCombin(int n, int k){
		return getFactorial(n)/(getFactorial(k)*getFactorial(n-k));
	}
	
	private double getFactorial(int n)	{
		double output = 1;
		for(int i = 1; i <= n; i++)	{
			output *= i;
		}
		return output;
	}
	
}
