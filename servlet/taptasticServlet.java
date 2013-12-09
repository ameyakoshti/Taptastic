import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class taptasticServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private PrintWriter out = null;
	private List<String> allowedtags = null;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setContentType("text/html");
		out = response.getWriter();

		allowedtags = new ArrayList<String>();
		allowedtags.add("15c88912");
		allowedtags.add("f5da8812");
		allowedtags.add("d5d38a12");
		allowedtags.add("dd7059dc");
		allowedtags.add("25b38b12");
		allowedtags.add("75aa8a12");

		String tagid = request.getParameter("tagid");
		if (!allowedtags.contains(tagid)) {
			//checkMessages(request.getParameter("classid"));
			return;
		}

		String id = request.getParameter("id");
		// SignUp
		if (Integer.parseInt(id) == 1) {
			String uscid = request.getParameter("uscid");
			String uname = request.getParameter("uname");
			String pwd = request.getParameter("pwd");
			out.print("{\"results\": {");
			out.print("\"status\": \"");
			signUp(uscid, uname, pwd);
			out.println("\",");
			out.println("\"posts\": \"\"}}");
	    // Login First Time
		} else if (Integer.parseInt(id) == 2) {
			String uname = request.getParameter("uname");
			String pwd = request.getParameter("pwd");
			out.print("{\"results\": {");
			out.print("\"status\": \"");
			login(uname, pwd);
			out.println("\",");
			out.println("\"posts\": \"\"}}");
	    // Check-in && Message Retrieval
		} else if (Integer.parseInt(id) == 3) {
			String uscid = request.getParameter("uscid");
			String classid = request.getParameter("classid");
			out.print("{\"results\": {");
			out.print("\"status\": \"");
			checkin(uscid, classid);
			out.println("\",");
			
			out.print("\"posts\":");
			checkMessages(classid);
			out.println("}}");
			
		// Fall 2013 Statistics
		} else if (Integer.parseInt(id) == 4) {
			String classid = request.getParameter("classid");
			out.print("{\"records\": {");
			getFallStatistics(classid);
			out.println("}}");
		// Insert Posts for the Class
		} else if (Integer.parseInt(id) == 5) {
			String classid = request.getParameter("classid");
			String post = request.getParameter("post");
			out.print("{\"results\": {");
			out.print("\"status\": \"");
			addPosts(classid, post);
			out.println("\",");
			out.println("\"posts\": \"\"}}");
		// Project Info
		} else if (Integer.parseInt(id) == 6) {

		}

	}

	void addPosts(String classid, String post) {
		if (connect == null)
			connectToDB();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy/MM/dd hh:mm:ss", new Locale("es", "ES"));
			DateFormat readFormat = new SimpleDateFormat(
					"EEE MMM dd HH:mm:ss ZZZ yyyy");

			java.util.Date dateread = new java.util.Date(System
					.currentTimeMillis());
			Date date = readFormat.parse(dateread.toString());
			String datestr = dateFormat.format(date);
			Date curdate = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss",
					new Locale("es", "ES")).parse(datestr);
			Timestamp curtime = new Timestamp(curdate.getTime());

			preparedStatement = connect
					.prepareStatement("INSERT INTO  taptastic.announcements(`class_id`,`post`,`ts`)"
							+ "VALUES(?,?,?)");

			preparedStatement.setString(1, classid);
			preparedStatement.setString(2, post);
			preparedStatement
					.setString(3, curtime.toString().replace('/', '-'));

			preparedStatement.executeUpdate();
			out.println("Successful Posting!");

		} catch (Exception e) {
			out.println(e);
			out.println("Error adding posts!");
		}
	}

	void checkMessages(String classid) {
	
		if (connect == null)
			connectToDB();
		try {
		    int flag = 1;
		    String opStr = "";
			String finalStr = "";
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy/MM/dd hh:mm:ss", new Locale("es", "ES"));
			DateFormat readFormat = new SimpleDateFormat(
					"EEE MMM dd HH:mm:ss ZZZ yyyy");

			Date preDateread = new Date(
					System.currentTimeMillis() - 24 * 3600 * 1000);
			Date preDate = readFormat.parse(preDateread.toString());
			String preDatestr = dateFormat.format(preDate);
			Date beforeDate = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss",
					new Locale("es", "ES")).parse(preDatestr);
			Timestamp beforetime = new Timestamp(beforeDate.getTime());

			statement = connect.createStatement();
			resultSet = statement
					.executeQuery("select post from announcements where class_id = '"
							+ classid + "' and ts >= '" + beforetime + "'");
			while (resultSet.next()) {
			    if(flag == 1) {
				    opStr+= "{";
                    flag = 0;				  
				}
			    opStr+="\"post\":";
				opStr+="\"" + resultSet.getString("post") + "\",";
			} 
			if(opStr.length() >= 1) {
                finalStr = opStr.substring(0, opStr.length()-1);
			    out.println(finalStr + "}");
			}
			else {
			    out.println("\" \"");
			}
		} catch (Exception e) {
			out.println(e);
			out.println("Error fetching Statistics!");
		}
	}

	void getFallStatistics(String classid) {
		connectToDB();
		String outputStr = "";
		String finalStr = "";
		try {
			statement = connect.createStatement();
			resultSet = statement
					.executeQuery("select usc_id,class_id,COUNT(*) AS No_of_classes_attended from timesheet where class_id = '"
							+ classid + "' group by usc_id");
			while (resultSet.next()) {
			    outputStr+="\"record\":{";
				
				outputStr+="\"id\":\"";
				outputStr+=resultSet.getString(1);
				outputStr+="\",";
				
				outputStr+="\"classid\":\"";
				outputStr+=resultSet.getString(2);
				outputStr+="\",";
				
				outputStr+="\"count\":\"";
				outputStr+=resultSet.getString(3);
				outputStr+="\"";
				
				outputStr+="},";

			}

			finalStr = outputStr.substring(0, outputStr.length()-1);
			out.println(finalStr);
		} catch (Exception e) {
			out.println("Error fetching Statistics!");
		}
	}

	void connectToDB() {
		try {
			// This will load the MySQL driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection(
					"jdbc:mysql://192.168.0.3:3306/taptastic", "tap", "");
			//out.println("Server Message: Connection to Database Established!!!");
		} catch (Exception e) {
			//out.println("Internal Error:Error Connecting to Database!!!");
		}
	}

	int checkExistingDB(String id, String user) {
		int flag = 1;
		try {
			statement = connect.createStatement();
			resultSet = statement
					.executeQuery("select * from users where usc_id = '" + id
							+ "'");
			if (resultSet.next()) {

				out.println("Student Id Already Exists!\n");
				flag = 0;
				return flag;
			}
			resultSet = statement
					.executeQuery("select * from users where uname = '" + user
							+ "'");
			if (resultSet.next()) {
				out.println("Username Already Exists!\n");
				flag = 0;
			}
		} catch (Exception e) {
		}
		return flag;
	}

	void checkin(String uscid, String classid) {
		connectToDB();
		Statement stmt = null;
		ResultSet result = null;
		try {
			statement = connect.createStatement();

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss", new Locale("es", "ES"));
			DateFormat readFormat = new SimpleDateFormat(
					"EEE MMM dd HH:mm:ss ZZZ yyyy");

			java.util.Date dateread = new java.util.Date(System
					.currentTimeMillis());
			Date curdate = readFormat.parse(dateread.toString());
			String datestr = dateFormat.format(curdate);
			curdate = dateFormat.parse(datestr);
			java.sql.Timestamp curtime = new java.sql.Timestamp(curdate
					.getTime());
			resultSet = statement
					.executeQuery("select * from csci588_schedule");

			while (resultSet.next()) {
				String date = resultSet.getString("class_date");
				String startdatetime = date + " " + resultSet.getString("class_start");
				String enddatetime = date + " " + resultSet.getString("class_end");
				startdatetime = startdatetime.replace('-', '/');
				enddatetime = enddatetime.replace('-', '/');

				java.util.Date fromDate = dateFormat.parse(startdatetime);
				java.sql.Timestamp fromtime = new java.sql.Timestamp(fromDate
						.getTime());

				java.util.Date toDate = dateFormat.parse(enddatetime);
				java.sql.Timestamp totime = new java.sql.Timestamp(toDate
						.getTime());

				if (curtime.after(fromtime) && curtime.before(totime)) {
					stmt = connect.createStatement();
					result = stmt
							.executeQuery("select timestamp from timesheet where usc_id = '"
									+ uscid
									+ "' and timestamp between '"
									+ startdatetime
									+ "' and '"
									+ enddatetime
									+ "'");
					if (result.next()) {
						out.println("Already checked in!!!");
						return;
					}
					String insQuery = "INSERT INTO timesheet(`usc_id`,`class_id`,`timestamp`)"
							+ "VALUES(?,?,?)";
					preparedStatement = connect.prepareStatement(insQuery);

					preparedStatement.setString(1, uscid);
					preparedStatement.setString(2, classid);
					preparedStatement.setString(3, curtime.toString().replace(
							'/', '-'));

					preparedStatement.executeUpdate();
					out.println("Successful Check-in!");
				}

			}

		} catch (Exception e) {
			out.println(e);
			out.println("Error while Checking-in!");
		}

	}

	void signUp(String uscid, String uname, String pwd) {
		connectToDB();
		int flag = checkExistingDB(uscid, uname);
		if (flag == 0) {
			return;
		}
		try {
			String insQuery = "INSERT INTO users(`usc_id`,`uname`,`pwd`,`type`)VALUES(?,?,?,?)";
			preparedStatement = connect.prepareStatement(insQuery);
			if (uscid.equals("0123456789"))
				preparedStatement.setString(4, "P");
			else
				preparedStatement.setString(4, "S");

			preparedStatement.setString(1, uscid);
			preparedStatement.setString(2, uname);
			preparedStatement.setString(3, pwd);
			preparedStatement.executeUpdate();
			out.println("User Registered!");

		} catch (Exception e) {
			out.println("Error Signing Up!");
		}

	}

	void login(String uname, String pwd) {
		connectToDB();
		try {
			statement = connect.createStatement();
			resultSet = statement
					.executeQuery("select * from users where uname = '" + uname
							+ "'");
			if (!resultSet.next()) {
				out.println("No Registered User Found!!!");
			} else {
				String p = resultSet.getString("pwd");

				if (p.equals(pwd)) {
					out.println("User Credentials Validated!");
				} else {
					out.println("User Credentials Cannot Be Validated!");
				}
			}
		} catch (Exception e) {
			out.print(e);
			out.println("Error Validating Credentials!");
		}
	}

}
