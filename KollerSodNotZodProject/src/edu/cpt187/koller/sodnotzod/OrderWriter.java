package edu.cpt187.koller.sodnotzod;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class OrderWriter 
{
	private final String MASTER_FILE = "allReceipts.dat";
	private final String RECEIPT_FILE_PATH = "INSERT_ECLIPSE_WORKSPACE_PROJECT_FILE_PATH_HERE" + MASTER_FILE;
	private int	masterRecordCount = 0;
	
	public void setDeleteMasterFile()
	{
		File masterFile = new File(RECEIPT_FILE_PATH);
			
		masterFile.delete();
	}
	
	public void setWriteOrderToMasterFile(String[][] borrowedOrderReceipt, int borrowedItemCount)
	{
		try
		{
			PrintWriter newRecord = new PrintWriter(new FileWriter(MASTER_FILE, true));
			
			for(int localIndex = 0; localIndex < borrowedItemCount; localIndex++)
			{
				newRecord.printf("%d\t%s\t%.2f\t%d\t%.2f\r\n", Integer.parseInt(borrowedOrderReceipt[localIndex][0]), borrowedOrderReceipt[localIndex][1],
						Double.parseDouble(borrowedOrderReceipt[localIndex][2]), Integer.parseInt(borrowedOrderReceipt[localIndex][3]),
								Double.parseDouble(borrowedOrderReceipt[localIndex][4]));
				masterRecordCount++;
			}
			newRecord.close();
		}
		catch(IOException ex)
		{
			masterRecordCount = -1;
		}
	}
	
	public int getMasterRecordCount()
	{
		return masterRecordCount;
	}
	
	public String getMasterFileName()
	{
		return MASTER_FILE;
	}

}//end of OrderWriter class