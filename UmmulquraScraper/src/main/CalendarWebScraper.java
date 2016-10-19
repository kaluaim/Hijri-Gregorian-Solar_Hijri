package main;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CalendarWebScraper
{
	// Settings
	static final Integer FROM_YEAR = 1438;
	static final Integer TO_YEAR = 1460;
	static final boolean FIFTH_MODE = true; // If true, only the 5th day of every solar month will be written to the file.
		
	private static final Map<String, String> WEEKDAYS_AR_TO_EN = new HashMap<>();
	private static final Map<String, Integer> GREGORIAN_MONTHS_AR_TO_INDEX = new HashMap<>();
	private static final Map<String, Integer> SOLAR_HIJRI_MONTHS_AR_TO_INDEX = new HashMap<>();

	static
	{
		WEEKDAYS_AR_TO_EN.put("الاحد", "Sunday");
		WEEKDAYS_AR_TO_EN.put("الاثنين", "Monday");
		WEEKDAYS_AR_TO_EN.put("الثلاثاء", "Tuesday");
		WEEKDAYS_AR_TO_EN.put("الاربعاء", "Wednesday");
		WEEKDAYS_AR_TO_EN.put("الخميس", "Thursday");
		WEEKDAYS_AR_TO_EN.put("الجمعة", "Friday");
		WEEKDAYS_AR_TO_EN.put("السبت", "Saturday");
		
		GREGORIAN_MONTHS_AR_TO_INDEX.put("يناير", 1);
		GREGORIAN_MONTHS_AR_TO_INDEX.put("فبراير", 2);
		GREGORIAN_MONTHS_AR_TO_INDEX.put("مارس", 3);
		GREGORIAN_MONTHS_AR_TO_INDEX.put("إبريل", 4);
		GREGORIAN_MONTHS_AR_TO_INDEX.put("مايو", 5);
		GREGORIAN_MONTHS_AR_TO_INDEX.put("يونيو", 6);
		GREGORIAN_MONTHS_AR_TO_INDEX.put("يوليو", 7);
		GREGORIAN_MONTHS_AR_TO_INDEX.put("أغسطس", 8);
		GREGORIAN_MONTHS_AR_TO_INDEX.put("سبتمبر", 9);
		GREGORIAN_MONTHS_AR_TO_INDEX.put("أكتوبر", 10);
		GREGORIAN_MONTHS_AR_TO_INDEX.put("نوفمبر", 11);
		GREGORIAN_MONTHS_AR_TO_INDEX.put("ديسمبر", 12);
		
		SOLAR_HIJRI_MONTHS_AR_TO_INDEX.put("الحمل", 1);
		SOLAR_HIJRI_MONTHS_AR_TO_INDEX.put("الثور", 2);
		SOLAR_HIJRI_MONTHS_AR_TO_INDEX.put("الجوزاء", 3);
		SOLAR_HIJRI_MONTHS_AR_TO_INDEX.put("السرطان", 4);
		SOLAR_HIJRI_MONTHS_AR_TO_INDEX.put("الأسد", 5);
		SOLAR_HIJRI_MONTHS_AR_TO_INDEX.put("السنبلة", 6);
		SOLAR_HIJRI_MONTHS_AR_TO_INDEX.put("الميزان", 7);
		SOLAR_HIJRI_MONTHS_AR_TO_INDEX.put("العقرب", 8);
		SOLAR_HIJRI_MONTHS_AR_TO_INDEX.put("القوس", 9);
		SOLAR_HIJRI_MONTHS_AR_TO_INDEX.put("الجدي", 10);
		SOLAR_HIJRI_MONTHS_AR_TO_INDEX.put("الدلو", 11);
		SOLAR_HIJRI_MONTHS_AR_TO_INDEX.put("الحوت", 12);
	}
	
	public static void main(String[] args) throws Exception
	{
		String fileTitle = String.format("output/UmmulquraScraper_%s-%s", FROM_YEAR.toString(), TO_YEAR.toString());
		fileTitle = FIFTH_MODE ? fileTitle + "_limited.csv" : fileTitle + "_full.csv";
		
		PrintWriter pw = new PrintWriter(new FileWriter(new File(fileTitle)));
		pw.println("Week Day,Hijri Day,Hijri Month,Hijri Year,Gregorian Day,Gregorian Month,Gregorian Year,Solar Hijri Day,Solar Hijri Month,Solar Hijri Year");
		String link = "http://www.ummulqura.org.sa/datespage.aspx?d1=true&d2=true&d3=true&d4=true&d5=true&month=%d&year=%d&h=true";
		
		for(int hijriYear = FROM_YEAR; hijriYear <= TO_YEAR; hijriYear++)
		{
			for(int hijriMonth = 1; hijriMonth <= 12; hijriMonth++)
			{
				System.out.println("hijriYear = " + hijriYear + " | hijriMonth = " + hijriMonth);
				
				Document document = Jsoup.connect(String.format(link, hijriMonth, hijriYear)).timeout(0).get();
				Elements elements = document.select("table[class=payerTB]");
				
				if(elements.size() > 0)
				{
					Element table = elements.get(0);
					Elements rows = table.select("tr");
					
					if(rows.size() > 1)
					{
						int previousGregorianMonth = -1;
						int previousGregorianYear = -1;
						int previousSolarHijriMonth = -1;
						int previousSolarHijriYear = -1;
						
						for(int hijriDay = 1; hijriDay < rows.size(); hijriDay++) // skip the header
						{
							Elements cells = rows.get(hijriDay).select("td");
							
							String weekday = cells.get(1).text();
							String gregorianDate = cells.get(2).text();
							String solarHijriDate = cells.get(3).text();
							
							String weekdayEN = WEEKDAYS_AR_TO_EN.get(weekday);
							
							String[] gregorianDateParts = gregorianDate.split("\\s+");
							int gregorianDay = Integer.parseInt(gregorianDateParts[0]);
							int gregorianMonth = gregorianDateParts.length > 1 ? previousGregorianMonth = GREGORIAN_MONTHS_AR_TO_INDEX.get(gregorianDateParts[1])
																			   : previousGregorianMonth;
							int gregorianYear = gregorianDateParts.length > 1 ? previousGregorianYear = Integer.parseInt(gregorianDateParts[2])
									   										  : previousGregorianYear;
							
							String[] solarHijriDateParts = solarHijriDate.split("\\s+");
							int solarHijriDay = Integer.parseInt(solarHijriDateParts[0]);
							int solarHijriMonth = solarHijriDateParts.length > 1 ? previousSolarHijriMonth = SOLAR_HIJRI_MONTHS_AR_TO_INDEX.get(solarHijriDateParts[1])
																				 : previousSolarHijriMonth;
							int solarHijriYear = solarHijriDateParts.length > 1 ? previousSolarHijriYear = Integer.parseInt(solarHijriDateParts[2])
									   											: previousSolarHijriYear;
							
							if (FIFTH_MODE) {
								
								if (solarHijriDay == 5) {
									pw.println(weekdayEN + "," + hijriDay + "," + hijriMonth + "," + hijriYear + "," + gregorianDay + "," + gregorianMonth + "," +
											   gregorianYear + "," + solarHijriDay + "," + solarHijriMonth + "," + solarHijriYear);
								}
								
							} else {
								pw.println(weekdayEN + "," + hijriDay + "," + hijriMonth + "," + hijriYear + "," + gregorianDay + "," + gregorianMonth + "," +
										   gregorianYear + "," + solarHijriDay + "," + solarHijriMonth + "," + solarHijriYear);
							}
							
						}
					}
				}
				else
				{
					System.out.println("No Table found!");
				}
			}
		}
		
		pw.close();
	}
}