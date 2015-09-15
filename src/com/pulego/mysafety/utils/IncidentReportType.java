/**
 * 
 */
package com.pulego.mysafety.utils;

import com.pulego.mysafety.R;

/**
 * @author mac
 *
 */
public class IncidentReportType {
	
	private static ReportType[] lList;
	
	public static ReportType[] getCategoryResourceId(int categoryNum) {

		int resId = 0;// = R.array.contactcrime;

		switch (categoryNum) {
		case 1:

			resId = R.array.contactcrime;
			ReportType sArray[] = new ReportType[] { new ReportType("Select Type", "0"), new ReportType("Murder", "1"),
					new ReportType("Sexual Crimes", "2"), new ReportType("Assault", "3"),
					new ReportType("Robbery", "4"), new ReportType("Child Abuse and Neglect", "5") };
			// convert array to list
			lList = sArray;
			break;
		case 2:

			resId = R.array.drugs;
			ReportType sDrugArray[] = new ReportType[] { new ReportType("Select Type", "0"),
					new ReportType("Drug Possession", "6"), new ReportType("Drug Sales", "7"),
					new ReportType("Illegal Gun or Ammunition", "8"), new ReportType("Gun Firing", "9") };
			// convert array to list
			lList = sDrugArray;
			break;
		case 3:

			resId = R.array.corruption;
			ReportType sCorruptionArray[] = new ReportType[] { new ReportType("Select Type", "0"),
					new ReportType("Bribery", "10"), new ReportType("Fraud", "11"),
					new ReportType("Misuse of public property", "12") };
			// convert array to list
			lList = sCorruptionArray;
			break;
		case 4:

			resId = R.array.property;
			ReportType sPropertyArray[] = new ReportType[] { new ReportType("Select Type", "0"),
					new ReportType("Burglary", "13"), new ReportType("Arson", "14"), new ReportType("Vandalism", "15"),
					new ReportType("Malicious Damage", "16"), new ReportType("Theft", "17") };
			// convert array to list
			lList = sPropertyArray;
			break;
		case 5:

			resId = R.array.vehicle;
			ReportType sVehicleArray[] = new ReportType[] { new ReportType("Select Type", "0"),
					new ReportType("Theft", "18"), new ReportType("Hijacking", "19"),
					new ReportType("Breaking", "20") };
			// convert array to list
			lList = sVehicleArray;
			break;
		case 6:

			resId = R.array.protest;
			ReportType sProtestArray[] = new ReportType[] { new ReportType("Select Type", "0"),
					new ReportType("Protest", "21"), new ReportType("Violent Protest", "22") };
			// convert array to list
			lList = sProtestArray;
			break;
		case 7:

			resId = R.array.traffic;
			ReportType sTrafficArray[] = new ReportType[] { new ReportType("Select Type", "0"),
					new ReportType("Accident", "0"), new ReportType("Traffic Lights", "23"),
					new ReportType("Driving under influence", "24") };
			// convert array to list
			lList = sTrafficArray;
			break;
		case 8:

			resId = R.array.bylaw;
			ReportType sBylawArray[] = new ReportType[] { new ReportType("Select Type", "0"),
					new ReportType("Public Amenities", "25"), new ReportType("Public Order and Public Places", "26"),
					new ReportType("Street Trading", "27"), new ReportType("Electricity / Water", "28"),
					new ReportType("Other", "29") };
			// convert array to list
			lList = sBylawArray;
			break;

		case 9:

			resId = R.array.other;
			ReportType sOtherArray[] = new ReportType[] { new ReportType("Select Type", "0"),
					new ReportType("Other", "30") };
			// convert array to list
			lList = sOtherArray;
			break;

		case 10:

			resId = R.array.test;
			ReportType sTestArray[] = new ReportType[] { new ReportType("Select Type", "0"),
					new ReportType("Test", "31") };
			// convert array to list
			lList = sTestArray;
			break;
		}

		return lList;
	}

}
