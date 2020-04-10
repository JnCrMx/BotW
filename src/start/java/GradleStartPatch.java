import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GradleStartPatch
{
	public static void main(String[] args)
	{
		try
		{
			// IDEA won't let me import those Classes, therefore acquire them manually.
			Class<?> gradleStartClass = Class.forName("GradleStart");
			Class<?> gradleStartCommonClass = Class.forName("net.minecraftforge.gradle.GradleStartCommon");

			/*
			Call GradleStart.hackNatives(),
			because GradleStart.main(String[]) does it to inject the native library path.
			 */
			Method hackNativesMethod = gradleStartClass.getDeclaredMethod("hackNatives");
			hackNativesMethod.setAccessible(true);
			hackNativesMethod.invoke(null);

			/*
			Since GradleStart.hackNatives() sets ClassLoader.sys_paths to null
			we need to trigger a rebuild of it manually.
			It appears that this was done automatically in older Java versions.
			 */
			rebuildLibraryPaths();

			/*
			Replicate the procedure of GradleStart.main(String[]):
			Initiate GradleStart and then ...
			 */
			Object gradleStartInstance = gradleStartClass.getConstructor().newInstance();

			//... call GradleStart.launch(String[]) with our args.
			Method launchMethod = gradleStartCommonClass.getDeclaredMethod("launch", String[].class);
			launchMethod.setAccessible(true);
			launchMethod.invoke(gradleStartInstance, (Object) args);
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(InstantiationException e)
		{
			e.printStackTrace();
		}
		catch(InvocationTargetException e)
		{
			e.printStackTrace();
		}
		catch(NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	private static void rebuildLibraryPaths() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		// ClassLoader.initLibraryPaths() rebuilds the library paths, so we don't need to do much work here.
		Method initLibraryPathsMethod = ClassLoader.class.getDeclaredMethod("initLibraryPaths");
		initLibraryPathsMethod.setAccessible(true);
		initLibraryPathsMethod.invoke(null);
	}
}
