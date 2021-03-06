package amazon;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Item {
	private int rank;
	private String name;
	private String asn;
	private double price;
	private double rating;
	private int ratingNum;
	
	public Item(int index, Element query) {
		rank = index;
		asn = GetAsn(query);
		name = GetName(query);
		price = GetPrice(query);
		rating = GetRating(query);
		ratingNum = GetRatingNum(query);
	};
	
	public String ToCsv() {
		//rank,name,asn,price,rating,ratingNum
		//-1 or -1.0 means rating N/A
		StringBuilder sb = new StringBuilder();
		sb.append(this.rank);
		sb.append(",");
		sb.append(this.name);
		sb.append(",");
		sb.append(this.asn);
		sb.append(",");
		sb.append(this.price);
		sb.append(",");
		sb.append(this.rating);
		sb.append(",");
		sb.append(this.ratingNum);
		
		return sb.toString();
	}

	
	private int GetRatingNum(Element query) {
		//TODO error check, return default value
		Elements result = query.getElementsByClass("a-size-small a-link-normal a-text-normal");
	
	
		for (Element e : result) {
			String att = e.text();
		
			NumberFormat format = NumberFormat.getInstance(Locale.US);
			Number number = null;
			try {
				if (att.contains("$")) {
					continue;
				}

				number = format.parse(att);
				
			} catch (ParseException ex) {
				// TODO Auto-generated catch block
				continue;
			}
			
			int ratingNum = number.intValue();
			System.out.println(ratingNum);
			return ratingNum;
		}
		
		//Item has no ratings
		return -1;
	}


	private double GetRating(Element query) {
		//TODO error check, return default value
		Elements result = query.getElementsByClass("a-icon-alt");
		if (result.first() == null) {
			//item has no rating
			return -1.0;
		}
		
		String att = result.first().text();
		
		att = att.split("\\s+")[0];
		
		if (att.equalsIgnoreCase("prime")) {
			//prime rating
			if (result.size() == 1) {
				//item has no rating
				return -1.0;
			}
			att = result.get(1).text();
			att = att.split("\\s+")[0];
		}

        double rating = Double.parseDouble(att);
		
		System.out.println(rating);
		return rating;
	}


	private double GetPrice(Element query) {
		//TODO error check, return default value
		Elements t = null;

		//prices are displayed in different ways, some times its on sale, sometimes its prime pricing
		t = query.getElementsByClass("a-size-base a-color-base");
		if (t.first() == null) {
			t = query.getElementsByClass("a-offscreen");
			if(t.first() == null) {
				t = query.getElementsByClass("a-size-base-plus a-color-secondary a-text-strike");
			}
		}
	
		if (t.first() == null) {
			//Item unavailable
			return -1.0;
		}
		
		String att = t.first().text();
		att = att.substring(1);
		
		NumberFormat format = NumberFormat.getInstance(Locale.US);
		Number number = null;
		try {
			number = format.parse(att);
		} catch (ParseException e) {
			// Item unavailable
			return -1.0;
		}
		
		double price = number.doubleValue();
		System.out.println(price);
		return price;
	}


	private String GetName(Element query) {
		//TODO error check, return default value
		Elements result = query.getElementsByClass("a-link-normal s-access-detail-page  s-color-twister-title-link a-text-normal");
		String att = result.first().attr("title");
	
		System.out.println(att);
		return att;
	}

	private String GetAsn(Element query) {
		//TODO error check, return a default value
		String att = query.attr("data-asin");
	
		System.out.println(att);
		return att;
	};

}


