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
	static final Integer FROM_YEAR = 1438; 	// MIN = 1318
	static final Integer TO_YEAR = 1460;	// MAX = 1500
	static final boolean FIFTH_MODE = false; // If true, only the 5th day of every solar month will be written to the file.
		
	private static final Map<String, String> WEEKDAYS = new HashMap<>();
	private static final Map<String, String> GREGORIAN_MONTHS = new HashMap<>();
	private static final Map<String, String> HIJRI_SOLAR_MONTHS = new HashMap<>();
	private static final Map<String, String> HIJRI_LUNAR_MONTHS = new HashMap<>();
	private static final Map<String, String> ASTRAL_AR_TO_EN = new HashMap<>();
	private static final Map<String, String> COMMON_AMONG_FARMERS_AR_TO_EN = new HashMap<>();

	static
	{
		WEEKDAYS.put("الاحد", "WEEKDAY_SUNDAY");
		WEEKDAYS.put("الاثنين", "WEEKDAY_MONDAY");
		WEEKDAYS.put("الثلاثاء", "WEEKDAY_TUESDAY");
		WEEKDAYS.put("الاربعاء", "WEEKDAY_WEDNESDAY");
		WEEKDAYS.put("الخميس", "WEEKDAY_THURSDAY");
		WEEKDAYS.put("الجمعة", "WEEKDAY_FRIDAY");
		WEEKDAYS.put("السبت", "WEEKDAY_SATURDAY");
		
		GREGORIAN_MONTHS.put("يناير", "MONTH_GREGORIAN_JANUARY");
		GREGORIAN_MONTHS.put("فبراير", "MONTH_GREGORIAN_FEBRUARY");
		GREGORIAN_MONTHS.put("مارس", "MONTH_GREGORIAN_MARCH");
		GREGORIAN_MONTHS.put("إبريل", "MONTH_GREGORIAN_APRIL");
		GREGORIAN_MONTHS.put("مايو", "MONTH_GREGORIAN_MAY");
		GREGORIAN_MONTHS.put("يونيو", "MONTH_GREGORIAN_JUNE");
		GREGORIAN_MONTHS.put("يوليو", "MONTH_GREGORIAN_JULY");
		GREGORIAN_MONTHS.put("أغسطس", "MONTH_GREGORIAN_AUGUST");
		GREGORIAN_MONTHS.put("سبتمبر", "MONTH_GREGORIAN_SEPTEMBER");
		GREGORIAN_MONTHS.put("أكتوبر", "MONTH_GREGORIAN_OCTOBER");
		GREGORIAN_MONTHS.put("نوفمبر", "MONTH_GREGORIAN_NOVEMBER");
		GREGORIAN_MONTHS.put("ديسمبر", "MONTH_GREGORIAN_DECEMBER");
		
		HIJRI_SOLAR_MONTHS.put("الميزان", "MONTH_HIJRI_SOLAR_LIBRA");
		HIJRI_SOLAR_MONTHS.put("العقرب", "MONTH_HIJRI_SOLAR_SCORPIO");
		HIJRI_SOLAR_MONTHS.put("القوس", "MONTH_HIJRI_SOLAR_SCORPIO");
		HIJRI_SOLAR_MONTHS.put("الجدي", "MONTH_HIJRI_SOLAR_CAPRICORN");
		HIJRI_SOLAR_MONTHS.put("الدلو", "MONTH_HIJRI_SOLAR_AQUARIUS");
		HIJRI_SOLAR_MONTHS.put("الحوت", "MONTH_HIJRI_SOLAR_PISCES");
		HIJRI_SOLAR_MONTHS.put("الحمل", "MONTH_HIJRI_SOLAR_ARIES");
		HIJRI_SOLAR_MONTHS.put("الثور", "MONTH_HIJRI_SOLAR_TAURUS");
		HIJRI_SOLAR_MONTHS.put("الجوزاء", "MONTH_HIJRI_SOLAR_GEMINI");
		HIJRI_SOLAR_MONTHS.put("السرطان", "MONTH_HIJRI_SOLAR_CANCER");
		HIJRI_SOLAR_MONTHS.put("الأسد", "MONTH_HIJRI_SOLAR_LEO");
		HIJRI_SOLAR_MONTHS.put("السنبلة", "MONTH_HIJRI_SOLAR_VIRGO");
		
//		ASTRAL_AR_TO_EN.put("الاكليل", "Saturday");
//		ASTRAL_AR_TO_EN.put("القلب", "Saturday");
//		ASTRAL_AR_TO_EN.put("الشولة", "Saturday");
//		ASTRAL_AR_TO_EN.put("النعايم", "Saturday");
//		ASTRAL_AR_TO_EN.put("البلدة", "Saturday");
//		ASTRAL_AR_TO_EN.put("سعد الذابح", "Saturday"); // it's "البلدة" in Ummualqura
//		ASTRAL_AR_TO_EN.put("سعد بلع", "Saturday");
//		ASTRAL_AR_TO_EN.put("سعد السعود", "Saturday");
//		ASTRAL_AR_TO_EN.put("سعد الاخبية", "Saturday");
//		ASTRAL_AR_TO_EN.put("المقدم", "Saturday");
//		ASTRAL_AR_TO_EN.put("المؤخر", "Saturday");
//		ASTRAL_AR_TO_EN.put("الرشا", "Saturday");
//		ASTRAL_AR_TO_EN.put("الشرطين", "Saturday");
//		ASTRAL_AR_TO_EN.put("البطين", "Saturday");
//		ASTRAL_AR_TO_EN.put("الثريا", "Saturday");
//		ASTRAL_AR_TO_EN.put("الدبران", "Saturday");
//		ASTRAL_AR_TO_EN.put("الهقعة", "Saturday");
//		ASTRAL_AR_TO_EN.put("الهنعة", "Sunday");
//		ASTRAL_AR_TO_EN.put("الذراع", "Monday");
//		ASTRAL_AR_TO_EN.put("النثرة", "Tuesday");
//		ASTRAL_AR_TO_EN.put("الطرف", "Saturday");
//		ASTRAL_AR_TO_EN.put("الجبهة", "Saturday");
//		ASTRAL_AR_TO_EN.put("الزبرة", "Wednesday");
//		ASTRAL_AR_TO_EN.put("الصرفة", "Thursday");
//		ASTRAL_AR_TO_EN.put("العوا", "Friday");
//		ASTRAL_AR_TO_EN.put("السماك", "Saturday");
//		ASTRAL_AR_TO_EN.put("الغفر", "Saturday");
//		ASTRAL_AR_TO_EN.put("الزبانا", "Saturday");
//
//		COMMON_AMONG_FARMERS_AR_TO_EN.put("المربعانية", "Friday");
//		COMMON_AMONG_FARMERS_AR_TO_EN.put("الشبط", "Saturday");
//		COMMON_AMONG_FARMERS_AR_TO_EN.put("العقارب", "Saturday");
//		COMMON_AMONG_FARMERS_AR_TO_EN.put("الحميمين", "Saturday");
//		COMMON_AMONG_FARMERS_AR_TO_EN.put("الذراعيين", "Saturday");
//		COMMON_AMONG_FARMERS_AR_TO_EN.put("الثريا", "Saturday");
//		COMMON_AMONG_FARMERS_AR_TO_EN.put("التوبيع", "Saturday");
//		COMMON_AMONG_FARMERS_AR_TO_EN.put("الجوزاء 1", "Saturday");
//		COMMON_AMONG_FARMERS_AR_TO_EN.put("الجوزاء 2", "Saturday");
//		COMMON_AMONG_FARMERS_AR_TO_EN.put("المرزم", "Monday");
//		COMMON_AMONG_FARMERS_AR_TO_EN.put("الكليبين", "Tuesday");
//		COMMON_AMONG_FARMERS_AR_TO_EN.put("سهيل", "Wednesday");
//		COMMON_AMONG_FARMERS_AR_TO_EN.put("الوسم", "Thursday");
//		COMMON_AMONG_FARMERS_AR_TO_EN.put("الشرطين", "Saturday");
		
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
						String previousGregorianMonth = "";
						int previousGregorianYear = -1;
						String previousSolarHijriMonth = "";
						int previousSolarHijriYear = -1;
						
						for(int hijriDay = 1; hijriDay < rows.size(); hijriDay++) // skip the header
						{
							Elements cells = rows.get(hijriDay).select("td");
							
							String weekday = cells.get(1).text();
							String gregorianDate = cells.get(2).text();
							String solarHijriDate = cells.get(3).text();
							
							String weekdayEN = WEEKDAYS.get(weekday);
							
							String[] gregorianDateParts = gregorianDate.split("\\s+");
							int gregorianDay = Integer.parseInt(gregorianDateParts[0]);
							String gregorianMonth = gregorianDateParts.length > 1 ? previousGregorianMonth = GREGORIAN_MONTHS.get(gregorianDateParts[1])
																			   	  : previousGregorianMonth;
							int gregorianYear = gregorianDateParts.length > 1 ? previousGregorianYear = Integer.parseInt(gregorianDateParts[2])
									   										  : previousGregorianYear;
							
							String[] solarHijriDateParts = solarHijriDate.split("\\s+");
							int solarHijriDay = Integer.parseInt(solarHijriDateParts[0]);
							String solarHijriMonth = solarHijriDateParts.length > 1 ? previousSolarHijriMonth = HIJRI_SOLAR_MONTHS.get(solarHijriDateParts[1])
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