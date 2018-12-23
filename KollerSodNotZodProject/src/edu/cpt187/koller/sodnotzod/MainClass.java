package edu.cpt187.koller.sodnotzod;

import java.util.Scanner;

public class MainClass 
{
	public static final String[][] MENU_OPTIONS = 
		{
				{"Main Menu", "Order Menu", "Discount Selection Menu"},
				{".........", "..........", "......................."},
				{"Load Inventory File", "Add new item to order"},
				{"Create Customer Order", "Go to Discounts and Checkout"},
				{"Quit Program", "Cancel and Return to Main Menu"}
		};
	
	
	public static void main(String[] args) 
	{
		//Declare and initialize variable
		char menuSelect = ' ';

		//declare objects
		Scanner input = new Scanner(System.in);
		SodOrder newSodOrder = new SodOrder();
		Inventory currentInventory = new Inventory();
		UserProfile newUserName = new UserProfile();
		OrderWriter orderWriter = new OrderWriter();
		
		displayWelcomeBanner();
		
		while(newUserName.getValidLogin() < 0)	//while login is invalid
		{
			while(validateUserMenu(input, newUserName.getUserMenuOptions()) == 'A')		//display and get user input for user account menu
			{
				newUserName.setUserNamePasswordArray(validateCreateUserAccount(input));	//send user name and password to UserProfile class to load array
				
				if(newUserName.getUserIndex() < 0)	//check if user name is taken and display appropriate response
				{
					displayUserNameTaken();
				}
				else
				{
					displayCreateAccountSuccess();
				}
			}
			
			newUserName.setLoginAttempt(validateLogin(input));	//send user login attempt to UserProfile class to validate login
			
			if(newUserName.getValidLogin() < 0) 
			{
				displayLoginResult();	//display invalid login
			}
			else
			{
				displayLoginResult(newUserName.getUserName());	//display successful login and welcome user
			}
		}
		
		menuSelect = validateMainMenu(input);	//initial main menu option
		
		while (menuSelect != 'Q')	//Start of while not-quit loop
		{
			if (menuSelect == 'A')	//user selects to load inventory file
			{
				currentInventory.setLoadItemsFromFile(validateFileRequest(input));	//attempt to load user designated file and load inventory array
				
				if (currentInventory.getItemRecordCount() < 0)
				{
					displayFileSearchResult();	//file not found
				}
				else
				{
					displayFileSearchResult(currentInventory.getItemRecordCount());	//file loaded successfully with how many records loaded
				}
			}
			else
			{
				if(currentInventory.getItemRecordCount() <= 0)	//Don't begin customer order unless inventory file is loaded
				{
					displayNoFileLoaded();
				}
				else
				{
					newSodOrder.setResetForNewReceipt();	//Receipt item receipt counter for new customer
					
					menuSelect = validateOrderMenu(input);
					
					while(menuSelect == 'A')
					{							
						currentInventory.setItemIndex(validateItemSearch(input));	//binary search loaded inventory array for desired item
							
						while (currentInventory.getitemIndex() < 0)	//while item not found
						{
							displayItemNotFound();
							currentInventory.setItemIndex(validateItemSearch(input));
						}
							
						newSodOrder.setItemSelection(currentInventory.getitemIndex(), currentInventory.getItemRecords());	//load item record from inventory into temp array

						newSodOrder.setHowMany(validateHowMany(input, newSodOrder.getItemSelection()));	//confirm how many of item customer wants
							
						if (newSodOrder.getInstockFlag(currentInventory.getItemRecords(), currentInventory.getitemIndex()) == true)	//check if enough are in-stock
						{	
							newSodOrder.setAddToOrderReceipt();
						}
						else
						{
							displayOutOfStock(newSodOrder.getItemSelection());
						}
						
						menuSelect = validateOrderMenu(input);	//modify LCV
					}

					if(menuSelect == 'B')	//if user chooses to go to discount menu and checkout
					{
						if(newSodOrder.getReceiptItemCount() == 0)	//check if any items have been added to order receipt array
						{
							displayNoItemsInCart();		//display error if user chose to go to checkout with no items added to cart
						}
						else
						{
							newSodOrder.setDecreaseInStock(currentInventory);	//reduce inventory of items selected on order
							
							newSodOrder.setDiscountRate(validateDiscountMenu(input, currentInventory.getDiscounts()),	//set discount rate for order
									currentInventory.getDiscounts());
										
							newSodOrder.setPrizeName(currentInventory.getPrizeNames());	//use Random Class to generate prize for customer
								
							orderWriter.setWriteOrderToMasterFile(newSodOrder.getOrderReceipt(), newSodOrder.getReceiptItemCount());	//write full order to daily total file
								
							if(orderWriter.getMasterRecordCount() < 0)	//if there was an error writing the order to receipt file
							{
								displayWriteToFileError();
							}
							else
							{
								if (newSodOrder.getDiscountAmtTotal() > 0.0)	//if there was a discount display receipt with discount line
								{
									displayItemReceipt(newSodOrder.getOrderReceipt(), newSodOrder.getReceiptItemCount(), newSodOrder.getDiscountRateNameSelect(), 
											newSodOrder.getDiscountRateSelect(), newSodOrder.getDiscountAmtTotal(),	newSodOrder.getSubTotal(), newSodOrder.getTaxAmount(), 
											newSodOrder.getTotalCost(), newSodOrder.getPrizeName());
					
								}
								else	//display receipt with no discount line
								{
									displayItemReceipt(newSodOrder.getOrderReceipt(), newSodOrder.getReceiptItemCount(), newSodOrder.getSubTotal(), 
											newSodOrder.getTaxAmount(), newSodOrder.getTotalCost(), newSodOrder.getPrizeName());
								}
							}
						}
					}//end of menu select == B
				}
			}
			menuSelect = validateMainMenu(input);
			
		}//End of run-while NOT QUIT loop
		
		if (orderWriter.getMasterRecordCount() > 0)	//Display final report if an order has been placed
		{
			displayDiscountAndInventoryCounts(currentInventory.getInventoryCounts(), currentInventory.getDiscounts(), newSodOrder.getDiscountCounts(),
					currentInventory.getItemRecords(), currentInventory.getItemRecordCount(), newUserName.getUserName());	//Display report on each item in inventory and number sold before arrays are loaded with master file
			
			currentInventory.setLoadItemFromFile(orderWriter.getMasterFileName(), orderWriter.getMasterRecordCount());	//load 2D array with master file
			
			displayFinalReport(currentInventory.getGrandTotal(), currentInventory.getItemRecordCount(), currentInventory.getItemRecords());
			
			if(validateDeleteFilePrompt(input, orderWriter.getMasterFileName()) == 'Y')	//prompt to delete written record file
			{
				orderWriter.setDeleteMasterFile();
			}
		}
		
		//Display farewell banner
		displayFarewellBanner();
		
		//Close Scanner
		input.close();
		
	}//End of main method

