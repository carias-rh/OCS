package eu.europa.ec.eci.oct.export.dto;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class ExportApiResponse implements Serializable {

	private static final long serialVersionUID = 900166116862938267L;

	public static String ERROR = "ERROR";
	public static String SUCCESS = "SUCCESS";

	private String status;
	private int code;
	private String message;

	public ExportApiResponse() {
	}

	public ExportApiResponse(String status, int code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

	public static ExportApiResponse buildSuccess(int code, String message) {
		return new ExportApiResponse(SUCCESS, code, message);
	}

	public static ExportApiResponse buildError(int code, String message) {
		return new ExportApiResponse(ERROR, code, message);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ApiResponse [status=" + status + ", code=" + code + ", message=" + message + "]";
	}

}
