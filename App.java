import java.sql.*;

public class App {

	public static void main(String[] args) throws Exception{
		/*
		String dbAddress = "jbdc:mysql://projgw.cse.cuhk.edu.hk:2633/db5";
        String dbUsername = "Group5";
        String dbPassword = "CSCI3170";
        */
		/*
		String dbAddress = "jdbc:mysql://localhost:3306/test";
        String dbUsername = "root";
        String dbPassword = "ILoveAsher";
    	*/
		
		String dbAddress = "jdbc:mysql://localhost:3306/csci3170?useSSL=false";
        String dbUsername = "root";
        String dbPassword = "123456";
        
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
        } catch (ClassNotFoundException e) {
            System.out.println("[ERROR]: Java MySQL DB Driver not found!!");
            System.exit(0);
        } catch (SQLException e){
            System.out.println(e);
        }
	        
        MainMenu mainMenu = new MainMenu();
        mainMenu.menu0(connection);
        
	}
}
