/**
 * 
 */
package com.notes.nicefact.comparator;

import java.util.Comparator;

import com.notes.nicefact.to.PostTO;

public class CreatedDateComparator  implements Comparator<PostTO>{


	@Override
	public int compare(PostTO o1, PostTO o2) {
		int result = 0;
		
		if(o1==null || o2 ==null)
			return result;
		if(o1.getCreatedTime() == o2.getCreatedTime())
			return 0;
		
		result = o1.getCreatedTime()> o2.getCreatedTime()?-1:1	;
		
		return result;
	}
	
}
