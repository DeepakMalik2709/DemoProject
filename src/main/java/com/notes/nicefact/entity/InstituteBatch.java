package com.notes.nicefact.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 *  This will be the top most group an organisation can have, or we can call it home page of XYZ institute.
 *  
 * @author jkb
 *
 */

@Entity
public class InstituteBatch extends CommonEntity {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Institute institute;
	
	String name;

}
