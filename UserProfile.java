package edu.cpt187.koller.sodnotzod;

public class UserProfile 
{
	private final int USERS_ARRAY_LENGTH = 20;
	private final int USERS_ARRAY_FIELDS = 2;
	private final int ATTEMPT_ARRAY_LENGTH = 2;
	private final String[] USER_MENU_OPTIONS = {"User Menu", ".........", "Create Account", "Login to Account"};
	private String[][] users = new String[USERS_ARRAY_LENGTH][USERS_ARRAY_FIELDS];
	private String[] loginAttempt = new String[ATTEMPT_ARRAY_LENGTH];
	private int userIndex = 0;
	private int userRecordCount = 0;
	
	public void setUserNamePasswordArray(String[] borrowedCreateUserNamePassword)	//create new user name and password if user name has not been used
	{
		users[userRecordCount][0] = borrowedCreateUserNamePassword[0];
		users[userRecordCount][1] = borrowedCreateUserNamePassword[1];
		
		if(getUserNameSearchResult() == false)
		{
			userRecordCount++;
			userIndex = 0;
		}
		else
		{
			userIndex = -1;
		}
	}
	
	public void setLoginAttempt(String[] borrowedUserNamePassword)	//check login attempt against array
	{
		loginAttempt[0] = borrowedUserNamePassword[0];
		loginAttempt[1] = borrowedUserNamePassword[1];
		userIndex = getValidLogin();
	}
	
	public boolean getUserNameSearchResult()	//check desired user name again array for matches
	{
		int localIndex = 0;
		boolean localFoundIt = false;
		
		while(localIndex < userRecordCount)
		{
			if(users[userRecordCount][0].equalsIgnoreCase(users[localIndex][0]))
			{
				localFoundIt = true;
				localIndex = userRecordCount;
			}
			else
			{
				localIndex++;
			}
		}
		
		return localFoundIt;
	}
	
	public int getValidLogin()	//check login attempt against arrays
	{
		int localIndex = 0;
		int localFoundIt = -1;
		
		while (localIndex < userRecordCount)
		{
			if(users[localIndex][0].equalsIgnoreCase(loginAttempt[0]) && users[localIndex][1].equals(loginAttempt[1]))
			{
				localFoundIt = localIndex;
				localIndex = userRecordCount;
			}
			else
			{
				localIndex++;
			}
		}
		return localFoundIt;
	}
	
	public String getUserName()
	{
		return users[userIndex][0];
	}

	public String[] getUserMenuOptions()
	{
		return USER_MENU_OPTIONS;
	}
	
	public int getUserIndex()
	{
		return userIndex;
	}
}//End of user class



