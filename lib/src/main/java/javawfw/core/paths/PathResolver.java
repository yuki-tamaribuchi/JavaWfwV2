package javawfw.core.paths;

import java.util.Iterator;
import java.util.List;

import javawfw.http.HttpMethod;

public class PathResolver {
	private List<Path> paths;

	public PathResolver(List<Path> paths) {
		this.paths = paths;
	}

	public Path resolve(String url, HttpMethod httpMethod) {
		if (url.contains("?")) {
			url = url.split("\\?")[0];
		}

		Iterator<Path> iterator = paths.iterator();
		while(iterator.hasNext()) {
			Path path = iterator.next();
			if (path.match(url, httpMethod)) {
				return path;
			}
		}
		return null;
	}
}