	//Method to validate user name
	public static String[] validateLogin(Scanner borrowedInput)
	{
		String[] localLogin = new String[2];

		displayAskUserName();
		localLogin[0] = borrowedInput.next();
		localLogin[0] = localLogin[0].substring(0,1).toUpperCase() + localLogin[0].substring(1).toLowerCase();
		
		displayAskUserPassword();
		localLogin[1] = borrowedInput.next();
			
		return localLogin;
	}//End of method to validate user name
	
	public static void displayAskUserName()
	{
		System.out.print("\nPlease enter your user name: ");
	}
	
	public static void displayAskUserPassword()
	{
		System.out.print("\nPlease enter your password: ");
	}
	
	public static void displayLoginResult()
	{
		System.out.println("\n............................................................");
		System.out.printf("%2s%-58s", "", "Invalid username and password, or your account does not");
		System.out.printf("\n%18s%-42s", "", "exist. Please try again.");
		System.out.println("\n............................................................");
	}
	public static void displayLoginResult(String borrowedUserName)	//successful login
	{
		System.out.println("\nLogin successful. Welcome " + borrowedUserName + "!");
	}
	
	public static void displayCreateUserMenu(String[] borrowedBorrowedUserMenuOptions)
	{
		System.out.printf("\n%1s%-59s", "", "__________________________________________________________");
		System.out.printf("\n%1s%59s", "|", "|");
		System.out.printf("\n%1s%24s%-34s%1s", "|", "", borrowedBorrowedUserMenuOptions[0], "|");
		System.out.printf("\n%1s%24s%-34s%1s", "|", "", borrowedBorrowedUserMenuOptions[1], "|");
		System.out.printf("\n%1s%18s%-4s%-36s%1s", "|", "", "[A]", borrowedBorrowedUserMenuOptions[2], "|");
		System.out.printf("\n%1s%18s%-4s%-36s%1s", "|", "", "[B]", borrowedBorrowedUserMenuOptions[3], "|");
		System.out.printf("\n%60s","|__________________________________________________________|\n");
		System.out.print("\nPlease enter your selection here: ");
	}
	
