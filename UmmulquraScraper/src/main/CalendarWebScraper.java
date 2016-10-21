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

public class CalendarWebScraper {
	// Settings
	static final Integer FROM_YEAR = 1438; // MIN = 1318
	static final Integer TO_YEAR = 1450; // MAX = 1500
	static final boolean FIFTH_MODE = false; // If true, only the 5th day of
												// every solar month will be
												// written to the file.

	private static final Map<String, String> WEEKDAYS = new HashMap<>();
	private static final Map<String, Integer> GREGORIAN_MONTHS = new HashMap<>();
	private static final Map<String, Integer> GREGORIAN_MONTHS_AR_TO_INDEX = new HashMap<>();
	private static final Map<String, String> HIJRI_SOLAR_MONTHS = new HashMap<>();
	private static final Map<String, String> HIJRI_LUNAR_MONTHS = new HashMap<>();
	private static final Map<String, String> ASTRAL_AR_TO_EN = new HashMap<>();
	private static final Map<String, String> COMMON_AMONG_FARMERS_AR_TO_EN = new HashMap<>();

	static {
		WEEKDAYS.put("الاحد", "WEEKDAY_SUNDAY");
		WEEKDAYS.put("الاثنين", "WEEKDAY_MONDAY");
		WEEKDAYS.put("الثلاثاء", "WEEKDAY_TUESDAY");
		WEEKDAYS.put("الاربعاء", "WEEKDAY_WEDNESDAY");
		WEEKDAYS.put("الخميس", "WEEKDAY_THURSDAY");
		WEEKDAYS.put("الجمعة", "WEEKDAY_FRIDAY");
		WEEKDAYS.put("السبت", "WEEKDAY_SATURDAY");

		GREGORIAN_MONTHS.put("يناير", 1);
		GREGORIAN_MONTHS.put("فبراير", 2);
		GREGORIAN_MONTHS.put("مارس", 3);
		GREGORIAN_MONTHS.put("إبريل", 4);
		GREGORIAN_MONTHS.put("مايو", 5);
		GREGORIAN_MONTHS.put("يونيو", 6);
		GREGORIAN_MONTHS.put("يوليو", 7);
		GREGORIAN_MONTHS.put("أغسطس", 8);
		GREGORIAN_MONTHS.put("سبتمبر", 9);
		GREGORIAN_MONTHS.put("أكتوبر", 10);
		GREGORIAN_MONTHS.put("نوفمبر", 11);
		GREGORIAN_MONTHS.put("ديسمبر", 12);

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

	}

	public static void main(String[] args) throws Exception {
		String fileTitle = String.format("output/UmmulquraScraper_%s-%s", FROM_YEAR.toString(), TO_YEAR.toString());
		fileTitle = FIFTH_MODE ? fileTitle + "_limited.json" : fileTitle + "_full.json";
		PrintWriter pw = new PrintWriter(new FileWriter(new File(fileTitle)));
		pw.println("{");
		String link = "http://www.ummulqura.org.sa/datespage.aspx?d1=true&d2=true&d3=true&d4=true&d5=true&month=%d&year=%d&h=true";

		for (int hijriYear = FROM_YEAR; hijriYear <= TO_YEAR; hijriYear++) {
			pw.println(" \"" + hijriYear + "\" : [");

			for (int hijriMonth = 1; hijriMonth <= 12; hijriMonth++) {
				System.out.println("hijriYear = " + hijriYear + " | hijriMonth = " + hijriMonth);
				Document document = Jsoup.connect(String.format(link, hijriMonth, hijriYear)).timeout(0).get();
				Elements elements = document.select("table[class=payerTB]");

				if (elements.size() > 0) {
					Element table = elements.get(0);
					Elements rows = table.select("tr");

					if (rows.size() > 1) {
						int previousGregorianMonth = -1;
						int previousGregorianYear = -1;
						String previousSolarHijriMonth = "";
						int previousSolarHijriYear = -1;
						pw.println("  [");

						for (int hijriDay = 1; hijriDay < rows.size(); hijriDay++) 
						{
							Elements cells = rows.get(hijriDay).select("td");
							String weekday = cells.get(1).text();
							String gregorianDate = cells.get(2).text();
							String solarHijriDate = cells.get(3).text();
							String weekdayEN = WEEKDAYS.get(weekday);
							String[] gregorianDateParts = gregorianDate.split("\\s+");
							int gregorianDay = Integer.parseInt(gregorianDateParts[0]);
							int gregorianMonth = gregorianDateParts.length > 1
									? previousGregorianMonth = GREGORIAN_MONTHS_AR_TO_INDEX.get(gregorianDateParts[1])
									: previousGregorianMonth;
							int gregorianYear = gregorianDateParts.length > 1
									? previousGregorianYear = Integer.parseInt(gregorianDateParts[2])
									: previousGregorianYear;
							String[] solarHijriDateParts = solarHijriDate.split("\\s+");
							int solarHijriDay = Integer.parseInt(solarHijriDateParts[0]);
							String solarHijriMonth = solarHijriDateParts.length > 1
									? previousSolarHijriMonth = HIJRI_SOLAR_MONTHS.get(solarHijriDateParts[1])
									: previousSolarHijriMonth;
							int solarHijriYear = solarHijriDateParts.length > 1
									? previousSolarHijriYear = Integer.parseInt(solarHijriDateParts[2])
									: previousSolarHijriYear;

							if (hijriDay + 1 == rows.size()) {
								pw.println("   {\"gregoran_day\" : " + gregorianDay + ", \"gregoran_month\" : "
										+ gregorianMonth + ", \"gregoran_year\" : " + gregorianYear
										+ ", \"hijri_solar_day\" : " + solarHijriDay + ", \"hijri_solar_month\" : \""
										+ solarHijriMonth + "\", \"hijri_solar_year\" : " + solarHijriYear + "}");
							} else {
								pw.println("   {\"gregoran_day\" : " + gregorianDay + ", \"gregoran_month\" : "
										+ gregorianMonth + ", \"gregoran_year\" : " + gregorianYear
										+ ", \"hijri_solar_day\" : " + solarHijriDay + ", \"hijri_solar_month\" : \""
										+ solarHijriMonth + "\", \"hijri_solar_year\" : " + solarHijriYear + "},");
							}

						}
					}
				} else {
					System.out.println("No Table found!");
				}

				if (hijriMonth == 12) {
					pw.println("  ]");
				} else {
					pw.println("  ],");
				}

			}

			if (hijriYear == TO_YEAR) {
				pw.println(" ]");
			} else {
				pw.println(" ],");
			}

		}
		pw.print("}");
		pw.close();
	}
}