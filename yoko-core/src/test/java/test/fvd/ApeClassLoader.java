package test.fvd;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

public final class ApeClassLoader extends URLClassLoader {
    private final Set<String> skipClassNames = new HashSet<>();

    public ApeClassLoader() {
        super(((URLClassLoader)ApeClassLoader.class.getClassLoader()).getURLs(), 
                ApeClassLoader.class.getClassLoader().getParent());
    }

    public ApeClassLoader doNotLoad(Class<?>... types) {
        for (Class<?> type : types)
            skipClassNames.add(type.getName());
        return this;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (skipClassNames.contains(name))
            throw new ClassNotFoundException(name);
        return super.findClass(name);
    }

    public boolean apeMain(String...args) {
        if (alreadyAped()) {
            System.out.println("invoked from an already aped main() in target loader " + this.getClass().getClassLoader());
            return false;
        }
        System.out.println("aping call to main() from class loader " + this.getClass().getClassLoader());
        invokeApedMain(args);
        return true;
    }

    private static boolean alreadyAped() {
        String expectedLoader = ApeClassLoader.class.getName();
        String actualLoader = ApeClassLoader.class.getClassLoader().getClass().getName();
        return expectedLoader.equals(actualLoader);
    }

    private void invokeApedMain(String...args) {
        final String className = getCallerClassName();
        final ClassLoader oldTCCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this);
        try {
            Class<?> targetClass = Class.forName(className, true, this);
            Method m = targetClass.getMethod("main", String[].class);
            // m.invoke(null, (Object[])args); // BAD
            m.invoke(null, (Object)args);     // GOOD
        } catch (ClassNotFoundException e) {
            throw new Error("Failed to load mirrored class " + className, e);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new Error("Failed to get method main(String[]) for mirrored class " + className, e);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new Error("Failed to invoke method main(String[]) for mirrored class" + className, e);
        } catch (InvocationTargetException e) {
            rethrow(e.getTargetException());
            throw new AssertionError("This code should be unreachable");
        } finally {
            Thread.currentThread().setContextClassLoader(oldTCCL);
        }
    }

    private static void rethrow(Throwable t) throws RuntimeException {
        ApeClassLoader.<RuntimeException>useTypeErasureMadnessToThrowAnyCheckedException(t);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void useTypeErasureMadnessToThrowAnyCheckedException(Throwable t) throws T {
        throw (T)t;
    }

    private static String getCallerClassName() {
        StackTraceElement[] frames = new Throwable().getStackTrace();
        int i = 0;
        // find this class in the stack
        while (!!!frames[i].getClassName().equals(ApeClassLoader.class.getName())) i++;
        // find the next class down in the stack
        while (frames[i].getClassName().equals(ApeClassLoader.class.getName())) i++;
        return frames[i].getClassName();
    }
}