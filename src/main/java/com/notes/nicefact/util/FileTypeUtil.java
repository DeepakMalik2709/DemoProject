package com.notes.nicefact.util;

public class FileTypeUtil {

	public static boolean isImage(String extension) {
		return extension.equals("bmp")
				|| extension.equals("cur")
				|| extension.equals("ico")
				|| extension.equals("gif")
				|| extension.equals("jpg")
				|| extension.equals("jpeg")
				|| extension.equals("png")
				|| extension.equals("psd")
				|| extension.equals("raw")
				|| extension.equals("tif");
	}
	
	public static boolean isPdf(String extension) {
		return extension.equals("pdf");
	}
	
	public static boolean isPpt(String extension) {
		return extension.equals("pps")
			|| extension.equals("ppt")
			|| extension.equals("pptx");
	}
	
	public static boolean isDoc(String extension) {
		return extension.equals("doc")
				|| extension.equals("docx");
	}
	
	public static boolean isExcel(String extension) {
		return extension.equals("xls")
				|| extension.equals("xlsx")
				|| extension.equals("xlr");
	}
}
