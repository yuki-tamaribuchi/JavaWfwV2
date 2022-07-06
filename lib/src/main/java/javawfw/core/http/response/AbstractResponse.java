package javawfw.core.http.response;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import javawfw.http.response.IResponse;
import javawfw.http.Status;
import javawfw.core.http.StatusInteger;

public class AbstractResponse implements IResponse {
	private final Integer status;
	private final Map<String, String> options;
	private String body;

	public AbstractResponse(Status status, Map<String, String> options, String body){
		this.status = StatusInteger.getStatusInteger(status);
		if (options==null) {
			this.options = new LinkedHashMap<>();
		} else {
			this.options = options;
		}

		if (body==null) {
			this.body = "";
		} else {
			this.body = body;
		}
	}
		

	public final int getStatus() {
		return (int)status;
	}

	public final long getContentLength() {
		return body.getBytes(StandardCharsets.UTF_8).length;
	}

	public final Map<String, String> getOptions() {
		return options;
	}

	public final void addOption(String key, String value) {
		options.put(key, value);
	}

	public final String getBody() {
		return body;
	}

	public String toString() {
		return "Status: " + String.valueOf(status) + ", Body: " + body;
	}

	protected final void setBody(String body) {
		this.body = body;
	}
}
