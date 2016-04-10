package jo.aspire.test;

import java.util.List;

import jo.aspire.api.automationUtil.DBhelper;

public class DBhelperTest {
	
	public static void main(String[] args){
		String TestQery , TestConnString , TestDriver , TestUsername , TestPasswd , DBname;
		DBname = "worldnow";
		TestQery = "select userNo,firstname,lastname,loginname from AffiliateUserWorldnow where loginname like 'manaf%';";
		TestConnString = "jdbc:sqlserver://WNTSTSQL01\\dsys1;databaseName="+DBname+";";
		TestDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		TestUsername = "wnow";
		TestPasswd = "public2see";
		
		
		DBhelper conn = new DBhelper(TestQery, TestConnString, TestDriver, TestUsername, TestPasswd);
		//boolean connected = conn.CheckDataInDB(Key);
		List<String[]> DBvalue = conn.GetDBData(TestQery);
		List<String[]> DataBaseValueWithoutSpace = DBvalue;
		System.out.println(DataBaseValueWithoutSpace);
		
	}

}
