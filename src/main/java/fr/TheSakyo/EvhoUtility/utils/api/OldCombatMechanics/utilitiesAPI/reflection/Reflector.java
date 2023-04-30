package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection;

import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.type.ClassType;
import org.bukkit.Bukkit;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reflector {

    private static String version;

    static {

        try { version = Bukkit.getServer().getClass().getName().split("\\.")[3]; }
        catch(Exception e) {

            System.err.println("Échec du chargement de la réflection");
            e.printStackTrace(System.err);
        }
    }

    public static String getVersion() { return version; }

    /**
     * Vérifie si la version actuelle du serveur est plus récente ou égale à celle fournie.
     *
     * @param major La version majeure cible
     * @param minor La version mineure cible. 0 pour tous
     * @param patch La version du patch cible. 0 pour tous
     * @return vrai si la version du serveur est plus récente ou égale à celle fournie
     */
    public static boolean versionIsNewerOrEqualAs(int major, int minor, int patch) {

        if(getMajorVersion() < major) { return false; }
        if(getMinorVersion() < minor) { return false; }

        return getPatchVersion() >= patch;
    }

    private static int getMajorVersion() { return Integer.parseInt(getVersionSanitized().split("_")[0]); }

    private static String getVersionSanitized() { return getVersion().replaceAll("[^\\d_]", ""); }

    private static int getMinorVersion() { return Integer.parseInt(getVersionSanitized().split("_")[1]); }

    private static int getPatchVersion() {

        String[] split = getVersionSanitized().split("_");
        if(split.length < 3) { return 0; }

        return Integer.parseInt(split[2]);
    }

    public static Class<?> getClass(ClassType type, String name) { return getClass(type.qualifyClassName(name)); }

    public static Class<?> getClass(String fqn) {

        try { return Class.forName(fqn); }
        catch(ClassNotFoundException e) { throw new RuntimeException("Impossible de charger la classe " + fqn, e); }
    }


    public static Method getMethod(Class<?> clazz, String name) {

        return Arrays.stream(clazz.getMethods()).filter(method -> method.getName().equals(name)).findFirst().orElse(null);
    }

    public static Method getMethod(Class<?> clazz, String name, int parameterCount) {

        return Arrays.stream(clazz.getMethods()).filter(method -> method.getName().equals(name) && method.getParameterCount() == parameterCount).findFirst().orElse(null);
    }

    public static Method getMethod(Class<?> clazz, String name, String... parameterTypeSimpleNames) {

        Function<Method, List<String>> getParameterNames = method -> Arrays.stream(method.getParameters()).map(Parameter::getType).map(Class::getSimpleName).collect(Collectors.toList());
        List<String> typeNames = Arrays.asList(parameterTypeSimpleNames);

        return Stream.concat(Arrays.stream(clazz.getDeclaredMethods()), Arrays.stream(clazz.getMethods())).filter(it -> it.getName().equals(name))
                .filter(it -> getParameterNames.apply(it).equals(typeNames))
                .peek(it -> it.setAccessible(true))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Method method, Object handle, Object... params) {

        try { return (T)method.invoke(handle, params); }
        catch(IllegalAccessException | InvocationTargetException e) { throw new RuntimeException(e); }
    }

    /**
     * Résout la méthode donnée, la met en cache et utilise ensuite cette instance pour toutes les invocations futures.
     * La fonction retournée invoque simplement la méthode mise en cache pour une cible donnée.
     *
     * @param clazz  'clazz' dans lequel se trouve la méthode
     * @param name   Le nom de la méthode
     * @param params Les paramètres de l'appel de la méthode
     * @param <T>    Le type de poignée
     * @param <R>    Le type de résultat de la méthode
     * @return Une fonction qui invoque la méthode récupérée en cache pour son argument
     */
    public static <T, R> Function<T, R> memoizeMethodAndInvoke(Class<T> clazz, String name, Object... params) {

        Method method = getMethod(clazz, name);
        return t -> invokeMethod(method, t, params);
    }

    public static Field getField(Class<?> clazz, String fieldName) {

        try { return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) { throw new RuntimeException(e); }
    }

    public static Field getFieldByType(Class<?> clazz, String simpleClassName) {

        for(Field declaredField : clazz.getDeclaredFields()) {
            if(declaredField.getType().getSimpleName().equals(simpleClassName)) { declaredField.setAccessible(true); return declaredField; }
        }
        throw new RuntimeException("Champ avec type " + simpleClassName + " non trouvé");
    }

    public static Field getInaccessibleField(Class<?> clazz, String fieldName) {

        Field field = getField(clazz, fieldName);
        field.setAccessible(true);
        return field;
    }

    public static Object getDeclaredFieldValueByType(Object object, String simpleClassName) throws Exception {

        for(Field declaredField : object.getClass().getDeclaredFields()) {

            if(declaredField.getType().getSimpleName().equals(simpleClassName)) {

                declaredField.setAccessible(true);
                return declaredField.get(object);
            }
        }

        throw new NoSuchFieldException("Impossible de trouver un champ de type " + simpleClassName + " dans " + object.getClass());
    }

    public static Object getFieldValue(Field field, Object handle) {

        field.setAccessible(true);

        try { return field.get(handle); }
        catch(IllegalAccessException e) { throw new RuntimeException(e); }
    }

    public static Constructor<?> getConstructor(Class<?> clazz, int numParams) {

        return Stream.concat(Arrays.stream(clazz.getDeclaredConstructors()), Arrays.stream(clazz.getConstructors())).filter(constructor -> constructor.getParameterCount() == numParams)
                .peek(it -> it.setAccessible(true))
                .findFirst()
                .orElse(null);
    }

    public static Constructor<?> getConstructor(Class<?> clazz, String... parameterTypeSimpleNames) {

        Function<Constructor<?>, List<String>> getParameterNames = constructor -> Arrays.stream(constructor.getParameters()).map(Parameter::getType).map(Class::getSimpleName).collect(Collectors.toList());
        List<String> typeNames = Arrays.asList(parameterTypeSimpleNames);

        return Stream.concat(Arrays.stream(clazz.getDeclaredConstructors()), Arrays.stream(clazz.getConstructors())).filter(constructor -> getParameterNames.apply(constructor).equals(typeNames))
                .peek(it -> it.setAccessible(true))
                .findFirst()
                .orElse(null);
    }

    /**
     * Vérifie si une classe donnée <i>d'une manière ou d'une autre</i> hérite d'une autre classe.
     *
     * @param toCheck        La classe à vérifier
     * @param inheritedClass La classe héritée, elle devrait avoir
     * @return Vrai si {@code toCheck} hérite en quelque sorte de<br/>
     * {@code inheritedClass}
     */
    public static boolean inheritsFrom(Class<?> toCheck, Class<?> inheritedClass) {

        if(inheritedClass.isAssignableFrom(toCheck)) { return true; }

        for(Class<?> implementedInterface : toCheck.getInterfaces()) { if(inheritsFrom(implementedInterface, inheritedClass)) { return true; } }
        return false;
    }

    public static <T> T getUnchecked(UncheckedReflectionSupplier<T> supplier) {

        try { return supplier.get(); }
        catch(ReflectiveOperationException e) { throw new RuntimeException(e); }
    }

    public static void doUnchecked(UncheckedReflectionRunnable runnable) {

        try {runnable.run(); }
        catch(ReflectiveOperationException e) { throw new RuntimeException(e); }
    }

    public interface UncheckedReflectionSupplier<T> { T get() throws ReflectiveOperationException; }

    public interface UncheckedReflectionRunnable { void run() throws ReflectiveOperationException; }
}
