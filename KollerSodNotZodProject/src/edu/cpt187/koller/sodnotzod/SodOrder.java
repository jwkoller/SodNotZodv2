package edu.cpt187.koller.sodnotzod;

import java.util.Random;

public class SodOrder 
{
	private final double TAX_RATE = 0.075;
	private final int DISCOUNT_ARRAY_SIZE = 3;
	private final int RECEIPT_FIELDS = 5;
	private final int MAX_RECEIPT_ITEMS = 100;
	private final int RESET_VALUE = 0;
	private String howMany = "";
	private String[] itemSelection;
	private String[][] orderReceipt = new String[MAX_RECEIPT_ITEMS][RECEIPT_FIELDS];
	private int receiptItemCount = 0;
	private double discountRateSelection = 0.0;
	private String discountRateNameSelect = "";
	private String prizeName = "";
	private int[] discountCounts = new int [DISCOUNT_ARRAY_SIZE];
	private Random prizeGenerator = new Random();

	public void setDiscountRate(char borrowedDiscountMenu, String[][] borrowedDiscounts)		//Set discount rate from user input
	{
		if (borrowedDiscountMenu == 'A')		//Member discount
		{
			discountRateNameSelect = borrowedDiscounts[0][0];
			discountRateSelection = Double.parseDouble(borrowedDiscounts[1][0]);
			discountCounts[0]++;
		}
		else if (borrowedDiscountMenu == 'B')	//Senior discount
		{
			discountRateNameSelect = borrowedDiscounts[0][1];
			discountRateSelection = Double.parseDouble(borrowedDiscounts[1][1]);
			discountCounts[1]++;
		}
		else	//no discount
		{
			discountRateNameSelect = borrowedDiscounts[0][2];
			discountRateSelection = Double.parseDouble(borrowedDiscounts[1][2]);
			discountCounts[2]++;
		}
	}
	
	public void setItemSelection(int borrowedItemIndex, String[][] borrowedItemRecords)	//set item details per user selection
	{	
		itemSelection = new String[borrowedItemRecords[0].length];
	
		for(int localRecordField = 0; localRecordField < borrowedItemRecords[0].length; localRecordField++)
		{
			itemSelection[localRecordField] = borrowedItemRecords[borrowedItemIndex][localRecordField];
		}
	}
	
	public void setAddToOrderReceipt()
	{
		for(int localField = 0; localField < itemSelection.length; localField++)
		{
			orderReceipt[receiptItemCount][localField] = itemSelection[localField];
		}
		
		orderReceipt[receiptItemCount][3] = howMany;
		orderReceipt[receiptItemCount][4] = String.valueOf(getTotalItemCost());
		receiptItemCount++;
	}
	
	public void setResetForNewReceipt()
	{
		receiptItemCount = RESET_VALUE;
	}
	
	//Set how many
	public void setHowMany(String borrowedHowMany)
	{
		howMany = borrowedHowMany;
	}
	
	public void setDecreaseInStock(Inventory borrowedCurrentInventory)
	{
		borrowedCurrentInventory.setReduceStock(orderReceipt, receiptItemCount);
	}
	
	public void setPrizeName(String[] borrowedPrizeNames)
	{
		prizeName = borrowedPrizeNames[getRandomNumber(borrowedPrizeNames)];
	}
	
	public int getRandomNumber(String[] borrowedPrizeNames)
	{
		return prizeGenerator.nextInt(borrowedPrizeNames.length);
	}
	
	public double getDiscountRateSelect() 
	{
		return discountRateSelection;
	}
	
	public String getDiscountRateNameSelect()
	{
		return discountRateNameSelect;
	}
	
	public double getDiscountAmtTotal()
	{
		return getSubTotal() * discountRateSelection;
	}
	
	public String[] getItemSelection()
	{
		return itemSelection;
	}
	
	public String[][] getOrderReceipt()
	{
		return orderReceipt;
	}
	
	public int getReceiptItemCount()
	{
		return receiptItemCount;
	}

	public String getHowMany()
	{
		return howMany;
	}
	
	public double getTotalItemCost()
	{
		return Integer.parseInt(howMany) * Double.parseDouble(itemSelection[2]);
	}

	//Return sub total
	public double getSubTotal()
	{
		double localSubTotal = 0.0;
		
		for(int localIndex = 0; localIndex < receiptItemCount; localIndex++)
		{
			localSubTotal += Double.parseDouble(orderReceipt[localIndex][4]);
		}
		
		return localSubTotal;
	}
	
	//Return tax amount
	public double getTaxAmount()
	{
		return (TAX_RATE * getSubTotal());
	}//End Return tax amount
	
	//Return total cost of order
	public double getTotalCost()
	{
		return ((getSubTotal() - getDiscountAmtTotal()) + getTaxAmount());
	}//end of total cost of order
	
	//return prize name
	public String getPrizeName()
	{
		return prizeName;
	}
	//Return discount count
	public int[] getDiscountCounts()
	{
		return discountCounts;
	}
	
	public boolean getInstockFlag(String[][] borrowedItemRecords, int borrowedItemIndex)
	{
		if(Integer.parseInt(howMany) <= Integer.parseInt(borrowedItemRecords[borrowedItemIndex][3]))
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
	
}//End of SodOrder Class