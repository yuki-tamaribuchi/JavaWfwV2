package javawfw.http.response;

import javawfw.core.http.response.AbstractResponse;
import javawfw.http.Status;

public class StatusResponse extends AbstractResponse {
	public StatusResponse(Status status) {
		super(status, null, null);
	}
}