	public static void displayUserNameTaken()
	{
		System.out.println("\n............................................................");
		System.out.printf("%5s%-55s", "", "That user name is already taken. Please try again.");
		System.out.println("\n............................................................");
	}
	
	public static void displayCreateAccountSuccess()
	{
		System.out.println("\nThank you for creating an account! You will be asked to");
		System.out.println("login before using the program.");
	}
	
	public static String[] validateCreateUserAccount(Scanner borrowedInput)
	{
		String[] localLogin = new String[3];
		
		System.out.print("\nEnter a desired user name: ");
		localLogin[0] = borrowedInput.next();
		localLogin[0] = localLogin[0].substring(0,1).toUpperCase() + localLogin[0].substring(1).toLowerCase();
		
		System.out.print("\nPlease create a password: ");
		localLogin[1] = borrowedInput.next();
		
		System.out.print("\nPlease confirm your password: ");
		localLogin[2] = borrowedInput.next();
		
		while(!localLogin[1].equals(localLogin[2]))
		{
			System.out.print("\nYour passwords did not match. Please try again.\n");
			System.out.print("\nCreate a password for your user name: ");
			localLogin[1] = borrowedInput.next();
			
			System.out.print("\nPlease confirm your password: ");
			localLogin[2] = borrowedInput.next();
		}
		return localLogin;
	}
	
	//Method to validate user  menu
	public static char validateUserMenu(Scanner borrowedInput, String[] borrowedUserMenuOptions)
	{
		char localMenuSelect = ' ';
		
		displayCreateUserMenu(borrowedUserMenuOptions);
		localMenuSelect = borrowedInput.next().toUpperCase().charAt(0);
		
		while (localMenuSelect != 'A' && localMenuSelect != 'B')
		{
			displayInvalidEntry();
			displayCreateUserMenu(borrowedUserMenuOptions);
			localMenuSelect = borrowedInput.next().toUpperCase().charAt(0);
		}
		return localMenuSelect;
	}
		
	//Method to display welcome banner
	public static void displayWelcomeBanner()
	{
		System.out.println("************************************************************");
		System.out.printf("%1s%18s%22s%18s%1s", "*", "", "Welcome to Sod Not Zod", "", "*");
		System.out.printf("\n%1s%6s%47s%5s%1s", "*", "", "This program will help you choose and calculate", "", "*");
		System.out.printf("\n%1s%12s%34s%12s%1s", "*", "", "your order for all your sod needs!", "", "*");
		System.out.println("\n************************************************************");
	}
	
	public static void displayInvalidEntry()
	{
		System.out.println("\n............................................................");
		System.out.printf("%13s%-47s", "", "Invalid entry.  Please try again.");
		System.out.println("\n............................................................");
	}

