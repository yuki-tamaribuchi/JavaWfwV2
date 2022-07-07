package javawfw.core.paths;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Iterator;

import javawfw.http.HttpMethod;


public class Path {
	private String path;
	private Method method;
	private HttpMethod httpMethod;
	private Pattern regexPattern;
	
	public Path(String path, Method method, HttpMethod httpMethod) {
		this.path = path;
		this.method = method;
		this.httpMethod = httpMethod;
		regexPattern = getRegexPattern(path);
	}

	private Pattern getRegexPattern(String path) {
		String regexPath = path.replaceAll("\\{(.*?)\\}", "[0-9a-zA-Z]+?");
		if (regexPath.endsWith("/")) {
			regexPath = regexPath.substring(0, regexPath.length()-1);
		}
		Pattern regexPattern = Pattern.compile(regexPath);
		return regexPattern;
	}

	public boolean match(String url, HttpMethod httpMethod) {
		Matcher matcher = regexPattern.matcher(url);
		boolean isUrlMatched = matcher.matches();
		boolean isHttpMethodMatched = this.httpMethod == httpMethod;
		boolean isMatched = isUrlMatched && isHttpMethodMatched;
		return isMatched;
	}

	public Method getMethod() {
		return method;
	}


	public Map<String, String> getPathVariableMap(String url) {
		Map<String, String> pathVariableMap = new LinkedHashMap<>();
		String[] splittedUrl = url.split("/");
		String[] splittedPath = path.split("/");
		if (splittedUrl.length == splittedPath.length) {
			for (int i=0; i<splittedPath.length; i++) {
				if(splittedPath[i].startsWith("{") && splittedPath[i].endsWith("}")) {
					String pathVariableName = splittedPath[i].substring(1, splittedPath[i].length()-1);
					String pathVariableValue = splittedUrl[i];
					pathVariableMap.put(pathVariableName, pathVariableValue);
				}
			}
			return pathVariableMap;
		}
		return null;
	}

	public Map<String, String> getRequestParamMap(String url) {
		Map<String, String> requestParamMap = new LinkedHashMap<>();
		String[] splittedUrl = url.split("\\?");
		if (splittedUrl.length==2) {
			String requestParamString = splittedUrl[1];
			Iterator<String> requestParamStringIterator = Arrays.asList(requestParamString.split("&")).iterator();
			while (requestParamStringIterator.hasNext()) {
				String requestParam = requestParamStringIterator.next();
				String[] splittedRequestParam = requestParam.split("=");
				if (splittedRequestParam.length==2) {
					String reqeustParamName = splittedRequestParam[0];
					String reqeustParamValue = splittedRequestParam[1];
					requestParamMap.put(reqeustParamName, reqeustParamValue);
				}
			}
		}
		return requestParamMap;
	}
}
