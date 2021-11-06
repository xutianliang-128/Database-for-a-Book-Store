import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class AdminSystem {
	public static Date systemDate;
    public static Date latestOrderDate;
    Connection connection;

    public void adminMenu(Scanner in, Connection connection) {
    	this.connection = connection;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String operation = "";

    	while (!operation.equals("5")){
        	System.out.println("<This is the system interface.>");
        	System.out.println("----------------------------------------------");
            System.out.println("1. Create Table.");
            System.out.println("2. Delete Table.");
            System.out.println("3. Insert Data.");
            System.out.println("4. Set System Date.");
            System.out.println("5. Back to main menu\n");
            System.out.print("Please enter your choice??..");

            String path = "";
            int opnum;
            try {
                operation = in.nextLine(); 
                opnum = Integer.parseInt(operation);
                
                if (opnum<1 || opnum>5){
                    System.out.println("[ERROR] Invalid input.");
                    System.out.println("Please enter [1-5]");
                    continue;
                }
            } catch(Exception e){
            	System.out.println("[ERROR] Invalid input.");
                System.out.println("Please enter [1-5]");
                continue;
            }
            

            switch (opnum) {
                case 1:
                    createTable();
                    break;
                case 2:
                    deleteTable();
                    break;
                case 3:
                    System.out.println("Please enter the folder path");
                    path = in.nextLine();
                    latestOrderDate = insertData(path);
                    break;
                case 4:
                	System.out.print("Please Input the date (YYYYMMDD): ");
                	String dateString = "";
                	dateString = in.nextLine();
                	StringBuilder sb = new StringBuilder(dateString);
                	sb.insert(4,"-");
                	sb.insert(7,"-");
                	dateString = sb.toString();
                	
                	Date inputDate = new Date(0);
                	try {
                    	inputDate = formatter.parse(dateString);
                	}catch(Exception e){
                		e.printStackTrace();
                		break;
                	}
                	
                	System.out.println("Latest date in orders: " + formatter.format(latestOrderDate));
                    updateSystemDate(inputDate);
                    System.out.println("Today is " + formatter.format(systemDate));
                    break;
                case 5: //back to menu
                    break;
                default:
            }
    	}
    }
    
    public void createTable(){
    	try {
    		 Statement stmt = connection.createStatement();    		
    		 
    		 //Book
    		 stmt.executeUpdate("CREATE TABLE Book "
    		 		+ "(ISBN varchar(13), "
    		 		+ "title varchar(100) NOT NULL, "
    		 		+ "unit_price int unsigned, "
    		 		+ "no_of_copies int unsigned, "
    		 		+ "PRIMARY KEY(ISBN));");
    		 //Customer
    		 stmt.executeUpdate("CREATE TABLE Customer "
    		 		+ "(customer_id varchar(10), "
    		 		+ "name varchar(50) NOT NULL, "
    		 		+ "shipping_address varchar(200) NOT NULL, "
    		 		+ "credit_card_no varchar(19), "
    		 		+ "PRIMARY KEY(customer_id));");
    		 //Orders
    		 stmt.executeUpdate("CREATE TABLE Orders "
    		 		+ "(order_id varchar(8), "
    		 		+ "customer_id varchar(10) NOT NULL, "
    		 		+ "o_date date, "
    		 		+ "shipping_status varchar(1), "
    		 		+ "charge int unsigned, "
    		 		+ "PRIMARY KEY(order_id), "
    		 		+ "FOREIGN KEY(customer_id) REFERENCES Customer(customer_id) "
    		 		+ "ON DELETE NO ACTION);");
    		 //Ordering
    		 stmt.executeUpdate("CREATE TABLE Ordering "
    		 		+ "(order_id varchar(8) NOT NULL, "
    		 		+ "ISBN varchar(13) NOT NULL, "
    		 		+ "quantity int, "
    		 		+ "PRIMARY KEY(order_id,ISBN), "
    		 		+ "FOREIGN KEY(order_id) REFERENCES Orders(order_id) ON DELETE NO ACTION, "
    		 		+ "FOREIGN KEY(ISBN) REFERENCES Book(ISBN) ON DELETE NO ACTION);");
    		 //book author
    		 stmt.executeUpdate("CREATE TABLE Book_author "
    		 		+ "(ISBN varchar(13) NOT NULL, "
    		 		+ "author_name varchar(50) NOT NULL, "
    		 		+ "PRIMARY KEY(ISBN, author_name), "
    		 		+ "FOREIGN KEY(ISBN) REFERENCES Book(ISBN) ON DELETE NO ACTION);"); 
    		 
    		 System.out.println("Done! Tables are created!");
    	} catch (Exception e) {
            e.printStackTrace();
        }
       
    }
    
    public void deleteTable(){
    	try {
    		Statement stmt = connection.createStatement();
    		stmt.executeUpdate("DROP TABLE IF EXISTS Book_author");
    		stmt.executeUpdate("DROP TABLE IF EXISTS Ordering");
    		stmt.executeUpdate("DROP TABLE IF EXISTS Orders");
            stmt.executeUpdate("DROP TABLE IF EXISTS Customer");
            stmt.executeUpdate("DROP TABLE IF EXISTS Book");
            

            System.out.println("Done! Tables are deleted");
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    public Date insertData(String path){	
    	Date date = new Date();
    	date.setTime(0);
    	try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Book VALUES (?,?,?,?);");
            BufferedReader reader = new BufferedReader(new FileReader(path+"\\book.txt"));
            String line = null;
            while((line=reader.readLine()) != null){
                String item[] = line.split("\\|");//split by |
                stmt.setString(1,item[0]);//isbn
                stmt.setString(2,item[1]);//name
                stmt.setString(3,item[2]);//unit price
                stmt.setInt(4,Integer.parseInt(item[3]));//no of copies
                stmt.executeUpdate();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }//Book

        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Customer VALUES (?,?,?,?);");
            BufferedReader reader = new BufferedReader(new FileReader(path+"\\customer.txt"));
            String line = null;
            while((line=reader.readLine())!=null){
                String item[] = line.split("\\|");
                stmt.setString(1,item[0]);//id
                stmt.setString(2,item[1]);//name
                stmt.setString(3,item[2]);//shipping_address
                stmt.setString(4,item[3]);//card_no
                stmt.executeUpdate();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }//Customer

        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Orders VALUES (?,?,?,?,?);");
            BufferedReader reader = new BufferedReader(new FileReader(path+"\\orders.txt"));
            String line = null;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            while((line=reader.readLine())!=null){
                String item[] = line.split("\\|");
                java.util.Date d = formatter.parse(item[1]);
                if (date.before(d)) {
                	date=d;
                }
                stmt.setString(1,item[0]);//name
                stmt.setDate(3, new java.sql.Date(d.getTime()));
                stmt.setString(4,item[2]);//shipping_status
                stmt.setString(2,item[4]);//customer_id
                stmt.setInt(5,Integer.parseInt(item[3]));//charge
                stmt.executeUpdate();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }//Orders
        
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Ordering VALUES (?,?,?);");
            BufferedReader reader = new BufferedReader(new FileReader(path+"\\ordering.txt"));
            String line = null;
            while((line=reader.readLine())!=null){
                String item[] = line.split("\\|");
                stmt.setString(1,item[0]);//name
                stmt.setString(2,item[1]);//isbn
                stmt.setInt(3,Integer.parseInt(item[2]));//quantity
                stmt.executeUpdate();
            } 
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }//Ordering 

        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Book_author VALUES (?,?);");
            BufferedReader reader = new BufferedReader(new FileReader(path+"\\book_author.txt"));
            String line = null;
            while((line=reader.readLine())!=null){
                String item[] = line.split("\\|");
                stmt.setString(1,item[0]);//isbn
                stmt.setString(2,item[1]);//name
                stmt.executeUpdate();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }//Book_author
        
        System.out.println("Data is loaded!");
        
        return date;
    }


    public void updateSystemDate(Date inputDate) {
    	if (latestOrderDate.after(inputDate) && latestOrderDate.after(systemDate)) {
    		systemDate.setTime(latestOrderDate.getTime());
    	}else if(inputDate.after(systemDate) && inputDate.after(latestOrderDate)) {
    		systemDate.setTime(inputDate.getTime());
    	}
    }    
}