	public static void displayDiscountMenu(String[][] borrowedBorrowedDiscounts)
	{	
		System.out.printf("\n%1s%-59s", "", "__________________________________________________________");
		System.out.printf("\n%1s%59s", "|", "|");
		System.out.printf("\n%1s%18s%-40s%1s", "|", "", MENU_OPTIONS[0][2], "|");
		System.out.printf("\n%1s%18s%-40s%1s", "|", "", MENU_OPTIONS[1][2], "|");
		System.out.printf("\n%1s%14s%-4s%-15s%10.0f%-15s%1s", "|", "", "[A]", borrowedBorrowedDiscounts[0][0],
				(100 * Double.parseDouble(borrowedBorrowedDiscounts[1][0])), "%", "|");
		System.out.printf("\n%1s%14s%-4s%-15s%10.0f%-15s%1s", "|", "", "[B]", borrowedBorrowedDiscounts[0][1], 
				(100 * Double.parseDouble(borrowedBorrowedDiscounts[1][1])), "%", "|");
		System.out.printf("\n%1s%14s%-4s%-40s%1s", "|", "", "[C]", borrowedBorrowedDiscounts[0][2], "|");
		System.out.printf("\n%60s","|__________________________________________________________|\n");
		System.out.print("\nPlease enter your selection here: ");
	}
	
	public static char validateDiscountMenu(Scanner borrowedInput, String[][] borrowedDiscounts)
	{
		char localMenuSelect = ' ';
		
		displayDiscountMenu(borrowedDiscounts);
		localMenuSelect = borrowedInput.next().toUpperCase().charAt(0);
		
		while (localMenuSelect != 'A' && localMenuSelect != 'B' && localMenuSelect != 'C')
		{
			displayInvalidEntry();
			displayDiscountMenu(borrowedDiscounts);
			localMenuSelect = borrowedInput.next().toUpperCase().charAt(0);
		}
		return localMenuSelect;
	}
	
	public static void displayItemQuantityPrompt(String[] borrowedBorrowedItemSelection)
	{
		System.out.println("\nEnter how many " + borrowedBorrowedItemSelection[1] + " at $" + borrowedBorrowedItemSelection[2] + " the customer");
		System.out.println("would like.");
		System.out.print("\nQuantity: ");
	}
	
	public static void displayInvalidItemQuantity()
	{
		System.out.println("\nInvalid amount. Quantity entered must be a whole number");
		System.out.println("greater than 0.");
	}
	public static String validateHowMany(Scanner borrowedInput, String[] borrowedItemSelection)
	{
		int localIndex = 0;
		String localHowMany = "";
		boolean localValidAmount = false;
		
		displayItemQuantityPrompt(borrowedItemSelection);
		localHowMany = borrowedInput.next().trim();
		
		while(localValidAmount == false)
		{
			while(localIndex < localHowMany.length())
			{
				if(!Character.isDigit(localHowMany.charAt(localIndex)))
				{
					displayInvalidItemQuantity();
					displayItemQuantityPrompt(borrowedItemSelection);
					localHowMany = borrowedInput.next().trim();
					localIndex = 0;
				}
				else
				{
					localIndex++;
				}
			}
			if(Integer.parseInt(localHowMany) > 0)
			{
				localValidAmount = true;
			}
			else
			{
				displayInvalidItemQuantity();
				displayItemQuantityPrompt(borrowedItemSelection);
				localHowMany = borrowedInput.next().trim();
				localIndex = 0;
			}
		}
		return localHowMany;
	}
	
	//display receipt with discount
	public static void displayItemReceipt(String[][] borrowedOrderReceipt, int borrowedReceiptItemCount, String borrowedDiscountNameSelect,
			double borrowedDiscountRate, double borrowedDiscountAmount, double borrowedSubTotal, double borrowedTaxAmount, 	double borrowedTotalCost, 
			String borrowedPrizeName)
	{
		System.out.println("\n____________________________________________________________");
		System.out.printf("%23s%-37s", "", "Order Receipt");
		System.out.println("\n............................................................");
		System.out.printf("%-16s%7s%17s%20s", "Item", "Qty", "Amt", "Cost" );
		System.out.println("\n____________________________________________________________");
		for(int localIndex = 0; localIndex < borrowedReceiptItemCount; localIndex++)
		{
			System.out.printf("\n%-16s%6s%10s%8s%12s%8.2f",  borrowedOrderReceipt[localIndex][1], borrowedOrderReceipt[localIndex][3], "$",  
					borrowedOrderReceipt[localIndex][2], "$", Double.parseDouble(borrowedOrderReceipt[localIndex][4]));
		}
		System.out.println();
		System.out.printf("\n%47s%5s%8.2f", "Subtotal", "$", borrowedSubTotal);
		System.out.printf("\n%38s%9s%5s%8.2f", borrowedDiscountNameSelect, "discount", "$", (0 - borrowedDiscountAmount));
		System.out.printf("\n%47s%13.2f", "Tax", borrowedTaxAmount);
		System.out.printf("\n%47s%5s%8.2f\n", "Total", "$", borrowedTotalCost);
		System.out.printf("\n%-40s%20s",  "Congratulations! You win a prize!", borrowedPrizeName);
		System.out.println("\n____________________________________________________________");
		System.out.println("\n............................................................");
	}
	
