package javawfw.http.response;

import java.util.Map;


public interface IResponse {
	public int getStatus();
	public Map<String, String> getOptions();
	public void addOption(String key,String value);
	public long getContentLength();
	public String getBody();
}
