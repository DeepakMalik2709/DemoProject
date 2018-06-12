package com.notes.nicefact.enums;

public enum AttendanceStatus {
	PRESENT("P"), ABSENT("A"), LEAVE("L") ;
	
	String shortLabel;
	AttendanceStatus(String shortLabel){
		this.shortLabel=shortLabel;
	}
	
	public String getShortLable(){
		return this.shortLabel;
	}
}