	//display receipt with no discount.
	public static void displayItemReceipt(String[][] borrowedOrderReceipt, int borrowedReceiptItemCount, double borrowedSubTotal, double borrowedTaxAmount, 
			double borrowedTotalCost, String borrowedPrizeName)
	{
		System.out.println("\n____________________________________________________________");
		System.out.printf("%23s%-37s", "", "Order Receipt");
		System.out.println("\n............................................................");
		System.out.printf("%-16s%7s%17s%20s", "Item", "Qty", "Amt", "Cost" );
		System.out.println("\n____________________________________________________________");
		for(int localIndex = 0; localIndex < borrowedReceiptItemCount; localIndex++)
		{
			System.out.printf("\n%-16s%6s%10s%8s%12s%8.2f",  borrowedOrderReceipt[localIndex][1], borrowedOrderReceipt[localIndex][3], "$",  
					borrowedOrderReceipt[localIndex][2], "$", Double.parseDouble(borrowedOrderReceipt[localIndex][4]));
		}
		System.out.println();
		System.out.printf("\n%47s%5s%8.2f", "Subtotal", "$", borrowedSubTotal);
		System.out.printf("\n%47s%13.2f", "Tax", borrowedTaxAmount);
		System.out.printf("\n%47s%5s%8.2f\n", "Total", "$", borrowedTotalCost);
		System.out.printf("\n%-40s%20s",  "Congratulations! You win a prize!", borrowedPrizeName);
		System.out.println("\n____________________________________________________________");
		System.out.println("\n............................................................");
	}//End of display item receipt
	
	public static void displayDiscountAndInventoryCounts(int[] borrowedInventoryCounts, String[][] borrowedDiscounts, int[] borrowedDiscountCounts, 
			String[][] borrowedItemRecords, int borrowedItemRecordCount, String borrowedUserName)
	{
		System.out.println("\nGreat work " + borrowedUserName + ". Here is your report for the day.");
		System.out.println("____________________________________________________________");
		System.out.printf("%21s%-39s", "", "DAILY FINAL REPORT");
		System.out.println("\n............................................................");
		System.out.printf("%-21s%-19s%20s", "Item", "Discounts Applied", "Qty");
		System.out.println("\n____________________________________________________________");
		for(int localIndex = 0; localIndex < borrowedDiscounts[0].length; localIndex++)
		{
			System.out.printf("\n%-30s%30d", borrowedDiscounts[0][localIndex], borrowedDiscountCounts[localIndex]);
		}
		System.out.println();
		System.out.println("............................................................");
		System.out.printf("%19s%-41s", "", "Current Inventory File");
		System.out.println("\n____________________________________________________________");
		for(int localIndex = 0; localIndex < borrowedItemRecordCount; localIndex++) 
		{
			System.out.printf("\n%-30s%30d", borrowedItemRecords[localIndex][1], borrowedInventoryCounts[localIndex]);
		}
	}
	
