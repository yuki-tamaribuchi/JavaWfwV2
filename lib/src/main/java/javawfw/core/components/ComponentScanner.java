package javawfw.core.components;

import java.util.List;
import java.util.Map;
import java.net.URL;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.lang.Class;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javawfw.annotations.Component;
import javawfw.annotations.Controller;
import javawfw.annotations.PathVariable;
import javawfw.annotations.RequestMapping;
import javawfw.annotations.RequestParam;
import javawfw.http.response.IResponse;
import javawfw.core.exceptions.controller.WrongMethodReturnTypeException;
import javawfw.core.exceptions.controller.WrongParameterTypeException;
import javawfw.core.dependencyinjection.InjectDependencies;
import javawfw.core.exceptions.dependencyinjection.MultipleConstructorsException;

public class ComponentScanner {
	private final static Map<Class<?>, Object> components = new LinkedHashMap<>();

	public static Map<Class<?>, Object> scan(String packageName) {
		List<String> classes = getClassesUnderPackage(packageName);

		classes.stream()
		.filter(clazz->hasComponentAnnotation(getClassByName(clazz)))
		.forEach(clazz->uncheck(() ->{
			Class<?> gotClass = getClassByName(clazz);
			if (gotClass.isAnnotationPresent(Controller.class)) {
				validateController(gotClass);
			}
			
			try {
				Object instance = InjectDependencies.inject(gotClass);
				components.put(gotClass, instance);
			} catch(MultipleConstructorsException e) {
				e.printStackTrace();
			}			
		}));
		
		return components;
	}

	private static Class<?> getClassByName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static List<String> getClassesUnderPackage(String packageName) {
		List<String> result = null;
		String packagePath = packageName.replaceAll("\\.", "/");
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL root = cl.getResource(packagePath);
		
		try (Stream<Path> walk = Files.walk(Paths.get(root.toString().replace("file:", "")))) {
			result = walk.filter(Files::isRegularFile)
			.filter(p -> p.getFileName().toString().endsWith(".class"))
			.map(x -> x.toString().replaceAll("\\.class$", "").split("(?="+packagePath+")")[1].replaceAll("/", "."))
			.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static boolean hasComponentAnnotation(Class<?> clazz) {
		return clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Controller.class);
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


	private static void validateController(Class<?> clazz) throws WrongMethodReturnTypeException, WrongParameterTypeException {
		List<Method> methods = Arrays.asList(clazz.getMethods());
		methods.stream()
		.filter(method->method.isAnnotationPresent(RequestMapping.class))
		.forEach(method->{
			if (method.getReturnType()!=IResponse.class) {
				throw new WrongMethodReturnTypeException(clazz, method);
			}

			List<Parameter> parameters = Arrays.asList(method.getParameters());
			parameters.stream()
			.filter(param->param.isAnnotationPresent(PathVariable.class)||param.isAnnotationPresent(RequestParam.class))
			.forEach(param->{
				if (param.getType() != String.class) {
					throw new WrongParameterTypeException(clazz, method, param);
				}
			});
		});

	}
}
