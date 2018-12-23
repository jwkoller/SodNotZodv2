package edu.cpt187.koller.sodnotzod;

import java.util.Scanner;
import java.io.FileInputStream;
import java.io.IOException;

public class Inventory 
{
	private final int MAX_ITEMS = 100;
	private final int MAX_FIELDS_INPUT_FILE = 4;
	private final int MAX_FIELDS_MASTER_FILE = 5;
	private final int RESET_VALUE = 0;
	private final String[] PRIZE_NAMES = {"Coffee Maker", "Fish", "Toaster", "Empty Box", "Puppy", "Kitten"};
	private final String[][] DISCOUNTS = 
			{
				{"S.N.Z. Member", "Senior Citizen", "No Discount"},
				{"0.15", "0.25", "0.0"}
			};
	private String[][] itemRecords;
	private int[] inventorySoldCounts;
	private int itemIndex = 0;
	private int itemRecordCount = 0;
	
	public void setReduceStock(String[][] borrowedOrderReceipt, int borrowedReceiptItemCount)	//reduce inventory in 2D array
	{
		int localID = 0;
		
		for(int localSold = 0; localSold < borrowedReceiptItemCount; localSold++)
		{
			while(localID < itemRecordCount)
			{
				if(borrowedOrderReceipt[localSold][0].equals(itemRecords[localID][0]))
				{
					itemRecords[localID][3] = String.valueOf(Integer.parseInt(itemRecords[localID][3]) - Integer.parseInt(borrowedOrderReceipt[localSold][3]));
					setInventorySoldCounts(localID, Integer.parseInt(borrowedOrderReceipt[localSold][3]));
					localID = itemRecordCount;
				}
				else
				{
					localID++;
				}
			}
			localID = 0;
		}
	}
	
	public void setInventorySoldCounts(int borrowedLocalID, int borrowedHowMany)
	{
		inventorySoldCounts[borrowedLocalID] += borrowedHowMany;
	}
	
	public void setLoadItemsFromFile(String borrowedFileName)	//load file from user input
	{	
		itemRecords = new String[MAX_ITEMS][MAX_FIELDS_INPUT_FILE];
		inventorySoldCounts = new int[MAX_ITEMS];
		
		try
		{
			Scanner inputFile = new Scanner(new FileInputStream(borrowedFileName));
			
			for(itemRecordCount = RESET_VALUE; inputFile.hasNext() && itemRecordCount < MAX_ITEMS; itemRecordCount++)
			{
				for(int localColumn = 0; localColumn < MAX_FIELDS_INPUT_FILE; localColumn++)
				{
					itemRecords[itemRecordCount][localColumn] = String.valueOf(inputFile.next());
				}
			}

			inputFile.close();
			setItemIDBubbleSort();
		}
		catch (IOException ex)
		{
			itemRecordCount = -1;
		}
	}
	
	public void setLoadItemFromFile(String borrowedFileName, int borrowedRecordSize)	//load master file for final report
	{
		itemRecords = new String[borrowedRecordSize][MAX_FIELDS_MASTER_FILE];
		
		try
		{
			Scanner inputFile = new Scanner(new FileInputStream(borrowedFileName));
			
			for(itemRecordCount = RESET_VALUE; itemRecordCount < borrowedRecordSize && itemRecordCount < MAX_ITEMS; itemRecordCount++)
			{
				for(int localColumn = 0; localColumn < MAX_FIELDS_MASTER_FILE; localColumn++)
				{
					itemRecords[itemRecordCount][localColumn] = String.valueOf(inputFile.next());
				}
			}

			inputFile.close();
		}
		catch(IOException ex)
		{
			itemRecordCount = -1;
		}
	}
	
	public void setItemIndex(String borrowedItemSearch)	//attempt to find and set index of desired item from user input
	{
		itemIndex = getSearchResults(borrowedItemSearch);
	}
	
	public int getSearchResults(String borrowedBorrowedItemSearch)
	{
		int localFirst = 0;
		int localLast = itemRecordCount - 1;
		int localMid = 0;
		boolean localFoundIt = false;
		
		while(localFirst <= localLast && localFoundIt == false)
		{
			localMid = (localFirst + localLast)/2;
			
			if(Integer.parseInt(itemRecords[localMid][0]) == Integer.parseInt(borrowedBorrowedItemSearch))
			{
				localFoundIt = true;
			}
			else 
			{
				if(Integer.parseInt(borrowedBorrowedItemSearch) > Integer.parseInt(itemRecords[localMid][0]))
				{
					localFirst = localMid + 1;
				}
				else
				{
					localLast = localMid - 1;
				}
			}
		}
		
		if(localFoundIt == false)
		{
			localMid = -1;
		}
		
		return localMid;		
	}
	
	public void setItemIDBubbleSort()
	{
		int localLast = itemRecordCount -1;
		int localIndex = 0;
		boolean localSwapFlag = false;

		
		while(localLast > 0)
		{
			localIndex = 0;
			localSwapFlag = false;
			
			while(localIndex < localLast)
			{
				if(Integer.parseInt(itemRecords[localIndex][0]) > Integer.parseInt(itemRecords[localIndex + 1][0]))
				{
					localSwapFlag = true;
					
					setSwapElements(localIndex);
					
				}
				localIndex++;
			}
			if (localSwapFlag == false)
			{
				localLast = 0;
			}
			else
			{
				localLast--;
			}
		}
	}
	
	public void setSwapElements(int borrowedIndex)
	{
		String localTempString = "";

		for(int localField = 0; localField < itemRecords[0].length; localField++)
		{
			localTempString = itemRecords[borrowedIndex + 1][localField];
			itemRecords[borrowedIndex + 1][localField] = itemRecords[borrowedIndex][localField];
			itemRecords[borrowedIndex][localField] = localTempString;
		}
	}

	public String[][] getItemRecords()
	{
		return itemRecords;
	}
	
	public double getGrandTotal()
	{
		double localTotal = 0.0;
		
		for(int localIndex = 0; localIndex < itemRecords.length; localIndex++)
		{
			localTotal += Double.parseDouble(itemRecords[localIndex][4]);
		}
		
		return localTotal;
	}
	
	public String[][] getDiscounts()
	{
		return DISCOUNTS;
	}

	public String[] getPrizeNames()
	{
		return PRIZE_NAMES;
	}
	
	public int getItemRecordCount()
	{
		return itemRecordCount;
	}
	
	public int getitemIndex()
	{
		return itemIndex;
	}
	
	public int[] getInventoryCounts()
	{
		return inventorySoldCounts;
	}
}//end of inventory class