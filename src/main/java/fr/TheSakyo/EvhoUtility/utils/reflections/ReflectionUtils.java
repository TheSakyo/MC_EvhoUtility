/*
 * La licence MIT (MIT)
 *
 * Copyright (c) 2020 Crypto Morin
 *
 * La permission est accordée par la présente, sans frais, à toute personne obtenant une copie
 * de ce logiciel et des fichiers de documentation associés (le " Logiciel "), de traiter
 * dans le logiciel sans restriction, y compris, sans limitation, les droits
 * d'utiliser, de copier, de modifier, de fusionner, de publier, de distribuer, d'octroyer des sous-licences et/ou de vendre des copies du logiciel, ainsi que d'utiliser et d'échanger des informations.
 * et/ou de vendre des copies du logiciel, et d'autoriser les personnes à qui le logiciel est fourni à le faire, sous réserve de l'approbation de l'autorité compétente.
 * fourni à le faire, sous réserve des conditions suivantes :
 *
 * L'avis de copyright ci-dessus et cet avis d'autorisation doivent être inclus dans
 * toutes les copies ou parties substantielles du logiciel.
 *
 * LE LOGICIEL EST FOURNI "EN L'ÉTAT", SANS GARANTIE D'AUCUNE SORTE, EXPRESSE OU IMPLICITE,
 * Y COMPRIS, MAIS SANS S'Y LIMITER, LES GARANTIES DE QUALITÉ MARCHANDE, D'ADÉQUATION É UN USAGE PARTICULIER
 * PARTICULIER ET DE NON-VIOLATION. EN AUCUN CAS, LES AUTEURS OU LES DÉTENTEURS DE DROITS D'AUTEUR NE SERONT RESPONSABLES
 * POUR TOUTE RÉCLAMATION, TOUT DOMMAGE OU TOUTE AUTRE RESPONSABILITÉ, QUE CE SOIT DANS LE CADRE D'UNE ACTION CONTRACTUELLE, DÉLICTUELLE OU AUTRE,
 * LES AUTEURS OU LES DÉTENTEURS DE DROITS D'AUTEUR NE SERONT EN AUCUN CAS RESPONSABLES DE TOUTE RÉCLAMATION, DE TOUT DOMMAGE OU DE TOUTE AUTRE RESPONSABILITÉ, QU'IL S'AGISSE D'UNE ACTION CONTRACTUELLE, DÉLICTUELLE OU AUTRE, DÉCOULANT DE OU LIÉE AU LOGICIEL OU À SON UTILISATION OU À D'AUTRES TRANSACTIONS.
 */
package fr.TheSakyo.EvhoUtility.utils.reflections;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


/************************************/
/* CLASS UTILE ("REFLECTIONUTILS") */
/************************************/

public class ReflectionUtils {

    private final String packageName;
    private final String version;

    private boolean ignoreException = false;

    /*/ ########################################## CONSTRUCTOR ########################### /*/

    public ReflectionUtils() {

        packageName = Bukkit.getServer().getClass().getPackage().getName();
        version = packageName.substring(packageName.lastIndexOf(".") + 1);
    }

                                /* ---------------------- */

    public ReflectionUtils noException() {

        ignoreException = true;
        return this;
    }


    /*/ ########################################## FIELDS ########################### /*/

    public String getpackageNameVersion() { return packageName; }

    public String getVersion() { return version; }

    public SaveField getField(String name , Class<?> clazz) {

        try {

            Field f = clazz.getField(name);
            return new SaveField(f);

        } catch(Exception e) {

            if(ignoreException != true) e.printStackTrace();
            return null;
        }
    }

    public SaveField getDeclaredField(Class<?> clazz) {

        try {

            Field f = clazz.getDeclaredField("aK");
            return new SaveField(f);

        } catch(Exception e) {

            if(ignoreException != true) e.printStackTrace();
            return null;
        }
    }


    /*/ ########################################## MÉTHODES ########################### /*/

    public SaveMethod getMethode(String name , Class<?> clazz , Class<?>... parameterClasses) {

        try {

            Method m = clazz.getMethod(name, parameterClasses);
            return new SaveMethod(m);

        } catch(Exception e) {

            if(ignoreException != true) e.printStackTrace();
            return null;
        }
    }

    public SaveMethod getDeclaredMethode(String name , Class<?> clazz , Class<?>... parameterClasses) {

        try {

            Method m = clazz.getDeclaredMethod(name, parameterClasses);
            m.setAccessible(true);
            return new SaveMethod(m);

        } catch(Exception e) {

            if(ignoreException != true) e.printStackTrace();
            return null;
        }
    }

    private Class<?> getClass(String name, boolean asArray) {

        try {

            if(asArray) return Array.newInstance(Class.forName(name), 0).getClass();
            else return Class.forName(name);

        } catch(Exception e) {

            if(ignoreException != true) e.printStackTrace();
            return null;
        }
    }

    /*/ ########################################## MINECRAFT ########################### /*/