	//Display final report
	public static void displayFinalReport(double borrowedGrandTotal, int borrowedItemRecordCount, String[][] borrowedItemRecords)
	{
		System.out.println("\n____________________________________________________________");
		System.out.printf("%24s%-36s",  "", "All Receipts");
		System.out.println("\n------------------------------------------------------------");
		System.out.printf("%-5s%-33s%-6s%4s%12s", "SKU", "ItemName", "Price", "Qty", "Total");
		System.out.println("\n____________________________________________________________");
		for(int localIndex = 0; localIndex < borrowedItemRecordCount; localIndex++)
		{
			System.out.printf("\n%-5s%-28s%4s%6s%5s%4s%8s", borrowedItemRecords[localIndex][0], borrowedItemRecords[localIndex][1], "$", 
					borrowedItemRecords[localIndex][2], borrowedItemRecords[localIndex][3], "$", borrowedItemRecords[localIndex][4]);
		}
		System.out.println();
		System.out.printf("\n%-30s%22s%8.2f", "Grand total sales", "$", borrowedGrandTotal);
		System.out.println("\n____________________________________________________________\n");
	}
	
	//Display farewell banner
	public static void displayFarewellBanner()
	{
		System.out.println("\n************************************************************");
		System.out.printf("%5s%-55s", "", "Thank you for using the Sod Not Zod Order Program.");
		System.out.printf("\n%16s%-44s", "", "Enjoy the rest of your day!");
		System.out.println("\n************************************************************");
	}
	
	//Display main menu
	public static void displayMainMenu()
	{
		System.out.printf("\n%1s%-59s", "", "__________________________________________________________");
		System.out.printf("\n%1s%59s", "|", "|");
		System.out.printf("\n%1s%24s%-34s%1s", "|", "", MENU_OPTIONS[0][0], "|");
		System.out.printf("\n%1s%24s%-34s%1s", "|", "", MENU_OPTIONS[1][0], "|");
		System.out.printf("\n%1s%16s%-4s%-38s%1s", "|", "", "[A]", MENU_OPTIONS[2][0], "|");
		System.out.printf("\n%1s%16s%-4s%-38s%1s", "|", "", "[B]", MENU_OPTIONS[3][0], "|");
		System.out.printf("\n%1s%16s%-4s%-38s%1s", "|", "", "[Q]", MENU_OPTIONS[4][0], "|");
		System.out.printf("\n%60s","|__________________________________________________________|\n");
		System.out.print("\nPlease enter your selection here: ");
	}
	
	//validate input for main menu
	public static char validateMainMenu(Scanner borrowedInput)
	{
		char localMenuSelect = ' ';
		
		displayMainMenu();
		localMenuSelect = borrowedInput.next().toUpperCase().charAt(0);
		
		while(localMenuSelect != 'A' && localMenuSelect != 'B' && localMenuSelect != 'Q')
		{
			displayInvalidEntry();
			displayMainMenu();
			localMenuSelect = borrowedInput.next().toUpperCase().charAt(0);
		}
		
		return localMenuSelect;
	}
	
	//display out of stock notice
	public static void displayOutOfStock(String[] borrowedItemSelection)
	{
		System.out.println("\nS.N.Z. apologizes, but we currently do not have enough");
		System.out.println(borrowedItemSelection[1] + " in stock to fill your order.");
	}
	
	public static void displayFileRequestPrompt()
	{
		System.out.println("\nEnter the name of the inventory file you would like to");
		System.out.println("load. File extensions are not required.");
		System.out.print("\nFile Name: ");
	}
	public static String validateFileRequest(Scanner borrowedInput)
	{
		String localFileName = "";
		
		displayFileRequestPrompt();
		localFileName = borrowedInput.next().trim().replaceAll(" +", "_");
		
		if(localFileName.contains(".dat"))
		{
			return localFileName;
		}
		else
		{
			return localFileName + ".dat";
		}
	}
	
	public static void displayFileSearchResult()	//File not found
	{
		System.out.println("\nUnfortunately that file cannot be found, is invalid, or");
		System.out.println("otherwise can not be read. Please try again.");
	}

	public static void displayFileSearchResult(int borrowedItemRecordCount)	//File found and load successful
	{
		System.out.println("\nThe file has been loaded successully. A total of");
		System.out.println(borrowedItemRecordCount + " records have been loaded.");
	}
	
