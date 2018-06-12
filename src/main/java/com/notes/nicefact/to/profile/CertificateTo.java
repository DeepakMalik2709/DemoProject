package com.notes.nicefact.to.profile;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.Certificate;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CertificateTo implements Serializable {

	private static final long serialVersionUID = 8525546362044092735L;

	private Long certificateId;
	private String name;
	private String date;
	private String image;
	private String organisation;
	private String grade;
	private Long appUserId;
	public Long getCertificateId() {
		return certificateId;
	}
	public void setCertificateId(Long certificateId) {
		this.certificateId = certificateId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getOrganisation() {
		return organisation;
	}
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public Long getAppUserId() {
		return appUserId;
	}
	public void setAppUserId(Long appUserId) {
		this.appUserId = appUserId;
	}
	
	public static CertificateTo convert(Certificate certificate) {
		CertificateTo certificateTo = new CertificateTo();
		certificateTo.setAppUserId(certificate.getAppUser().getId());
		certificateTo.setDate(certificate.getDate());
		certificateTo.setGrade(certificate.getGrade());
		certificateTo.setCertificateId(certificate.getId());
		certificateTo.setImage(certificate.getImage());
		certificateTo.setName(certificate.getName());
		certificateTo.setOrganisation(certificate.getOrganisation());
		
		return certificateTo;
	}
}