    public String getNMSPrefix() { return "net.minecraft."; }

    public String getCraftBukkitPrefix() { return "org.bukkit.craftbukkit." + version + "."; }

    public Class<?> getNMSClass(String name) { return getClass("net.minecraft." + name , false); }

    public Class<?> getNMSClassAsArray(String name) { return getClass("net.minecraft." + name , true); }

    public Class<?> getCraftBukkitClass(String name) { return getClass("org.bukkit.craftbukkit." + version + "." + name , false); }

    public Class<?> getCraftBukkitClassAsArray(String name) { return getClass("org.bukkit.craftbukkit." + version + "." + name , true); }

    private Object getEntityPlayer(Player p) {

        try { return getCraftBukkitClass("entity.CraftPlayer").getMethod("getHandle").invoke(p); }

        catch(Exception e) {

            if(ignoreException != true) e.printStackTrace();
            return null;
        }
    }

    public Object serializeString(String s) {

        try {

            Class<?> chatSerelizer = getCraftBukkitClass("util.CraftChatMessage");

            Method mSerelize = chatSerelizer.getMethod("fromString", String.class);

            return ((Object[])mSerelize.invoke(null, s))[0];

        } catch(Exception e) {

            if(ignoreException != true) e.printStackTrace();
            return null;
        }
    }

    private String fromIChatBaseComponent(Object component) {

        try {

            Class<?> chatSerelizer = getCraftBukkitClass("util.CraftChatMessage");

            Method mSerelize = chatSerelizer.getMethod("fromComponent", this.getNMSClass("IChatBaseComponent"));

            return (String) mSerelize.invoke(null, component);

        } catch(Exception e) {

            if(ignoreException != true) e.printStackTrace();
            return null;
        }

    }

    public Object getEnumGamemode(Player p) {

        try {

            Field fInteractManager = this.getNMSClass("server.level.EntityPlayer").getField("playerInteractManager");
            fInteractManager.setAccessible(true);
            Object oInteractManager = fInteractManager.get(getEntityPlayer(p));

            Field enumGamemode = this.getNMSClass("server.level.PlayerInteractManager").getDeclaredField("gamemode");
            enumGamemode.setAccessible(true);

            return enumGamemode.get(oInteractManager);

        } catch(Exception e) {

            if(ignoreException != true) e.printStackTrace();
            return null;
        }
    }

    public int getPing(Player p) {

        try {

            Field ping = getNMSClass("server.level.EntityPlayer").getDeclaredField("ping");
            ping.setAccessible(true);
            return (int) ping.get(getEntityPlayer(p));

        } catch (Exception e) {

            if(ignoreException != true) e.printStackTrace();
            return -1;
        }
    }

    public void sendPacket(Player p , Object packet) {

        try {

            Object nmsPlayer = getEntityPlayer(p);

            Field fieldCon = nmsPlayer != null ? nmsPlayer.getClass().getDeclaredField("playerConnection") : null;
            Object nmsCon = fieldCon != null ? fieldCon.get(nmsPlayer) : null;

            Method sendPacket = nmsCon.getClass().getMethod("sendPacket", getNMSClass("Packet"));
            sendPacket.invoke(nmsCon,packet);

        } catch (Exception e) { if(ignoreException != true) e.printStackTrace(); }
    }

    public int getID(Entity e) {

        try {

            Object entityEntity = getCraftBukkitClass("entity.CraftEntity").getMethod("getHandle").invoke(e);

            return (int) getNMSClass("world.entity.Entity").getMethod("getId").invoke(entityEntity);

        } catch (Exception ex) {

            ex.printStackTrace();
            return -1;
        }
    }

    public Object getEntity(Entity entity) {

        try { return getCraftBukkitClass("entity.CraftEntity").getMethod("getHandle").invoke(entity);

        } catch (Exception e) {

            if(ignoreException != true) e.printStackTrace();
            return null;
        }
    }

    public Object getWorldServer(World w) {

        try { return getCraftBukkitClass("CraftWorld").getMethod("getHandle").invoke(w); }

        catch (Exception e) {

            if(ignoreException != true) e.printStackTrace();
            return null;
        }
    }

    public Object getObjectNMSItemStack(ItemStack item) {

        try { return getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null,item); }

        catch (Exception e) {

            if(ignoreException != true) e.printStackTrace();
            return null;
        }
    }

    public String[] fromIChatBaseComponent(Object[] baseComponentArray) {

        String[] array = new String[baseComponentArray.length];

        for(int i = 0 ; i < array.length ; i++) { array[i] = fromIChatBaseComponent(baseComponentArray[i]); }
        return array;
    }

    public Object[] serializeString(String[] strings) {

        Object[] array = (Object[]) Array.newInstance(getNMSClass("IChatBaseComponent"), strings.length);

        for(int i = 0 ; i < array.length ; i++) { array[i] = serializeString(strings[i]); }
        return array;
    }
}
/************************************/
/* CLASS UTILE ("REFLECTIONUTILS") */
/************************************/