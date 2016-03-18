package main;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class HomePageServlet extends HttpServlet 
{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException 
	{

		   
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		
		RequestDispatcher header = req.getRequestDispatcher("Header.html"); 
		header.include(req, resp);
		            
		String name= req.getParameter("q");
		out.println("<p class='add-result'>"+name+"</p>");
		
		
		RequestDispatcher main = req.getRequestDispatcher("default.html");
		main.include(req, resp);


		RequestDispatcher footer = req.getRequestDispatcher("Footer.html");
		footer.include(req, resp);
		
	}//End of doPost
}//End of Class
