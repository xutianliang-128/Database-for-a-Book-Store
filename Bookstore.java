import java.sql.*;
import java.util.Scanner;

public class Bookstore {
	Connection connection;
	Scanner in;
	
	
	public void bookstoreMenu(Scanner in, Connection connection) {
		this.connection = connection;
		this.in = in;
		
		String operation = "";
		while(!operation.equals("4")){
			System.out.println("<This is the bookstore interface.>");
			System.out.println("----------------------------------------------");
			System.out.println("1. Order Update.");
			System.out.println("2. Order Query.");
			System.out.println("3. N most Popular Book Query.");
			System.out.println("4. Back to main menu\n");
			System.out.print("What is your choice?..");
			
            int opnum;
			try {
				operation = in.nextLine(); 
                opnum = Integer.parseInt(operation); 
				if (opnum<1 || opnum>4){
					System.out.println("[ERROR] Invalid input.");
					System.out.println("Please enter [1-4]");
					continue;
				}
			} catch(Exception e){
				System.out.println("[ERROR] Invalid input.");
				System.out.println("Please enter [1-4]");
				continue;
			}

			switch (opnum) {
				case 1:
	                orderUpdate();
	                break;
	            case 2:
	            	orderQuery();
	                break;
	            case 3:
	            	nMostPopularBookQuery();
	            	break;
	            case 4: // just go back to menu
	            	break;
	            default: //
	        }
		}
	}
	
	public void orderUpdate() {
		System.out.print("Please input the order ID: ");
		String operation = "";
		String id = in.nextLine();
		try {
			PreparedStatement stmt = connection.prepareStatement(
					"SELECT Orders.order_id, Orders.shipping_status, Ordering.quantity, Orders.charge "
					+ "FROM Orders "
					+ "INNER JOIN Ordering ON Orders.order_id = Ordering.order_id "
					+ "WHERE Orders.order_id = ?;");
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();
			
			String shipping_sta = "";
			int no_books = 0;
			int charge = 0;
			while(rs.next()) {
				shipping_sta = rs.getString(2);
				no_books += rs.getInt(3);
				charge = rs.getInt(4);
			}
			System.out.println("the Shipping status of " 
					+ id 
					+ " is "
					+ shipping_sta
					+ " and "
					+ no_books 
					+ " books ordered");
			
			if (shipping_sta.equals("N")) {
				if (charge == 0) {
					System.out.println("The ordered copy is null!");
				}else {
					System.out.print("Are you sure to update the shipping status? (Yes=Y) ");
					operation = in.nextLine();
					if (operation.equals("Y")) {
						PreparedStatement stmt1 = connection.prepareStatement("UPDATE Orders SET shipping_status = \"Y\" WHERE order_id = ?;");
						stmt1.setString(1, id);
						stmt1.executeUpdate();
						System.out.println("Updated shipping status");
					}
				}
			}else {
				System.out.println("The shipping status Y is not allowed updating!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void orderQuery(){
		
		String input = "";
		int o_year = 0;
		int o_month = 0;
        System.out.print("Please Input the Month for Order Query (e.g. 2005-09): ");
        input = in.nextLine();
        System.out.println("\n");
        o_year = Integer.parseInt(input.substring(0,4));
        o_month = Integer.parseInt(input.substring(input.length() - 2));//get order year and month input
        String shipsta = "Y";

		try {
			PreparedStatement stmt =  connection.prepareStatement(
					"SELECT * FROM Orders "
					+ "WHERE shipping_status = ? AND YEAR(o_date) = ? AND MONTH(o_date) = ? "
					+ "ORDER BY order_id");//be sorted in ascending order by Order ID

			stmt.setString(1, shipsta);
			stmt.setInt(2, o_year);
			stmt.setInt(3, o_month);
			ResultSet rs = stmt.executeQuery();//select from Orders
			int record = 1;
			int chargesum = 0;
			
			while(rs.next()) {
				int charge = rs.getInt(5);
				chargesum = chargesum + charge;//calculate sum of charge
	            System.out.println("Record : "+ record);
	            record++;
	            System.out.println("order_id : "+ rs.getString(1));
	            System.out.println("customer_id : "+ rs.getString(2));
	            System.out.println("date : "+ rs.getDate(3));
	            System.out.println("charge : "+ charge +"\n\n");

			}
			System.out.println("Total charges of the month is "+ chargesum);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void nMostPopularBookQuery() {
		System.out.print("Please input the N popular books number: ");
		try {
			//TODO
			//Syntax error found at 03:47
			/*com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException: 
			 * You have an error in your SQL syntax; check the manual that corresponds to
			 *  your MySQL server version for the right syntax to use near
			 *   '(ORDER BY num DESC) AS rFROM (SELECT ISBN, SUM(quantity) AS numFROM Ordering GRO' at line 1*/
			String n = "";			
			n = in.nextLine();
			PreparedStatement stmt = connection.prepareStatement(
					"SELECT A.ISBN, B.title, A.num FROM "
					+ "(SELECT ISBN, num, @curRank := @curRank + 1 AS rank "
					+ "FROM (SELECT ISBN, SUM(quantity) AS num "
					+ "FROM Ordering GROUP BY ISBN) n, (SELECT @curRank := 0) r "
					+ "ORDER BY num DESC) A, Book B "
					+ "WHERE rank <= ? AND A.ISBN = B.ISBN;");
			stmt.setString(1, n);
			ResultSet rs = stmt.executeQuery();
			System.out.println("ISBN            " + "Title            " + "copies");
			while(rs.next()) {
				System.out.print(rs.getString(1) + "   ");
				System.out.print(rs.getString(2) + "      ");
				System.out.println(rs.getInt(3));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
