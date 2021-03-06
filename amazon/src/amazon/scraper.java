package amazon;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class scraper {
	
	final static String BASE = "https://www.amazon.com/s/ref=lp_172659_st?rh=n%3A172282%2Cn%3A%21493964%2Cn%3A1266092011%2Cn%3A172659&qid=1520461163&sort=review-rank";
	final static String prefix = "https://www.amazon.com/s/ref=sr_pg_2?rh=n%3A172282%2Cn%3A%21493964%2Cn%3A1266092011%2Cn%3A172659&page=";
	final static String postfix = "&sort=review-rank&ie=UTF8&qid=1520474376";
	private static List<Item> resultList = new ArrayList<>();
	
	
	//set number of items to parse
	private static int numItems = 1000;
	
	public static void main(String[] args) {
		
		int index = 0;
		scrape(index ,BASE);
		
		index = 24;
		for (int i = 2; i <= (numItems/24) + 1; i++) {
			scrape(index ,prefix + i + postfix);
			index = index + 24;
		}	
		
		printer(resultList);
		resultList.clear();
	}
	
	private static void scrape(int index, String url) {
		Connection.Response response = null;
		
		try {
			response = Jsoup.connect(url).followRedirects(true).execute();
		} catch (IOException e) {
			//connection failed
			e.printStackTrace();
		}
		

		if (response != null && response.statusCode() == 200) {
			Document doc = null;
			
			try {
				doc = response.parse();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			Elements result = null;
			for (int i = index; i < index + 24; i++) {
				result = doc.select("#result_" + i);
				
				if (i > numItems) break;
				resultList.add(new Item(i, result.first()));
			}
		}
	}
	
	private static void printer(List<Item> itemList) {
		LocalDateTime time = LocalDateTime.now();
		time = time.withNano(0);		
		
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(time + ".csv"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringBuilder sb = new StringBuilder();
		for(Item item : itemList) {
			sb.append(item.ToCsv());
			sb.append('\n');
		}
		
		pw.write(sb.toString());
		pw.close();
	}

}
