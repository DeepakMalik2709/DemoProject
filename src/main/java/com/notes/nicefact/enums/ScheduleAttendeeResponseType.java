package com.notes.nicefact.enums;

public enum ScheduleAttendeeResponseType {
	
	ACCEPTED("ACCEPTED"),TENTATIVE("TENTATIVE"), DECLINED("DECLINED"), NEEDSACTION("NEEDSACTION");
	
	String typeLabel;
	ScheduleAttendeeResponseType(String typeLabel){
		this.typeLabel=typeLabel;
	}
	
	/*public ScheduleAttendeeResponseType getValue(String value){
		switch(value){
		case "yes": return ACCEPTED;			
		case "no": return DECLINED;		
		case "maybe": return TENTATIVE;
		case "ideal": return NEEDSACTION;
		}
		return NEEDSACTION;
	}
*/
}
