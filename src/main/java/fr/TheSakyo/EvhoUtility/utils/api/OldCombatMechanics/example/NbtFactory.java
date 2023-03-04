package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.example;

import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.primitives.Primitives;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.Reflector;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.type.ClassType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class NbtFactory {

    // Convertit entre l'id NBT et la classe équivalente en java
    private static final BiMap<Integer, Class<?>> NBT_CLASS = HashBiMap.create();

    private static final BiMap<Integer, NbtType> NBT_ENUM = HashBiMap.create();

    // Instance partagée
    private static NbtFactory INSTANCE;

    private final Field[] DATA_FIELD = new Field[12];

    // La classe de base NBT
    private Class<?> BASE_CLASS;

    private Class<?> COMPOUND_CLASS;

    private Method NBT_CREATE_TAG;

    private Method NBT_GET_TYPE;

    private Field NBT_LIST_TYPE;

    // CraftItemStack
    private Class<?> CRAFT_STACK;

    private Field CRAFT_HANDLE;

    private Field STACK_TAG;

    /**
     * Construire une instance de la fabrique NBT en déduisant la classe de NBTBase.
     */
    private NbtFactory(){

        if(BASE_CLASS == null) {

            try {

                // On utilise des noms de champs codés en dur, mais cela ne pose pas de problème tant que nous avons affaire à CraftBukkit ou ses dérivés.
                // Cela ne fonctionne cependant pas dans MCPC+.
                ClassLoader loader = NbtFactory.class.getClassLoader();

                String packageName = Bukkit.getServer().getClass().getPackage().getName();
                Class<?> offlinePlayer = loader.loadClass(packageName + ".CraftOfflinePlayer");

                // Prépare le NBT
                COMPOUND_CLASS = getMethod(0, Modifier.STATIC, offlinePlayer, "getData").getReturnType();
                BASE_CLASS = Reflector.getClass(ClassType.NMS, "nbt.NBTBase");
                NBT_GET_TYPE = getMethod(0, Modifier.STATIC, BASE_CLASS, "getTypeId");
                NBT_CREATE_TAG = getMethod(Modifier.STATIC, 0, BASE_CLASS, "createTag", byte.class);

                // Prépare CraftItemStack
                CRAFT_STACK = loader.loadClass(packageName + ".inventory.CraftItemStack");
                CRAFT_HANDLE = getField(null, CRAFT_STACK, "handle");
                STACK_TAG = getField(null, CRAFT_HANDLE.getType(), "tag");


            } catch(ClassNotFoundException e) { throw new IllegalStateException("Impossible de trouver le lecteur hors ligne.", e); }
        }
    }

    /**
     * Récupère ou construit une usine NBT partagée.
     *
     * @return The factory.
     */
    private static NbtFactory get() {

        if(INSTANCE == null) INSTANCE = new NbtFactory();
        return INSTANCE;
    }

    /**
     * Construit une nouvelle liste NBT d'un type non spécifié.
     *
     * @return La liste NBT.
     */
    public static NbtList createList(Object... content) { return createList(Arrays.asList(content)); }

    /**
     * Construit une nouvelle liste NBT d'un type non spécifié.
     *
     * @return La liste NBT.
     */
    public static NbtList createList(Iterable<?> iterable) {

        NbtList list = get().new NbtList(INSTANCE.createNbtTag(NbtType.TAG_LIST, "", null));

        // Ajoute également le contenu
        for(Object obj : iterable) list.add(obj);
        return list;
    }

    /**
     * Construit un nouveau composé NBT.
     *
     * @return Le composé NBT.
     */
    public static NbtCompound createCompound() {

        return get().new NbtCompound(INSTANCE.createNbtTag(NbtType.TAG_COMPOUND, "", null));
    }

    /**
     * Construit un nouveau composé racine NBT.
     * Il faut donner un nom à ce composé, car il s'agit de l'objet racine.
     *
     * @param name - Le nom du composé.
     * @return Le Composé NBT.
     */
    public static NbtCompound createRootCompound(String name) {

        return get().new NbtCompound(INSTANCE.createNbtTag(NbtType.TAG_COMPOUND, name, null));
    }

    /**
     * Construit un nouveau 'wrapper NBT' à partir d'une liste.
     *
     * @param nmsList - La liste NBT.
     * @return Le 'Wrapper'.
     */
    public static NbtList fromList(Object nmsList) { return get().new NbtList(nmsList); }

    /**
     * Construit un nouveau 'wrapper NBT' à partir d'un composé.
     *
     * @param nmsCompound - le composé NBT.
     * @return Le 'Wrapper'.
     */
    public static NbtCompound fromCompound(Object nmsCompound) { return get().new NbtCompound(nmsCompound); }

    /**
     * Définit la balise composée NBT d'une pile d'éléments donnée.
     * La pile d'éléments doit être un wrapper pour un CraftItemStack. Utilisez
     *
     * @param stack - La pile d'éléments, ne peut pas être de l'air.
     * @param compound - Le nouveau composé NBT, ou NULL pour le supprimer.
     * @throws IllegalArgumentException Si la pile n'est pas un CraftItemStack, ou si elle représente de l'air.
     */
    public static void setItemTag(ItemStack stack, NbtCompound compound) {

        checkItemStack(stack);
        Object nms = getFieldValue(get().CRAFT_HANDLE, stack);

        // Maintenant, mettez à jour le composé de la balise
        setFieldValue(get().STACK_TAG, nms, compound.getHandle());
    }

    /**
     * Construit une enveloppe pour une balise NBT stockée (en mémoire) dans une pile d'éléments. C'est là que sont stockées données auxiliaires telles que l'enchantement, le nom et l'histoire sont stockées.
     * Il ne comprend pas les éléments matériels, la valeur des dommages ou le nombre.
     * La pile d'objets doit être un wrapper pour une CraftItemStack.
     *
     * @param stack - La pile d'objets.
     *
     * @return Un wrapper pour sa balise NBT.
     */
    public static NbtCompound fromItemTag(ItemStack stack) {
        checkItemStack(stack);
        Object nms = getFieldValue(get().CRAFT_HANDLE, stack);
        Object tag = getFieldValue(get().STACK_TAG, nms);

        // Créez le tag s'il n'existe pas.
        if(tag == null){

            NbtCompound compound = createRootCompound("tag");
            setItemTag(stack, compound);
            return compound;
        }

        return fromCompound(tag);
    }

    /**
     * Récupère une version CraftItemStack de la pile.
     *
     * @param stack - La pile à convertir.
     *
     * @return La version CraftItemStack.
     */
    public static ItemStack getCraftItemStack(ItemStack stack) {

        // Un besoin de conversion ?
        if(stack == null || get().CRAFT_STACK.isAssignableFrom(stack.getClass())) return stack;

        try {

            // Appeler le constructeur privé
            Constructor<?> caller = INSTANCE.CRAFT_STACK.getDeclaredConstructor(ItemStack.class);
            caller.setAccessible(true);
            return (ItemStack) caller.newInstance(stack);

        } catch(Exception e) { throw new IllegalStateException("Impossible de convertir " + stack + " + à un CraftItemStack."); }
    }

    /**
     * Assurez-vous que la pile donnée peut stocker des informations NBT arbitraires.
     *
     * @param stack - La pile a vérifié.
     */
    private static void checkItemStack(ItemStack stack) {

        if(stack == null) throw new IllegalArgumentException("La pile ne peut pas être NULL.");
        if(!get().CRAFT_STACK.isAssignableFrom(stack.getClass())) throw new IllegalArgumentException("La pile doit être un 'CraftItemStack', trouvé " + stack.getClass().getSimpleName());
        if(stack.getType() == Material.AIR) throw new IllegalArgumentException("'ItemStacks' représentant l'air ne peut pas stocker les informations NMS.");
    }

    /**
     * Invoque une méthode sur l'instance cible donnée en utilisant les paramètres fournis.
     *
     * @param method - La méthode a invoqué.
     * @param target - La cible.
     * @param params - Les paramètres à fournir.
     *
     * @return - Le résultat de la méthode.
     */
    private static Object invokeMethod(Method method, Object target, Object... params) {

        try { return method.invoke(target, params); }
        catch(Exception e) { throw new RuntimeException("Impossible d'invoquer la méthode " + method + " for " + target, e); }
    }

    private static void setFieldValue(Field field, Object target, Object value) {

        try { field.set(target, value); }
        catch(Exception e){throw new RuntimeException("Impossible de définir " + field + " pour " + target, e); }
    }

    private static Object getFieldValue(Field field, Object target) {

        try { return field.get(target); }
        catch(Exception e) { throw new RuntimeException("Impossible de récupérer " + field + " pour " + target, e); }
    }

    /**
     * Recherche de la première méthode définie de manière publique ou privée dont le nom et le nombre de paramètres sont donnés.
     *
     * @param requireMod - Modificateurs qui sont requis.
     * @param bannedMod - Modificateurs qui sont interdits.
     * @param clazz - Une classe pour commencer.
     * @param methodName - Le nom de la méthode, ou NULL pour l'ignorer.
     *
     * @return La première méthode de ce nom.
     *
     * @throws IllegalStateException Si nous ne pouvons pas trouver cette méthode.
     */
    private static Method getMethod(int requireMod, int bannedMod, Class<?> clazz, String methodName, Class<?>... params) {

        for(Method method : clazz.getDeclaredMethods()) {

            // Limitation : Ne gère pas les surcharges
            if((method.getModifiers() & requireMod) == requireMod && (method.getModifiers() & bannedMod) == 0 && (methodName == null || method.getName().equals(methodName)) &&
                    Arrays.equals(method.getParameterTypes(), params)) {

                method.setAccessible(true);
                return method;
            }
        }

        // Recherche dans chaque superclasse
        if(clazz.getSuperclass() != null) return getMethod(requireMod, bannedMod, clazz.getSuperclass(), methodName, params);

        throw new IllegalStateException(String.format("Impossible de trouver la méthode %s (%s).", methodName, Arrays.asList(params)));
    }

    /**
     * Recherche le premier champ défini de manière publique et privée du nom donné.
     *
     * @param instance  - Une instance de la classe avec le champ.
     * @param clazz     - Une classe optionnelle pour commencer, ou NULL pour la déduire de l'instance.
     * @param fieldName - Le nom du champ.
     *
     * @return Le premier champ de ce nom.
     *
     * @throws IllegalStateException Si nous ne pouvons pas trouver ce champ.
     */
    private static Field getField(Object instance, Class<?> clazz, String fieldName) {

        if(clazz == null) clazz = instance.getClass();

        // Ignore les règles d'accès
        for(Field field : clazz.getDeclaredFields()) {

            if(field.getName().equals(fieldName)) {

                field.setAccessible(true);
                return field;
            }
        }

        // Construire récursivement le champ correct
        if(clazz.getSuperclass() != null) return getField(instance, clazz.getSuperclass(), fieldName);

        throw new IllegalStateException("Impossible de trouver le champ " + fieldName + " dans " + instance);
    }

    private Map<String, Object> getDataMap(Object handle) {

        return (Map<String, Object>) getFieldValue(getDataField(NbtType.TAG_COMPOUND, handle), handle);
    }

    private List<Object> getDataList(Object handle) {

        return (List<Object>) getFieldValue(getDataField(NbtType.TAG_LIST, handle), handle);
    }

    /**
     * Convertit les objets List et Map enveloppés en leurs équivalents NBT respectifs.
     *
     * @param name  - Le nom de l'élément NBT à créer.
     * @param value - La valeur de l'élément à créer. Il peut s'agir d'une liste ou d'une carte.
     *
     * @return L'Élément NBT.
     */
    private Object unwrapValue(String name, Object value) {

        if(value == null) return null;

        if(value instanceof Wrapper) { return ((Wrapper) value).getHandle(); }
        else if(value instanceof List) { throw new IllegalArgumentException("Peut seulement insérer une WrappedList."); }
        else if(value instanceof Map) { throw new IllegalArgumentException("Peut uniquement insérer un WrappedCompound."); }
        else { return createNbtTag(getPrimitiveType(value), name, value); }
    }

    /**
     * Convertit un élément NBT donné en un wrapper primitif ou un équivalent List/Map.
     * Toutes les modifications apportées à tout objet mutable seront reflétées dans le ou les éléments NBT sous-jacents.
     *
     * @param nms - L'Élément NBT.
     * @return L'Équivalent du wrapper.
     */
    private Object wrapNative(Object nms) {
        if(nms == null)
            return null;

        if(BASE_CLASS.isAssignableFrom(nms.getClass())){

            final NbtType type = getNbtType(nms);

            // Manipule les différents types
            return switch(type) {

                case TAG_COMPOUND -> new NbtCompound(nms);
                case TAG_LIST -> new NbtList(nms);
                default -> getFieldValue(getDataField(type, nms), nms);
            };
        }

        throw new IllegalArgumentException("Unexpected type: " + nms);
    }

    /**
     * Construit une nouvelle balise SGEN NBT initialisée avec la valeur donnée.
     *
     * @param type  - Le type de NBT.
     * @param name  - Le nom de la balise NBT.
     * @param value - La valeur, ou NULL pour conserver la valeur originale.
     * @return La balise créée.
     */
    private Object createNbtTag(NbtType type, String name, Object value) {
        Object tag = invokeMethod(NBT_CREATE_TAG, null, (byte) type.id);

        if(value != null){
            setFieldValue(getDataField(type, tag), tag, value);
        }
        return tag;
    }

    /**
     * Récupérer le champ dans lequel la classe NBT stocke sa valeur.
     *
     * @param type - Le type de NBT.
     * @param nms  - L'Instance de la classe NBT.
     * @return Le champ correspondant.
     */
    private Field getDataField(NbtType type, Object nms) {

        if(DATA_FIELD[type.id] == null) DATA_FIELD[type.id] = getField(nms, null, type.getFieldName());
        return DATA_FIELD[type.id];
    }

    /**
     * Récupère le type de NBT d'une balise NBT NMS donnée.
     *
     * @param nms - la balise NBT native.
     * @return Le type correspondant.
     */
    private NbtType getNbtType(Object nms) {

        int type = (Byte) invokeMethod(NBT_GET_TYPE, nms);
        return NBT_ENUM.get(type);
    }

    /**
     * Récupère le type NBT le plus proche pour un type primitif donné.
     *
     * @param primitive - Le type primitif.
     * @return Le type correspondant.
     */
    private NbtType getPrimitiveType(Object primitive) {

        NbtType type = NBT_ENUM.get(NBT_CLASS.inverse().get(Primitives.unwrap(primitive.getClass())));

        // Afficher la valeur illégale au moins
        if(type == null) throw new IllegalArgumentException(String.format("Type illégal : %s (%s)", primitive.getClass(), primitive));
        return type;
    }

    /**
     * Activation ou non de la compression des flux.
     *
     * @author Kristian, TheSakyo
     */
    public enum StreamOptions {

        NO_COMPRESSION,
        GZIP_COMPRESSION,
    }

    private enum NbtType {

        TAG_END(0, Void.class),
        TAG_BYTE(1, byte.class),
        TAG_SHORT(2, short.class),
        TAG_INT(3, int.class),
        TAG_LONG(4, long.class),
        TAG_FLOAT(5, float.class),
        TAG_DOUBLE(6, double.class),
        TAG_BYTE_ARRAY(7, byte[].class),
        TAG_INT_ARRAY(11, int[].class),
        TAG_STRING(8, String.class),
        TAG_LIST(9, List.class),
        TAG_COMPOUND(10, Map.class);

        // Identifiant NBT unique
        public final int id;

        NbtType(int id, Class<?> type) {

            this.id = id;
            NBT_CLASS.put(id, type);
            NBT_ENUM.put(id, this);
        }

        private String getFieldName() {

            if(this == TAG_COMPOUND) return "map";
            else if(this == TAG_LIST) return "list";
            else return "data";
        }
    }

    /**
     * Représente un objet qui fournit une vue d'une classe native du NMS.
     *
     * @author Kristian, TheSakyo
     */
    public interface Wrapper {
        /**
         * Récupère la balise NBT native sous-jacente.
         *
         * @return Le système sous-jacent NBT.
         */
        Object getHandle();
    }

    /**
     * Représente un composé NBT racine.
     * <p>
     * Tous les changements apportés à cette carte seront reflétés dans le composé NBT sous-jacent. Les valeurs ne peuvent être que l'une des suivantes :
     * <ul>
     * <li>Types primitifs</li>
     * <li>{@link java.lang.String String}</li>
     * <li>{@link NbtList}</li>
     * <li>{@link NbtCompound}</li>
     * </ul>
     * <p>
     * Voir aussi :
     * <ul>
     * <li>{@link NbtFactory#createCompound()}</li>
     * <li>{@link NbtFactory#fromCompound(Object)}</li>
     * </ul>
     *
     * @author Kristian, TheSakyo
     */
    public final class NbtCompound extends ConvertedMap {
        private NbtCompound(Object handle){ super(handle, getDataMap(handle)); }

        // *** Simplification de l'accès à chaque valeur *** //
        public Byte getByte(String key, Byte defaultValue) { return containsKey(key) ? (Byte) get(key) : defaultValue; }
        public Short getShort(String key, Short defaultValue) { return containsKey(key) ? (Short) get(key) : defaultValue; }
        public Integer getInteger(String key, Integer defaultValue) { return containsKey(key) ? (Integer) get(key) : defaultValue; }
        public Long getLong(String key, Long defaultValue) { return containsKey(key) ? (Long) get(key) : defaultValue; }
        public Float getFloat(String key, Float defaultValue){ return containsKey(key) ? (Float) get(key) : defaultValue; }
        public Double getDouble(String key, Double defaultValue) { return containsKey(key) ? (Double) get(key) : defaultValue; }
        public String getString(String key, String defaultValue) { return containsKey(key) ? (String) get(key) : defaultValue; }
        public byte[] getByteArray(String key, byte[] defaultValue) { return containsKey(key) ? (byte[]) get(key) : defaultValue; }
        public int[] getIntegerArray(String key, int[] defaultValue) { return containsKey(key) ? (int[]) get(key) : defaultValue; }
        // *** Simplification de l'accès à chaque valeur *** //

        /**
         * Méthode pour les éléments qui ne sont pas créés selon la convention NBTtag et qui ont des nombres entiers là où il devrait y avoir des nombres longs.
         */
        public Long getIntegerOrLong(String key, Long defaultValue) {

            if(!containsKey(key)) return defaultValue;

            //Essaie de convertir en long ; si cela ne fonctionne pas, essayez de convertir en entier, si cela réussit, convertit 'Int' en 'Long' et renvoie.
            Long resultingValue = defaultValue;

            try { resultingValue = (Long)get(key); }
            catch(ClassCastException e) {

                //Ce n'est pas un long, essayez de le convertir en entier.
                try { resultingValue = ((Integer) get(key)).longValue(); }
                catch(ClassCastException e1){

                    System.out.println("La valeur NBT n'était ni un 'Long' ni un 'Integer'.");
                    e1.printStackTrace();
                }
            }

            return resultingValue;
        }

        /**
         * Récupère la liste par le nom donné.
         *
         * @param key       - Le nom de la liste.
         * @param createNew - Créer ou non une nouvelle liste si elle est manquante.
         *
         * @return Une liste existante, une nouvelle liste ou NULL.
         */
        public NbtList getList(String key, boolean createNew) {

            NbtList list = (NbtList)get(key);

            if(list == null) put(key, list = createList());
            return list;
        }

        /**
         * Récupère la carte par le nom donné.
         *
         * @param key       - Le nom de la carte.
         * @param createNew - Créer ou non une nouvelle carte si elle est manquante.
         *
         * @return Une carte existante, une nouvelle carte ou NULL.
         */
        public NbtCompound getMap(String key, boolean createNew) { return getMap(Collections.singletonList(key), createNew); }


        /**
         * Définit la valeur d'une entrée à un endroit donné.
         * Chaque élément du chemin (sauf la fin) est supposé être composé, et sera créé s'il est manquant.
         *
         * @param path  - Le chemin d'accès a entré.
         * @param value - La nouvelle valeur de cette entrée.
         *
         * @return La nouvelle valeur de cette entrée.
         */
        public NbtCompound putPath(String path, Object value) {
            List<String> entries = getPathElements(path);
            Map<String, Object> map = getMap(entries.subList(0, entries.size() - 1), true);

            map.put(entries.get(entries.size() - 1), value);
            return this;
        }

        /**
         * Récupère la valeur d'une entrée donnée dans l'arbre.
         * Tous les éléments du chemin (sauf la fin) sont supposés être des composés. L'opération de récupération sera annulée si l'un d'entre eux est manquant.
         *
         * @param path - Le chemin d'accès a entré.
         *
         * @return La valeur, ou NULL si elle n'est pas trouvée.
         */
        public <T> T getPath(String path) {
            List<String> entries = getPathElements(path);
            NbtCompound map = getMap(entries.subList(0, entries.size() - 1), false);

            if(map != null) { return (T) map.get(entries.get(entries.size() - 1)); }
            return null;
        }

        /**
         * Récupère une carte à partir d'un chemin donné.
         *
         * @param path      - Chemin des composés à rechercher.
         * @param createNew - Créer ou non de nouveaux composés en cours de route.
         *
         * @return La Carte à cet endroit.
         */
        private NbtCompound getMap(Iterable<String> path, boolean createNew) {
            NbtCompound current = this;

            for(String entry : path){
                NbtCompound child = (NbtCompound) current.get(entry);

                if(child == null) {

                    if(!createNew) throw new IllegalArgumentException("Cannot find " + entry + " in " + path);
                    current.put(entry, child = createCompound());
                }

                current = child;
            }

            return current;
        }

        /**
         * Divisez le chemin en éléments distincts.
         *
         * @param path - Le chemin de la séparation.
         *
         * @return Les éléments.
         */
        private List<String> getPathElements(String path) { return Lists.newArrayList(Splitter.on(".").omitEmptyStrings().split(path)); }
    }

    /**
     * Représente une liste NBT racine.
     * Voir aussi :
     * <ul>
     * <li>{@link NbtFactory#fromList(Object)}</li>
     * </ul>
     *
     * @author Kristian, TheSakyo
     */
    public final class NbtList extends ConvertedList { private NbtList(Object handle) { super(handle, getDataList(handle)); } }

    /**
     * Represents a class for caching wrappers.
     *
     * @author Kristian, TheSakyo
     */
    private final class CachedNativeWrapper {

        // Ne pas recréer les objets wrapper
        private final ConcurrentMap<Object, Object> cache = new MapMaker().weakKeys().makeMap();

        public Object wrap(Object value){
            Object current = cache.get(value);

            if(current == null){
                current = wrapNative(value);

                // Ne met en cache que les objets composites
                if(current instanceof ConvertedMap || current instanceof ConvertedList) {
                    cache.put(value, current);
                }
            }
            return current;
        }
    }

    /**
     * Représente une carte qui enveloppe une autre carte et convertit automatiquement
     * convertit automatiquement les entrées de son type et d'un autre type exposé.
     *
     * @author Kristian, TheSakyo
     */
    private class ConvertedMap extends AbstractMap<String, Object> implements Wrapper {
        private final Object handle;
        private final Map<String, Object> original;

        private final CachedNativeWrapper cache = new CachedNativeWrapper();

        public ConvertedMap(Object handle, Map<String, Object> original) {

            this.handle = handle;
            this.original = original;
        }

        // Pour convertir en avant et en arrière
        protected Object wrapOutgoing(Object value) { return cache.wrap(value); }

        protected Object unwrapIncoming(String key, Object wrapped) { return unwrapValue(key, wrapped); }

        // Modification
        @Override
        public Object put(String key, Object value) { return wrapOutgoing(original.put(key, unwrapIncoming(key, value))); }

        // Performance
        @Override
        public Object get(Object key) { return wrapOutgoing(original.get(key)); }

        @Override
        public Object remove(Object key) { return wrapOutgoing(original.remove(key)); }

        @Override
        public boolean containsKey(Object key) { return original.containsKey(key); }

        @Override
        public Set<Entry<String, Object>> entrySet() {

            return new AbstractSet<Entry<String, Object>>() {

                @Override
                public boolean add(Entry<String, Object> e) {
                    String key = e.getKey();
                    Object value = e.getValue();

                    original.put(key, unwrapIncoming(key, value));
                    return true;
                }

                @Override
                public int size() { return original.size(); }

                @Override
                public Iterator<Entry<String, Object>> iterator() { return ConvertedMap.this.iterator(); }
            };
        }

        private Iterator<Entry<String, Object>> iterator() {
            final Iterator<Entry<String, Object>> proxy = original.entrySet().iterator();

            return new Iterator<Entry<String, Object>>() {

                public boolean hasNext() { return proxy.hasNext(); }


                public Entry<String, Object> next() {

                    Entry<String, Object> entry = proxy.next();

                    return new SimpleEntry<>(entry.getKey(), wrapOutgoing(entry.getValue()));
                }


                public void remove() { proxy.remove(); }
            };
        }

        public Object getHandle() { return handle; }
    }

    /**
     * Représente une liste qui enveloppe une autre liste et convertit les éléments
     * de son type et d'un autre type exposé.
     *
     * @author Kristian, TheSakyo
     */
    private class ConvertedList extends AbstractList<Object> implements Wrapper {
        private final Object handle;

        private final List<Object> original;
        private final CachedNativeWrapper cache = new CachedNativeWrapper();

        public ConvertedList(Object handle, List<Object> original) {

            if(NBT_LIST_TYPE == null) NBT_LIST_TYPE = getField(handle, null, "type");
            this.handle = handle;
            this.original = original;
        }

        protected Object wrapOutgoing(Object value) { return cache.wrap(value); }

        protected Object unwrapIncoming(Object wrapped) { return unwrapValue("", wrapped); }

        @Override
        public Object get(int index) { return wrapOutgoing(original.get(index)); }

        @Override
        public int size() { return original.size(); }

        @Override
        public Object set(int index, Object element){ return wrapOutgoing(original.set(index, unwrapIncoming(element))); }

        @Override
        public void add(int index, Object element) {

            Object nbt = unwrapIncoming(element);

            // Définit le type de liste si c'est le premier élément.
            if(size() == 0) setFieldValue(NBT_LIST_TYPE, handle, (byte) getNbtType(nbt).id);
            original.add(index, nbt);
        }

        @Override
        public Object remove(int index) { return wrapOutgoing(original.remove(index)); }

        @Override
        public boolean remove(Object o) { return original.remove(unwrapIncoming(o)); }

        public Object getHandle() { return handle; }
    }
}