	public static void displayItemSearchPrompt()
	{
		System.out.println("\nEnter the SKU number of the item the customer wishes to");
		System.out.println("purchase.");
		System.out.print("\nItem SKU: ");
	}
	
	public static void displayInvalidItemID()
	{
		System.out.println("\nAn item SKU does not contain letters or special characters.");
		System.out.println("Please try again.");
	}
	public static String validateItemSearch(Scanner borrowedInput)
	{
		String localItemSearch = "";
		int localIndex = 0;
		
		displayItemSearchPrompt();
		localItemSearch = borrowedInput.next().trim();
		
		while(localIndex < localItemSearch.length())
		{
			if(!Character.isDigit(localItemSearch.charAt(localIndex)))
			{
				displayInvalidItemID();
				displayItemSearchPrompt();
				localItemSearch = borrowedInput.next().trim();
				localIndex = 0;
			}
			else
			{
				localIndex++;
			}
		}
		
		return localItemSearch;
	}
	
	public static void displayItemNotFound()
	{
		System.out.println("\nThat item SKU was not found. Please try again.");
	}
	
	public static void displayNoFileLoaded()
	{
		System.out.println("\nYou must load an inventory file before creating an");
		System.out.println("order for a customer.");
	}
	
	public static void displayOrderMenu()
	{
		System.out.printf("\n%1s%-59s", "", "__________________________________________________________");
		System.out.printf("\n%1s%59s", "|", "|");
		System.out.printf("\n%1s%24s%-34s%1s", "|", "", MENU_OPTIONS[0][1], "|");
		System.out.printf("\n%1s%24s%-34s%1s", "|", "", MENU_OPTIONS[1][1], "|");
		System.out.printf("\n%1s%14s%-4s%-40s%1s", "|", "", "[A]", MENU_OPTIONS[2][1], "|");
		System.out.printf("\n%1s%14s%-4s%-40s%1s", "|", "", "[B]", MENU_OPTIONS[3][1], "|");
		System.out.printf("\n%1s%14s%-4s%-40s%1s", "|", "", "[C]", MENU_OPTIONS[4][1], "|");
		System.out.printf("\n%60s","|__________________________________________________________|\n");
		System.out.print("\nPlease enter your selection here: ");
	}
	
	public static char validateOrderMenu(Scanner borrowedInput)
	{
		char localMenuSelect = ' ';
		
		displayOrderMenu();
		localMenuSelect = borrowedInput.next().toUpperCase().charAt(0);
		
		while(localMenuSelect != 'A' && localMenuSelect != 'B' && localMenuSelect != 'C')
		{
			displayInvalidEntry();
			displayOrderMenu();
			localMenuSelect = borrowedInput.next().toUpperCase().charAt(0);
		}
		
		return localMenuSelect;
	}
	
	public static void displayDeleteFilePrompt(String borrowedBorrowedMasterFileName)
	{
		System.out.println("\nWould you like to delete the daily receipts file");
		System.out.println("\"" + borrowedBorrowedMasterFileName + "\" at this time?");
		System.out.print("\nY / N: ");
	}
	
	public static char validateDeleteFilePrompt(Scanner borrowedInput, String borrowedMasterFileName)
	{
		char localEntry = ' ';
		
		displayDeleteFilePrompt(borrowedMasterFileName);
		localEntry = borrowedInput.next().toUpperCase().charAt(0);
		
		while(localEntry != 'Y' && localEntry != 'N')
		{
			displayInvalidEntry();
			displayDeleteFilePrompt(borrowedMasterFileName);
			localEntry = borrowedInput.next().toUpperCase().charAt(0);
		}
		
		return localEntry;
	}
	
	public static void displayWriteToFileError()
	{
		System.out.println("\n............................................................");
		System.out.println("\nThere was a problem processing the customer order.");
		System.out.println("Please close the program and contact technical support.");
		System.out.println("............................................................");
	}
	
	public static void displayNoItemsInCart()
	{
		System.out.println("\n............................................................");
		System.out.printf("%9s%-51s", "", "No items in cart. Returning to Main Menu.");
		System.out.println("\n............................................................");
	}
	
}//End of main class



