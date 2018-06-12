package com.notes.nicefact.enums;


public enum WeekDay {
	SUNDAY(1), MONDAY(2), TUESDAY(3), WEDNESDAY(4), THURSDAY(5), FRIDAY(6), SATURDAY(7);

	private int number;

	private WeekDay(int number) {
		this.number =number;
	}

	public static WeekDay getByNumber(int number){
		switch(number){
		case 1:
			return SUNDAY;
		case 2:
			return MONDAY;
		case 3:
			return TUESDAY;
		case 4:
			return WEDNESDAY;
		case 5:
			return THURSDAY;
		case 6:
			return FRIDAY;
		case 7:
			return SATURDAY;
		default:
			return null;
		}
	}

}
