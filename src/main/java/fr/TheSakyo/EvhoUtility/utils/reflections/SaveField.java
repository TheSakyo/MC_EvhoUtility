package fr.TheSakyo.EvhoUtility.utils.reflections;

import java.lang.reflect.Field;


/***** -- CLASS UTILE POUR 'REFLECTIONUTIL' -- *****/

public class SaveField {

    private Field f;

    public SaveField(Field f) {

        try { f.setAccessible(true); this.f = f;
        } catch (Exception e) { e.printStackTrace(); }
    }

    public Object get(Object instance) {

        try { return f.get(instance);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public void set(Object instance , Object value , boolean stackTrace) {

        try { f.set(instance, value);
        } catch (Exception e) { if(stackTrace) e.printStackTrace(); }
    }
}

/***** -- CLASS UTILE POUR 'REFLECTIONUTIL' -- *****/