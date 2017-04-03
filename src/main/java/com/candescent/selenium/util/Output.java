package com.candescent.selenium.util;

import java.io.PrintWriter;

import com.candescent.selenium.Constants;

public class Output {

	private PrintWriter writer;
	
	private static Output output;
	
	private Output() {
		try {
			writer = new PrintWriter(Constants.OUTPUT_LOCATION, "UTF-8");
		} catch (Exception e) {
			System.out.println("Cannot Initialize Output.  Output will not be stored.");
		}
	}
	
	public void write(String[] items) {
		StringBuilder sb = new StringBuilder();
		Boolean isFirst = Boolean.TRUE;
		for(String item : items) {
			if(!isFirst) {
				sb.append(",");
			}
			sb.append(item);
			isFirst = Boolean.FALSE;
		}
		System.out.println(sb.toString());
		writer.println(sb.toString());
	}

	public void close() {
		writer.close();
	}
	
	public static Output getInstance() {
		if(output == null) { 
			output = new Output();
		}
		return output;
	}
	
}
