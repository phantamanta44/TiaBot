package io.github.phantamanta44.tiabot.util.http;

public class HttpException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private int code;
	
	public HttpException(int code) {
		super(String.format("Encountered http status code: %d", code));
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}

}
