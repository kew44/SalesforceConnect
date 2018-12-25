package com.java.sfdc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
 
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.json.JSONException;
 
public class SalesforceCrudOPwithTrainRider {
 
	static final String USERNAME     = "shuboypanda@gmail.com";
    static final String PASSWORD     = "Shuboy$123wyxdrBjfiysHH8BgxzdGXWp2";
    static final String LOGINURL     = "https://login.salesforce.com";
    static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
    static final String CLIENTID     = "3MVG9_zfgLUsHJ5pcLvz1njiXea0rxR0bJ1mANaRii.MkDnXryszKMaEGmWJWzBRLTQX7tqmQEOMNXTrPwmZ5";
    static final String CLIENTSECRET = "306077981507142943";
    
    private static String REST_ENDPOINT = "/services/data" ;
    private static String API_VERSION = "/v44.0" ;
    private static String baseUri;
    private static Header oauthHeader;
    private static Header prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
    private static String TrainRiderID ;
    private static String TrainRiderEmailID;
    private static String TrainRiderName;
    private static String TrainRiderPassword;
 
    public static void main(String[] args) {
 
        HttpClient httpclient = HttpClientBuilder.create().build();
 
        // Assemble the login request URL
        String loginURL = LOGINURL +
                          GRANTSERVICE +
                          "&client_id=" + CLIENTID +
                          "&client_secret=" + CLIENTSECRET +
                          "&username=" + USERNAME +
                          "&password=" + PASSWORD;
 
        // Login requests must be POSTs
        HttpPost httpPost = new HttpPost(loginURL);
        HttpResponse response = null;
 
        try {
            // Execute the login POST request
            response = httpclient.execute(httpPost);
        } catch (ClientProtocolException cpException) {
            cpException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
 
        // verify response is HTTP OK
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            System.out.println("Error authenticating to Force.com: "+statusCode);
            // Error is in EntityUtils.toString(response.getEntity())
            return;
        }
 
        String getResult = null;
        try {
            getResult = EntityUtils.toString(response.getEntity());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
 
        JSONObject jsonObject = null;
        String loginAccessToken = null;
        String loginInstanceUrl = null;
 
        try {
            jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
            loginAccessToken = jsonObject.getString("access_token");
            loginInstanceUrl = jsonObject.getString("instance_url");
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
 
        baseUri = loginInstanceUrl + REST_ENDPOINT + API_VERSION ;
        oauthHeader = new BasicHeader("Authorization", "OAuth " + loginAccessToken) ;
        System.out.println("oauthHeader1: " + oauthHeader);
        System.out.println("\n" + response.getStatusLine());
        System.out.println("Successful login");
        System.out.println("instance URL: "+loginInstanceUrl);
        System.out.println("access token/session ID: "+loginAccessToken);
        System.out.println("baseUri: "+ baseUri);        
 
        // Run codes to query, isnert, update and delete records in Salesforce using REST API
        queryLeads();
        createLeads();
      //  updateLeads();
        deleteLeads();        
 
        // release connection
        httpPost.releaseConnection();
    }
 
    // Query Leads using REST HttpGet
    public static void queryLeads() {
        System.out.println("\n_______________ Lead QUERY _______________");
        try {
 
            //Set up the HTTP objects needed to make the request.
            HttpClient httpClient = HttpClientBuilder.create().build();
 
            String uri = baseUri + "/query?q=Select+Id+,+trainrider__Tremail__c+,+Name+,+trainrider__Trpassword__c+From+trainrider__TrainRider__c+Limit+3";
            System.out.println("Query URL: " + uri);
            HttpGet httpGet = new HttpGet(uri);
            System.out.println("oauthHeader2: " + oauthHeader);
            httpGet.addHeader(oauthHeader);
            httpGet.addHeader(prettyPrintHeader);
 
            // Make the request.
            HttpResponse response = httpClient.execute(httpGet);
 
            // Process the result
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String response_string = EntityUtils.toString(response.getEntity());
                try {
                    JSONObject json = new JSONObject(response_string);
                    System.out.println("JSON result of Query:\n" + json.toString(1));
                    JSONArray j = json.getJSONArray("records");
                    for (int i = 0; i < j.length(); i++){
                    	TrainRiderID = json.getJSONArray("records").getJSONObject(i).getString("Id");
                    	TrainRiderEmailID = json.getJSONArray("records").getJSONObject(i).getString("trainrider__Tremail__c");
                        TrainRiderName = json.getJSONArray("records").getJSONObject(i).getString("Name");
                        TrainRiderPassword = json.getJSONArray("records").getJSONObject(i).getString("trainrider__Trpassword__c");
                        System.out.println("Lead record is: " + i + ". " + TrainRiderID + " " + TrainRiderEmailID + " " + TrainRiderName +" "+ TrainRiderPassword );
                        
                        // to insert the each record in oracle database...
                         
                        Class.forName("oracle.jdbc.driver.OracleDriver");  
                        Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","shubhu","shubhu");  
                    	System.out.println("Connected..");
                    	Statement stmt=con.createStatement();  
                    	String insertSql="insert into trainrider values("+"trainride_seq.nextval,'"+TrainRiderID+"'"+",'"+TrainRiderEmailID+"','"+TrainRiderName+"','"+TrainRiderPassword+"'"+")";
                    	System.out.println("insertSql-->"+insertSql);
                    	stmt.executeUpdate(insertSql);  
                    	System.out.println("1 row inserted..");
            			con.close();  
            		
                    }
                } catch (JSONException je) {
                    je.printStackTrace();
                } catch(Exception e){
    				
    				System.out.println(e);
    			}
            } else {
                System.out.println("Query was unsuccessful. Status code returned is " + statusCode);
                System.out.println("An error has occured. Http status: " + response.getStatusLine().getStatusCode());
                System.out.println(getBody(response.getEntity().getContent()));
                System.exit(-1);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }
 
    // Create Leads using REST HttpPost
public static void createLeads() {
    System.out.println("\n_______________ Lead INSERT _______________");
 
        String uri = baseUri + "/sobjects/trainrider__ExternalDatase__c/";
        try {
 
       // to fetch dbid from trainrider table
    	Class.forName("oracle.jdbc.driver.OracleDriver");  
        Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","shubhu","shubhu");  
     	System.out.println("Connected..");
     	Long DBid=(long) 0;
     	String trid=TrainRiderID;
     	System.out.println("trid-->"+trid);
     	Statement stmt=con.createStatement();  
     	String selectSql="select id from trainrider where TRAINRIDERID='"+trid+"'";
     	System.out.println(selectSql);
     	ResultSet rs=stmt.executeQuery(selectSql);  
     	while(rs.next())  {
     		System.out.println("enter..");
     	 System.out.println(rs.getLong(1));
         DBid=rs.getLong(1);
     	}  
     	con.close();  

        JSONObject lead = new JSONObject();
        lead.put("trainrider__TrainRiderID__c", trid);
        lead.put("trainrider__DBId__c",DBid);
        System.out.println("JSON for update of lead record:\n" + lead.toString(1));
 

            //Construct the objects needed for the request
            HttpClient httpClient = HttpClientBuilder.create().build();
 
            HttpPost httpPost = new HttpPost(uri);
            httpPost.addHeader(oauthHeader);
            httpPost.addHeader(prettyPrintHeader);
            // The message we are going to post
        StringEntity body = new StringEntity(lead.toString(1));
        body.setContentType("application/json");
            httpPost.setEntity(body);
 
            //Make the request
            HttpResponse response = httpClient.execute(httpPost);
 
            //Process the results
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 201) {
            String response_string = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(response_string);
            // Store the retrieved lead id to use when we update the lead.
            TrainRiderID = json.getString("id");
            System.out.println("record inserted...");
            System.out.println("New Lead id from response: " + TrainRiderID);
        } else {
            System.out.println("Insertion unsuccessful. Status code returned is " + statusCode);
        }
    } catch (JSONException e) {
        System.out.println("Issue creating JSON or processing results");
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
 /*
    // Update Leads using REST HttpPatch. We have to create the HTTPPatch, as it does not exist in the standard library
// Since the PATCH method was only recently standardized and is not yet implemented in Apache HttpClient
public static void updateLeads() {
    System.out.println("\n_______________ Lead UPDATE _______________");
 
        //Notice, the id for the record to update is part of the URI, not part of the JSON
    String uri = baseUri + "/sobjects/trainrider__ExternalDatase__c/" + TrainRiderID;
    try {
        //Create the JSON object containing the updated lead last name
        //and the id of the lead we are updating.
    	
    	// to fetch dbid from trainrider table
    	Class.forName("oracle.jdbc.driver.OracleDriver");  
        Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","shubhu","shubhu");  
     	System.out.println("Connected..");
     	Long DBid=(long) 0;
     	String trid=TrainRiderID;
     	System.out.println("trid-->"+trid);
     	Statement stmt=con.createStatement();  
     	String selectSql="select id from trainrider where TRAINRIDERID='"+trid+"'";
     	System.out.println(selectSql);
     	ResultSet rs=stmt.executeQuery(selectSql);  
     	while(rs.next())  {
     		System.out.println("enter..");
     	 System.out.println(rs.getLong(1));
         DBid=rs.getLong(1);
     	}  
     	con.close();  

        JSONObject lead = new JSONObject();
        lead.put("trainrider__TrainRiderID__c", trid);
        lead.put("trainrider__DBId__c",DBid);
        System.out.println("JSON for update of lead record:\n" + lead.toString(1));
 
            //Set up the objects necessary to make the request.
        //DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpClient httpClient = HttpClientBuilder.create().build();
 
            HttpPatch httpPatch = new HttpPatch(uri);
            httpPatch.addHeader(oauthHeader);
            httpPatch.addHeader(prettyPrintHeader);
            StringEntity body = new StringEntity(lead.toString(1));
            body.setContentType("application/json");
            httpPatch.setEntity(body);
 
            //Make the request
            HttpResponse response = httpClient.execute(httpPatch);
 
            //Process the response
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 204) {
            System.out.println("Updated the lead successfully.");
        } else {
            System.out.println("Lead update NOT successfully. Status code is " + statusCode);
        }
    } catch (JSONException e) {
        System.out.println("Issue creating JSON or processing results");
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
   
    // Extend the Apache HttpPost method to implement an HttpPatch
    private static class HttpPatch extends HttpPost {
        public HttpPatch(String uri) {
            super(uri);
        }
 
        public String getMethod() {
            return "PATCH";
        }
    }
  */
  //Update Leads using REST HttpDelete (We have to create the HTTPDelete, as it does not exist in the standard library.)
public static void deleteLeads() {
    System.out.println("\n_______________ Lead DELETE _______________");
 
        //Notice, the id for the record to update is part of the URI, not part of the JSON
    String uri = baseUri + "/sobjects/trainrider__TrainRider__c/" + TrainRiderID;
    try {
        //Set up the objects necessary to make the request.
            HttpClient httpClient = HttpClientBuilder.create().build();
 
            HttpDelete httpDelete = new HttpDelete(uri);
            httpDelete.addHeader(oauthHeader);
            httpDelete.addHeader(prettyPrintHeader);
 
            //Make the request
            HttpResponse response = httpClient.execute(httpDelete);
 
            //Process the response
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 204) {
            System.out.println("Deleted the lead successfully.");
        } else {
            System.out.println("Lead delete NOT successful. Status code is " + statusCode);
        }
    } catch (JSONException e) {
        System.out.println("Issue creating JSON or processing results");
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }
 
    private static String getBody(InputStream inputStream) {
        String result = "";
    try {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(inputStream)
        );
        String inputLine;
        while ( (inputLine = in.readLine() ) != null ) {
            result += inputLine;
            result += "\n";
        }
        in.close();
    } catch (IOException ioe) {
        ioe.printStackTrace();
    }
    return result;
}

 }
