import java.util.Scanner;
import java.util.Date;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.SimpleDateFormat;

public class MainMenu {
    Scanner in;
    AdminSystem sys;
    Customer customer;
    Bookstore bookstore;

	public MainMenu() {
		AdminSystem.systemDate = new Date(0l);
		AdminSystem.latestOrderDate = new Date(0l);
	    in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
	}
	
	public void menu0(Connection connection) {
    	String operation = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        //Date date = new Date(AdminSystem.systemDate.getTime());
        
    	//System.out.println("The system date is now " + formatter.format(AdminSystem.systemDate));
    	System.out.println("The system date is now 0000-00-00");

    	while (!operation.equals("5")){
    		System.out.println("<This is the Book Ordering System.>");
        	System.out.println("----------------------------------------------");
            System.out.println("1. System interface.");
            System.out.println("2. Customer interface.");
            System.out.println("3. Bookstore interface.");
            System.out.println("4. Show System Date.");
            System.out.println("5. Quit the system......\n");
            System.out.print("Please enter your choice??..");
            
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
            
            switch (opnum){
                case 1:
                	sys = new AdminSystem();
                    sys.adminMenu(in, connection);
                    break;
                case 2:
                	customer = new Customer();
                	customer.customerMenu(in, connection);
                    break;
                case 3:
                	bookstore = new Bookstore();
                	bookstore.bookstoreMenu(in, connection);
                    break;
                case 4:
                	System.out.println("The system date is now "+ formatter.format(AdminSystem.systemDate));
                    break;
                case 5:
                    break;
                default:
                    System.out.println("[ERROR] Invalid input.");
            }
        }
        System.out.println("Quit!");
    }
}