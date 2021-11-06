import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Customer {
	Connection connection;
	Scanner in;
	
	public void customerMenu(Scanner in, Connection connection) {
		this.connection = connection;
		this.in = in;
		
		String operation = "";
    	while (!operation.equals("5")){
    		System.out.println("<This is the customer interface.>");
        	System.out.println("----------------------------------------------");
            System.out.println("1. Book Search.");
            System.out.println("2. Order Creation.");
            System.out.println("3. Order Altering.");
            System.out.println("4. Order Query.");
            System.out.println("5. Back to main menu\n");
            System.out.print("What is your choice?..");

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
                    searchBook();
                    break;
                case 2:
                    orderCreation();
                    break;
                case 3:
                	orderAltering();
                	break;
                case 4:
                	orderQuery();
                	break;
                case 5: 
                    break;
                default: 
            }
    	}
	}
	
	
	public void searchBook() {
		String operation = "";
	
    	while(!operation.equals("4")) {
    		System.out.println("What do u want to search??");
    		System.out.println("1 ISBN");
    		System.out.println("2 Book Title");
    		System.out.println("3 Author Name");
    		System.out.println("4 Exit");
    		System.out.print("Your choice?... ");
    		
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
                searchISBN();
                break;
            case 2:
            	searchBookTitle();
                break;
            case 3:
            	searchAuthorName();
            	break;
            case 4: // just go back to menu
            	break;
            default: 
        	}
			System.out.println("Operation not allowed after ResultSet closed cannot query the book\n");
        }
	}

	public void searchISBN() {
		System.out.print("Input the ISBN: ");
		String isbn = "";
		try {
			PreparedStatement stmt =  connection.prepareStatement(
					"SELECT * FROM Book "
					+ "WHERE ISBN = ?;");
			PreparedStatement stmt1 = connection.prepareStatement(
					"SELECT * FROM Book_author "
					+ "WHERE ISBN = ? "
					+ "ORDER BY author_name;");
			isbn = in.nextLine();
			System.out.println("");
			stmt.setString(1, isbn);
			stmt1.setString(1, isbn);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				System.out.println("Record 1");
				System.out.println("ISBN: " + rs.getString(1));
				System.out.println("Book Title: " + rs.getString(2));
				System.out.println("Unit Price: " + rs.getInt(3));
				System.out.println("No Of Available: " + rs.getInt(4));
			}
			System.out.println("Authors:");
			ResultSet rs1 = stmt1.executeQuery();
			int i=1;
			while(rs1.next()) {
				System.out.println(i + " :" + rs1.getString(2));
				i++;
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void searchBookTitle() {
		System.out.print("Input the book title: ");
		String bt = "";
		try {
			PreparedStatement stmt =  connection.prepareStatement(
					"SELECT * FROM Book "
					+ "WHERE title LIKE ? "
					+ "ORDER BY title;");
			bt = in.nextLine();
			stmt.setString(1, bt);
			
			ResultSet rs = stmt.executeQuery();
			int j = 1;
			while(rs.next()) {
				System.out.println("");
				System.out.println("Record " + j);
				j++;
				System.out.println("ISBN: " + rs.getString(1));
				System.out.println("Book Title: " + rs.getString(2));
				System.out.println("Unit Price: " + rs.getInt(3));
				System.out.println("No Of Available: " + rs.getInt(4));
				System.out.println("Authors:");
				PreparedStatement stmt1 = connection.prepareStatement(
						"SELECT * FROM Book_author "
						+ "WHERE ISBN = ? "
						+ "ORDER BY author_name;");
				stmt1.setString(1, rs.getString(1));
				ResultSet rs1 = stmt1.executeQuery();
				int i=1;
				while(rs1.next()) {
					System.out.println(i + " :" + rs1.getString(2));
					i++;
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void searchAuthorName() {
		System.out.print("Input the author name: ");
		String an = "";
		try {
			PreparedStatement stmt =  connection.prepareStatement(
					"SELECT Book_author.ISBN, Book_author.author_name, Book.title "
					+ "FROM Book_author INNER JOIN Book "
					+ "ON Book_author.ISBN = Book.ISBN "
					+ "WHERE author_name LIKE ? "
					+ "ORDER BY title, ISBN;");
			an = in.nextLine();
			stmt.setString(1, an);
			ResultSet rs = stmt.executeQuery();
			
			int j = 1;
			while(rs.next()) {
				PreparedStatement stmt1 = connection.prepareStatement(
						"SELECT * FROM Book "
						+ "WHERE ISBN = ?");
				stmt1.setString(1, rs.getString(1));
				ResultSet rs1 = stmt1.executeQuery();
				System.out.println("");
				System.out.println("Record " + j); 
				j++;
				
				while(rs1.next()) {
					System.out.println("ISBN: " + rs1.getString(1)); 
					System.out.println("Book Title: " + rs1.getString(2));
					System.out.println("Unit Price: " + rs1.getInt(3));
					System.out.println("No Of Available: " + rs1.getInt(4));
				}
				
				System.out.println("Authors:");
				PreparedStatement stmt2 = connection.prepareStatement(
						"SELECT * FROM Book_author "
						+ "WHERE ISBN = ? "
						+ "ORDER BY author_name;");
				stmt2.setString(1, rs.getString(1));
				ResultSet rs2 = stmt2.executeQuery();
				int i=1;
				while(rs2.next()) {
					System.out.println(i + " :" + rs2.getString(2));
					i++;
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void orderCreation() {
		String customerID = "";
		String isbn = "";
		String orderID = "";
	    String shippingStatus = "N";
	    int charge = 0;
	    ArrayList<String> isbns = new ArrayList<String>();
	    ArrayList<Integer> quantities = new ArrayList<Integer>();
	    ArrayList<Integer> unitPrices = new ArrayList<Integer>();
		
		System.out.print("Please enter your customerID??");
		customerID = in.nextLine();
        
        System.out.println(">> What books do you want to order??");
        System.out.println(">> Input ISBN and then the quantity.");
        System.out.println(">> You can press \"L\" to see ordered list, or \"F\" to finish ordering.");
        	
        boolean flag = true;
        
        while(flag) {
        	System.out.print("Please enter the book's ISBN: ");
           	isbn = in.nextLine();
    		int request = -1;

           	if (isbn.equals("L")) {
           		orderedList(isbns, quantities);
           	} else if (isbn.equals("F")) {
           		uploading(customerID, quantities, charge, isbns, unitPrices, orderID, shippingStatus);
           		flag = false;
           		break;
           	} else {
           		while(request < 0) {
           			System.out.print("Please enter the quantity of the order: ");
           			String requestString = "";
           			try {
           				requestString = in.nextLine(); 
	                    request = Integer.parseInt(requestString);
	                    if(request < 0) {
	                    	System.out.println("[ERROR] Invalid input.");
	                    	continue;
	                    }
	                } catch(Exception e){
	                	System.out.println("[ERROR] Invalid input.");
	                	continue;
	                }
           		}
           		orderID = startCreate(isbn, request, isbns, quantities, unitPrices, orderID);
           	}
        } 
	}
	
	public void orderedList(ArrayList<String> isbns, ArrayList<Integer> quantities) {
		System.out.println("ISBN          Number:");
		int size;
		size = isbns.size();
		for(int i=0; i < size; i++) {
			System.out.println(isbns.get(i) + "   " + quantities.get(i));
		}
	}
	
	public void uploading(String customerID, ArrayList<Integer> quantities, int charge,
			ArrayList<String> isbns, ArrayList<Integer> unitPrices, String orderID, 
			String shippingStatus) {
		
		// Calculate charge
		int sum = 0;
		for(int j:quantities) {
			sum += j;
		}
		if(sum == 0) {
			charge = 0;
		} else {
			int book_price = 0;
			for (int k=0; k<isbns.size(); k++) {
				book_price = book_price + quantities.get(k)*unitPrices.get(k);
			}
			charge = book_price + sum*10 + 10;
		} 
		
		// Update TABLE Orders
		try {
			PreparedStatement stmt =  connection.prepareStatement(
					"INSERT INTO Orders VALUES (?,?,?,?,?);");
			stmt.setString(1, orderID);
			stmt.setString(2, customerID);
			stmt.setDate(3, new java.sql.Date(AdminSystem.systemDate.getTime()));
			stmt.setString(4, shippingStatus);
			stmt.setInt(5, charge);
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Update TABLE Ordering
		try {
			PreparedStatement stmt =  connection.prepareStatement(
					"INSERT INTO Ordering VALUES (?,?,?);");
			for(int i=0; i<isbns.size(); i++) {
				stmt.setString(1, orderID);
				stmt.setString(2, isbns.get(i));
				stmt.setInt(3, quantities.get(i));
				stmt.executeUpdate();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		// Update TABLE Book
		// save the previous stock
		
		ArrayList<Integer> preStock = new ArrayList<Integer>();
		try {
			for(int i=0; i<isbns.size(); i++) {
				PreparedStatement stmt1 =  connection.prepareStatement(
						"SELECT * FROM Book "
						+ "WHERE ISBN = ?;");
				stmt1.setString(1, isbns.get(i));
				ResultSet rs1 = stmt1.executeQuery();
				while(rs1.next()) {
					preStock.add(rs1.getInt(4));
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			PreparedStatement stmt = connection.prepareStatement(
					"UPDATE Book "
					+ "SET no_of_copies = ? "
					+ "WHERE ISBN = ?;");
			for(int i=0; i<isbns.size(); i++) {
				
				int updateStock = 0;
				updateStock = preStock.get(i)-quantities.get(i);
				stmt.setInt(1, updateStock);
				stmt.setString(2, isbns.get(i));
				stmt.executeUpdate();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public String startCreate(String isbn, int request, ArrayList<String> isbns, ArrayList<Integer> quantities,
			ArrayList<Integer> unitPrices, String orderID) {
		try {
			PreparedStatement stmt1 =  connection.prepareStatement(
					"SELECT * FROM Book "
					+ "WHERE ISBN = ?;");
			stmt1.setString(1, isbn);
			ResultSet rs = stmt1.executeQuery();
			while(rs.next()) {
				int unit_price;
				unit_price = rs.getInt(3);
				int available_num;
				available_num = rs.getInt(4);
				if (available_num < request) {
					System.out.println("The book copies are not available. In stock: "+ available_num);
					// 
				} else { // Build an order
					isbns.add(isbn);
					quantities.add(request);
					unitPrices.add(unit_price);
					
					// Count previous orders
					PreparedStatement stmt0 = connection.prepareStatement(
							"SELECT COUNT(*) AS rowcount FROM Orders;");
					ResultSet rs0 = stmt0.executeQuery();
					int count = 0;
					while(rs0.next()) {
						count = rs0.getInt("rowcount");
					}
					// Assign order_id from 00000000
					Integer Count =  count;
					orderID = Count.toString();
					int len = orderID.length();
					for (int i = 0; i < (8-len); i++) {
						orderID = "0" + orderID;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderID;
	}
	

	//-------------------------------------start
	public void orderAltering() {
		String order_id = "";
		System.out.print("Please enter the OrderID that you want to change: ");
		order_id = in.nextLine();//get order id
	    ArrayList<String> isbn_list = new ArrayList<String>();
	    ArrayList<Integer> quantity_list = new ArrayList<Integer>();
	    String shipping_sta = "";
	    int charge = 0;
	    String customer_id = "";
		
		try {PreparedStatement stmt1 =  connection.prepareStatement(	
				"SELECT * FROM Orders "
				+ "WHERE order_id = ?;");

			stmt1.setString(1, order_id);
			ResultSet rs1 = stmt1.executeQuery();//select from Orders according to order_id
			
			while(rs1.next()) {
				shipping_sta = rs1.getString(4);
				charge = rs1.getInt(5);
				customer_id = rs1.getString(2);
				System.out.println("order_id:" 
						+ order_id 
						+ "  shipping:"
						+ shipping_sta
						+ "  charge="
						+  charge
						+ "  customerID="
						+ customer_id)	;		
				PreparedStatement stmt2 = connection.prepareStatement(
						"SELECT * FROM Ordering WHERE " + 
						"order_id = ? ;");//select from Ordering to find list of ISBN (Book No.)
				stmt2.setString(1, order_id);
				ResultSet rs2 = stmt2.executeQuery();
				int record = 1;
				while(rs2.next()) {
					String ISBN = rs2.getString(2);
					int quantity = rs2.getInt(3);
					isbn_list.add(ISBN);//isbn list is in pair with book no. record
					quantity_list.add(quantity);
					System.out.println("book no: " + record+" ISBN = " + ISBN + " quantity = " + quantity);
					record++;
				}

			}
			int book_no = 0;
			int opnum = 0;
			System.out.println("Which book you want to alter (input book no.): ");
			String book_no_string = in.nextLine();//get book_no that the customer want to alter
			book_no = Integer.parseInt(book_no_string);
			String isbn_alter = isbn_list.get(book_no-1);//get isbn from book_no
			int quantity_alter = quantity_list.get(book_no-1);//get quantity from book_no
			System.out.println("input add or remove ");
			String operation = "";
			operation = in.nextLine();
			System.out.println("input the number: ");
			String opnum_string = in.nextLine();
			opnum = Integer.parseInt(opnum_string);//get number that to add or to remove
			
			//get info from certain order_id and isbn : isbn_alter, opnum, shipping_sta, quantity_alter, no_of_copies, unit_price
			PreparedStatement stmt4 = connection.prepareStatement(
					"SELECT * FROM Book WHERE " + 
					"ISBN = ? ;");//get no_of_copies from ISBN
			stmt4.setString(1, isbn_alter);
			ResultSet rs4 = stmt4.executeQuery();
			int no_of_copies = 0;
			int unit_price = 0;
			while(rs4.next()) {
				no_of_copies = rs4.getInt(4);
				unit_price = rs4.getInt(3);
			}
			
			//add or remove
			if (operation.equals("add")) {
				if(shipping_sta.equals("Y")){
					System.out.println("The books in the order are shipped.");
				}else {//not shipped yet
					if(opnum > no_of_copies) {
						System.out.println("There are not enough books available to add.");
					}else {
						int new_quantity = quantity_alter + opnum;
						PreparedStatement stmt5 = connection.prepareStatement(
								"UPDATE Ordering "
								+ "SET quantity = ? "
								+ "WHERE order_id = ? AND ISBN = ? ;");
						stmt5.setInt(1, new_quantity);
						stmt5.setString(2, order_id);
						stmt5.setString(3, isbn_alter);
						stmt5.executeUpdate();//update quantity
						
						int new_charge = charge + (unit_price+10) * opnum;
						PreparedStatement stmt6 = connection.prepareStatement(
								"UPDATE Orders "
								+ "SET o_date = ?, charge = ? WHERE "
								+ "order_id = ? ;");
						stmt6.setDate(1, new java.sql.Date(AdminSystem.systemDate.getTime()));
						stmt6.setInt(2, new_charge);
						stmt6.setString(3, order_id);
						stmt6.executeUpdate();//update o_date and charge
						
						int new_no_of_copies = no_of_copies - opnum;
						PreparedStatement stmt7 = connection.prepareStatement(
								"UPDATE Book "
								+ "SET no_of_copies = ? WHERE " + 
								"ISBN = ? ;");
						stmt7.setInt(1, new_no_of_copies);
						stmt7.setString(2, isbn_alter);
						stmt7.executeUpdate();//update no_of_copies
									
						System.out.println("Update is ok!");
						System.out.println("Update is done!!");
						System.out.println("Updated charge");
						//print new result
						System.out.println("order_id:" 
								+ order_id 
								+ "  shipping:"
								+ shipping_sta
								+ "  charge="
								+  charge
								+ "  customerID="
								+ customer_id
								)	;
						//still, list all books ordered with updated quantity
						
						PreparedStatement stmt2 = connection.prepareStatement(
								"SELECT * FROM Ordering WHERE " + 
								"order_id = ? ;");//select from Ordering to find list of ISBN (Book No.)
						stmt2.setString(1, order_id);
						ResultSet rs2 = stmt2.executeQuery();
						int record = 1;
						while(rs2.next()) {
							String ISBN = rs2.getString(2);
							int quantity = rs2.getInt(3);
							isbn_list.add(ISBN);//isbn list is in pair with book no. record
							quantity_list.add(quantity);
							System.out.println("book no: " + record+" ISBN = " + ISBN + " quantity = " + quantity);
							record++;
						}
					}
				}
			}else if(operation.equals("remove")){//remove
				if(shipping_sta.equals("Y")){
					System.out.println("The books in the order are shipped.");
				}else {//not shipped yet
					int new_quantity = quantity_alter - opnum;
					PreparedStatement stmt8 = connection.prepareStatement(
							"UPDATE Ordering "
							+ "SET quantity = ? "
							+ "WHERE order_id = ? and ISBN = ? ;");
					stmt8.setInt(1, new_quantity);
					stmt8.setString(2, order_id);
					stmt8.setString(3, isbn_alter);
					stmt8.executeUpdate();//update quantity
					
					int new_charge = charge - (unit_price +10) * opnum;
					PreparedStatement stmt9 = connection.prepareStatement(
							"UPDATE Orders "
							+ "SET o_date = ?, charge = ? "
							+ "WHERE order_id = ?;");
					stmt9.setDate(1, new java.sql.Date(AdminSystem.systemDate.getTime()));
					stmt9.setInt(2, new_charge);
					stmt9.setString(3, order_id);
					stmt9.executeUpdate();//update o_date and charge
					
					int new_no_of_copies = no_of_copies + opnum;
					PreparedStatement stmt10 = connection.prepareStatement(
							"UPDATE Book "
							+ "SET no_of_copies = ? WHERE " + 
							"ISBN = ? ;");
					stmt10.setInt(1, new_no_of_copies);
					stmt10.setString(2, isbn_alter);
					stmt10.executeUpdate();//update no_of_copies
					
					System.out.println("Update is ok!");
					System.out.println("Update is done!!");
					System.out.println("Updated charge");
					//print new result
					System.out.println("order_id : " 
							+ order_id 
							+ " shipping: "
							+ shipping_sta
							+ " charge = "
							+  new_charge
							+ " customerid = "
							+ customer_id
							)	;
					//still, list all books ordered with updated quantity
					PreparedStatement stmt2 = connection.prepareStatement(
							"SELECT * FROM Ordering WHERE " + 
							"order_id = ? ;");//select from Ordering to find list of ISBN (Book No.)
					stmt2.setString(1, order_id);
					ResultSet rs2 = stmt2.executeQuery();
					int record = 1;
					while(rs2.next()) {
						String ISBN = rs2.getString(2);
						int quantity = rs2.getInt(3);
						isbn_list.add(ISBN);//isbn list is in pair with book no. record
						quantity_list.add(quantity);
						System.out.println("book no: " + record+" ISBN = " + ISBN + " quantity = " + quantity);
						record++;
					}
					
				}
			}else {
				System.out.println("Please select add or remove!");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	
	public void orderQuery() {
		String customerID = "";
        int orderYear = 0;
        String orderYearString = "";
        System.out.print("Please Input Customer ID: ");
        customerID = in.nextLine();//get customer id input
        System.out.print("Please Input the Year: ");//get order year input
        orderYearString = in.nextLine();
        orderYear = Integer.parseInt(orderYearString);
        
        try {
			PreparedStatement stmt =  connection.prepareStatement("SELECT * FROM Orders WHERE " +
		"customer_id = ? AND YEAR(o_date) = ? "+ "ORDER BY order_id;");

			stmt.setString(1, customerID);
			stmt.setInt(2, orderYear);
			ResultSet rs = stmt.executeQuery();//select from Orders
			int record = 1;
			
			while(rs.next()) {
	            System.out.println("Record : "+ record);
	            record++;
	            System.out.println("OrderID : "+ rs.getString(1));
	            System.out.println("OrderDate : "+ rs.getDate(3));
	            System.out.println("charge : "+ rs.getInt(5));
	            System.out.println("shipping status : "+ rs.getString(4));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

