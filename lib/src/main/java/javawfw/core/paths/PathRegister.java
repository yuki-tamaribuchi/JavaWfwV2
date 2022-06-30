package javawfw.core.paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.lang.reflect.Method;

import javawfw.annotations.Controller;
import javawfw.annotations.RequestMapping;
import javawfw.http.HttpMethod;
import javawfw.core.utils.types.pair.*;

public class PathRegister {
	public static List<Path> register(Map<Class<?>, Object> components) {
		List<Path> pathList = new ArrayList<>();

		components.forEach((clazz, component)->{
			if (clazz.isAnnotationPresent(Controller.class)){
				if (clazz.isAnnotationPresent(RequestMapping.class)) {
					RequestMapping rqmp =  clazz.getAnnotation(RequestMapping.class);
					Map<String, List<IPair<Method, HttpMethod>>> pathsInClassMap = createPathsInClassMap(clazz);
					pathsInClassMap.forEach((String path, List<IPair<Method, HttpMethod>> methodList)->{
						methodList.forEach((IPair<Method, HttpMethod> method)->{
							pathList.add(new Path(rqmp.path() + "/" + path, method.getFirst(), method.getSecond()));
						});
					});
				} else {
					Map<String, List<IPair<Method, HttpMethod>>> pathsInClassMap = createPathsInClassMap(clazz);
					pathsInClassMap.forEach((String path, List<IPair<Method, HttpMethod>> methodList)->{
						methodList.forEach((IPair<Method, HttpMethod> method)->{
							pathList.add(new Path(path, method.getFirst(), method.getSecond()));
						});
					});
				}
			}
		});
		return pathList;
	}

	private static Map<String, List<IPair<Method, HttpMethod>>> createPathsInClassMap(Class<?> clazz) {
		Map<String, List<IPair<Method, HttpMethod>>> pathsInClassMap = new LinkedHashMap<>();
		List<HttpMethod> httpMethodList = new ArrayList<>();
		List<Method> methodList = new ArrayList<>();
		List<String> pathList = new ArrayList<>();

		Arrays.asList(clazz.getDeclaredMethods()).stream()
		.filter(method->method.isAnnotationPresent(RequestMapping.class))
		.forEach(method->uncheck(()->{
			RequestMapping rqmp = method.getAnnotation(RequestMapping.class);
			pathList.add(rqmp.path());
			httpMethodList.add(rqmp.method());
			methodList.add(method);
		}));
		
		for (int i=0;i<pathList.size();i++) {
			IPair<Method, HttpMethod> httpMethodAndMethodPair = new Pair<>();
			httpMethodAndMethodPair.add(methodList.get(i), httpMethodList.get(i));

			if (pathsInClassMap.containsKey(pathList.get(i))) {
				pathsInClassMap.get(pathList.get(i)).add(httpMethodAndMethodPair);
			} else {
				List<IPair<Method, HttpMethod>> httpMethodAndMethodMapList = new ArrayList<>();
				httpMethodAndMethodMapList.add(httpMethodAndMethodPair);
				pathsInClassMap.put(pathList.get(i), httpMethodAndMethodMapList);
			}
		}
		return pathsInClassMap;
	}

	private static void uncheck(ThrowsRunnable runnable){
		try {
			runnable.run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@FunctionalInterface
	private static interface ThrowsRunnable {
		void run() throws Exception;
	}
}
