package javawfw.core.paths;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javawfw.http.HttpMethod;


public class Path {
	private String path;
	private HttpMethod httpMethod;
	private Pattern regexPattern;
	
	public Path(String path, HttpMethod httpMethod) {
		this.path = path;
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
}
