package main;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class Conn_add_new extends HttpServlet 
{
    private static Client getClient() throws UnknownHostException {
		Settings settings = Settings.settingsBuilder()
		        .put("cluster.name", "my-application").build();
		Client client = TransportClient.builder().settings(settings).build().
				addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        return client;
    }  
    private static String INDEX_NAME="dell_wiki";
    private static String DOC_TYPE="string";
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException 
	{
		String taskTitle = req.getParameter("postVariableName").trim();
		System.out.println(taskTitle);
		//for getting match value
		if(taskTitle.equals("gettingTheDoc"))
		{
			String search_str=req.getParameter("search_text");
			resp.setContentType("text/html");
			PrintWriter out = resp.getWriter();
			
			//fetching all the doc and putting in html format
			final Client client = getClient();
			SearchResponse response = client.prepareSearch(INDEX_NAME).setTypes(DOC_TYPE).setQuery(QueryBuilders.queryStringQuery("*"+search_str+"*")).setSize(40).execute().actionGet();
		    SearchHits searchHits = response.getHits();
		    SearchHit[] hits = searchHits.getHits();
			out.println("<span id='no-of-doc' style='display:none'>"+hits.length+"</span");
		    for (int i = 0; i <hits.length; i++) {
			        SearchHit hit = hits[i];
			        Map<String, Object> result = hit.getSource();
			        int noOfColumn=result.size();
			    	Set s=result.keySet();
			    	Iterator ref=s.iterator();
				   out.println("<article id='post-1' class=' post-1 topic type-topic status-publish hentry topic-tag-basic topic-tag-suggestion'>");
				   out.println("<header class='clearfix'>");
				   out.println("<h3 class='post-title gotham-rounded-bold'>");
				   out.println("<i class='doc_update_button fa fa-pencil-square-o'></i>");
				   out.println("<span id='main-post-tile'>");
				   //getting data from doc of elasticsearch
				   Object topic_title = null,more_desc = null,short_desc = null,file_name = null;
//				   System.out.println(noOfColumn);
				   if(noOfColumn==3){
				   ref.hasNext();
				   Object key= ref.next();	
				   topic_title=result.get(key);
				   
				   ref.hasNext();
				   key= ref.next();
				   more_desc=result.get(key);
				   
				   ref.hasNext();
				   key= ref.next();
				   short_desc=result.get(key);
				   }
				   else
				   {
				   
				   try {
					   
					   Object key= ref.next();
					   
					    key= ref.next();
					    topic_title=result.get(key);

					   key= ref.next();	
					   more_desc=result.get(key);
					   
					   key= ref.next();
					   short_desc=result.get(key);
					   
					   key= ref.next();
					   file_name=result.get(key);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				   }
				   out.println(topic_title);
				   out.println("</span>");
				   out.println("(<span class='less-description gotham-rounded-light'>");
				   out.println(short_desc);
				   out.println("");
				   out.println("</span>)");
				   out.println("</h3>");
				   out.println("</header>");
				   out.println("<p class='more-description gotham-rounded-light'>");
				   out.println(more_desc);
				   out.println("</p>"); 
				   if(file_name!=null){
					 out.println("<br/><a id='download-file' href='DownloadFileServlet?file="+file_name+"&&id_no="+hit.getId()+"'>"+file_name+"</a>");
				   }
				   out.println("</article>");
		    }
		}
		
		//for adding new doc
		else if(taskTitle.equals("add"))
		{
			int reply = 44;
			final Client client = getClient();
			String title = req.getParameter("title");
			String short_desc = req.getParameter("short_desc");
			String long_desc = req.getParameter("long_desc");
			String file_name = req.getParameter("file_name");
			String file_contents = req.getParameter("attach_file");
			//getting the total no of doc in the index and setting id according to that.
			SearchResponse response = client.prepareSearch(INDEX_NAME)
			        .setTypes(DOC_TYPE)
			        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
			        .setQuery(QueryBuilders.matchQuery("topic_title".trim().toLowerCase(), title.trim().toLowerCase()))                 // Query
			        .execute()
			        .actionGet();
		    SearchHit[] searchresponse=response.getHits().hits();
		    try{
			    searchresponse[0].getId().toString();
			    reply = 66;//"66" for already available
			    }
		    catch(ArrayIndexOutOfBoundsException ae)
		    {
		    	client.prepareIndex().setIndex(INDEX_NAME).setType(DOC_TYPE)
		    	.setSource("topic_title",title, "topic_description",short_desc, "topic_more_description", long_desc,"file_title",file_name,"file_content",file_contents).execute().actionGet();
		    }
		    finally{ 
				String s="home?q="+reply;
				resp.sendRedirect(s);
		    }
		}
		//for updating one doc
		else if(taskTitle.equals("update"))
		{
			int reply = 44;
			String title = req.getParameter("title");
			String short_desc = req.getParameter("short_desc");
			String long_desc = req.getParameter("long_desc");
			String file_name = req.getParameter("file_name");
			String file_contents = req.getParameter("attach_file");
			final Client client = getClient();
			//gettting id of the title and updating in the respective id
			SearchResponse response = client.prepareSearch(INDEX_NAME)
			        .setTypes(DOC_TYPE)
			        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
			        .setQuery(QueryBuilders.matchQuery("topic_title".trim().toLowerCase(), title.trim().toLowerCase()))                 // Query
			        .execute()
			        .actionGet();
		    SearchHit[] searchresponse=response.getHits().hits();
		    try{
			    String id_value=searchresponse[0].getId().toString();
			    UpdateRequest updateRequest1;
			    if(file_name.length()>2){
				updateRequest1 = new UpdateRequest(INDEX_NAME, DOC_TYPE, id_value).doc(jsonBuilder()
			        .startObject().field("topic_description", short_desc).field("topic_more_description", long_desc)
			        .field("file_title", file_name).field("file_content", file_contents)
			        .endObject());
				}
			    else{
			    	updateRequest1 = new UpdateRequest(INDEX_NAME, DOC_TYPE, id_value).doc(jsonBuilder()
					        .startObject().field("topic_description", short_desc).field("topic_more_description", long_desc)
					        .endObject());
			    }
				try {
					client.update(updateRequest1).get();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    }
		    catch(ArrayIndexOutOfBoundsException ae)
		    {
		    	reply = 55;//"55" for doc is not available
		    }
		    finally{
				String s="home?q="+reply;
				resp.sendRedirect(s);
		    }

		} //end of update
		//for getting the title for autocomplete
		else if(taskTitle.equals("gettingTheTitleAutocomplete"))
		{
			resp.setContentType("text/html");
			PrintWriter out = resp.getWriter();
			//fetching all the doc and putting in html format
			final Client client = getClient();
			SearchResponse response = client.prepareSearch(INDEX_NAME)
			        .setTypes(DOC_TYPE)
			        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
			        .setQuery(QueryBuilders.wildcardQuery("topic_description", "*"))
			        .setSize(400)
			        .execute()
			        .actionGet(); 
			System.out.println("3333");
		    SearchHits searchHits = response.getHits();
		    SearchHit[] hits = searchHits.getHits();
		    for (int i = 0; i <hits.length; i++) {
			        SearchHit hit = hits[i];
			        Map<String, Object> result = hit.getSource();
			        int noOfColumn=result.size();

			        Object topic_title=null;
			    	Set s=result.keySet();   
			    	Iterator ref=s.iterator();
					   if(noOfColumn==3){
						   Object key= ref.next();	
						   topic_title=result.get(key);
						   
						   key= ref.next();
						   
						   key= ref.next();
						   }
						   else
						   {
						   
						   try {
							   
							   Object key= ref.next();
							   
							    key= ref.next();
							    topic_title=result.get(key);

							   key= ref.next();	
							   
							   key= ref.next();
							   
							   key= ref.next();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						   }		           
				   out.println("<span class='doc-title-value'>"+topic_title+"</span>");
		    }
		}

	}//End of doPost
    public static byte[] loadFileAsBytesArray(String fileName) throws Exception {    	 
        File file = new File(fileName);
        int length = (int) file.length();
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = new byte[length];
        reader.read(bytes, 0, length);
        reader.close();
        return bytes;
    }  
}//End of Class
