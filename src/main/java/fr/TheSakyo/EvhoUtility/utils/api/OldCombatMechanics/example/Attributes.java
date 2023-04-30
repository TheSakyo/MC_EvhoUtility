package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.example;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class Attributes {

    public ItemStack stack; // Ceci peut être modifié
    private final NbtFactory.NbtList attributes;

    public Attributes(ItemStack stack) {

        // Create un 'CraftItemStack' (sous le capot)
        this.stack = NbtFactory.getCraftItemStack(stack);

        // Chargement NBT
        NbtFactory.NbtCompound nbt = NbtFactory.fromItemTag(this.stack);
        this.attributes = nbt.getList("AttributeModifiers", true);
    }

    /**
     * Récupére la pile d'éléments modifiés.
     *
     * @return La pile d'éléments modifiée.
     */
    public ItemStack getStack() { return stack; }

    /**
     * Récupère le nombre d'attributs.
     *
     * @return Nombre d'attributs.
     */
    public int size() { return attributes.size(); }

    /**
     * Ajouter un nouvel attribut à la liste.
     *
     * @param attribute - Le nouvel attribut.
     */
    public void add(Attribute attribute) {

        Preconditions.checkNotNull(attribute.getName(), "must specify an attribute name.");
        attributes.add(attribute.data);
    }

    /**
     * Supprime la première instance de l'attribut donné.
     * L'Attribut sera supprimé en utilisant son UUID.
     *
     * @param attribute - L'Attribut à supprimer.
     * @return VRAI si l'attribut a été supprimé, FALSE sinon.
     */
    public boolean remove(Attribute attribute) {

        UUID uuid = attribute.getUUID();

        for(Iterator<Attribute> it = values().iterator(); it.hasNext(); ) {

            if(Objects.equal(it.next().getUUID(), uuid)) {

                it.remove();
                return true;
            }
        }
        return false;
    }

    public void clear() { attributes.clear(); }

    /**
     * Récupère l'attribut à un index donné.
     *
     * @param index - l'index à rechercher.
     * @return L'attribut à cet indice.
     */
    public Attribute get(int index) { return new Attribute((NbtFactory.NbtCompound) attributes.get(index)); }

    // Nous ne pouvons pas rendre les attributs eux-mêmes itérables sans les diviser en classes séparées.
    public Iterable<Attribute> values() { return () -> Iterators.transform(attributes.iterator(), element -> new Attribute((NbtFactory.NbtCompound) element)); }

    public enum Operation {

        ADD_NUMBER(0),
        MULTIPLY_PERCENTAGE(1),
        ADD_PERCENTAGE(2);
        private final int id;

        Operation(int id) { this.id = id; }

        public static Operation fromId(int id) {

            // Le balayage linéaire est très rapide pour les petits N
            for(Operation op : values()) { if(op.getId() == id) return op; }
            throw new IllegalArgumentException("Corrupt operation ID " + id + " detected.");
        }

        public int getId() { return id; }
    }

    public record AttributeType(String minecraftId) {

            private static final ConcurrentMap<String, AttributeType> LOOKUP = Maps.newConcurrentMap();

            public static final AttributeType GENERIC_MAX_HEALTH = new AttributeType("generic.maxHealth").register();
            public static final AttributeType GENERIC_FOLLOW_RANGE = new AttributeType("generic.followRange").register();
            public static final AttributeType GENERIC_ATTACK_DAMAGE = new AttributeType("generic.attackDamage").register();
            public static final AttributeType GENERIC_MOVEMENT_SPEED = new AttributeType("generic.movementSpeed").register();
            public static final AttributeType GENERIC_KNOCKBACK_RESISTANCE = new AttributeType("generic.knockbackResistance").register();
            public static final AttributeType GENERIC_ARMOR = new AttributeType("generic.armor").register();
            public static final AttributeType GENERIC_ARMOR_TOUGHNESS = new AttributeType("generic.armorToughness").register();

        /**
         * Construit un nouveau type d'attribut.
         * N'oubliez pas de {@link #register()} le type.
         *
         * @param minecraftId - l'ID du type.
         */
        public AttributeType {
        }

            /**
             * Récupère le type d'attribut associé à un ID donné.
             *
             * @param minecraftId L'ID à rechercher.
             * @return Le type d'attribut, ou NULL si non trouvé.
             */
            public static AttributeType fromId(String minecraftId) { return LOOKUP.get(minecraftId); }

            /**
             * Récupère chaque type d'attribut enregistré.
             *
             * @return Chaque type.
             */
            public static Iterable<AttributeType> values() { return LOOKUP.values(); }

            /**
             * Récupère l'identifiant Minecraft associé.
             *
             * @return L'ID associé.
             */
            @Override
            public String minecraftId() { return minecraftId; }

            /**
             * Enregistrer le type dans le registre central.
             *
             * @return Le type enregistré.
             */
            // Les constructeurs ne doivent pas avoir d'effets secondaires !
            public AttributeType register() {

                AttributeType old = LOOKUP.putIfAbsent(minecraftId, this);
                return old != null ? old : this;
            }
        }

    public static class Attribute {
        private NbtFactory.NbtCompound data;

        private Attribute(Builder builder) {

            data = NbtFactory.createCompound();
            setAmount(builder.amount);
            setOperation(builder.operation);
            setAttributeType(builder.type);
            setName(builder.name);
            setUUID(builder.uuid);
            setSlot(builder.slot);
        }

        private Attribute(NbtFactory.NbtCompound data) { this.data = data; }

        /**
         * Construit un nouveau constructeur d'attributs avec un UUID aléatoire et une opération par défaut d'addition de nombres.
         *
         * @return Le constructeur d'attributs.
         */
        public static Builder newBuilder() { return new Builder().uuid(UUID.randomUUID()).operation(Operation.ADD_NUMBER); }

        public double getAmount() {

            // Hack pour les mauvais plugins Bukkit qui ne réalisent pas qu'ILS SONT SUSPENDUS ÊTRE DOUBLES ! !! *soupir*
            Object value = data.get("Amount");

            if(value instanceof Number) { return ((Number)value).doubleValue(); }
            return 0.0;
        }

        public void setAmount(double amount) { data.put("Amount", amount); }

        public Operation getOperation() { return Operation.fromId(data.getInteger("Operation", 0)); }

        public void setOperation(Operation operation) {

            Preconditions.checkNotNull(operation, "l'opération ne peut pas être NULL.");
            data.put("Operation", operation.getId());
        }

        public AttributeType getAttributeType() { return AttributeType.fromId(data.getString("AttributeName", null)); }

        public void setAttributeType(AttributeType type) {

            Preconditions.checkNotNull(type, "le type ne peut pas être NULL.");
            data.put("AttributeName", type.minecraftId());
        }

        public String getName() { return data.getString("Name", null); }

        public void setName(String name) {

            Preconditions.checkNotNull(name, "le nom ne peut pas être NULL.");
            data.put("Name", name);
        }

        public UUID getUUID() { return new UUID(data.getIntegerOrLong("UUIDMost", null), data.getIntegerOrLong("UUIDLeast", null)); }

        public void setUUID(UUID id) {

            Preconditions.checkNotNull(id, "id ne peut pas être NULL.");
            data.put("UUIDLeast", id.getLeastSignificantBits());
            data.put("UUIDMost", id.getMostSignificantBits());
        }

        public String getSlot() { return data.getString("Slot", null); }

        public void setSlot(String slot) {

            Preconditions.checkNotNull(slot, "slot cannot be NULL.");
            data.put("Slot", slot);
        }

        /**
         * Une petite méthode simple ajoutée par Rayzr522
         */
        public NbtFactory.NbtCompound getData() {
            return data;
        }

        public void setData(NbtFactory.NbtCompound data) {
            this.data = data;
        }

        // Facilite la construction d'un attribut.
        public static class Builder {
            private double amount;
            private Operation operation = Operation.ADD_NUMBER;
            private AttributeType type;
            private String name;
            private UUID uuid;
            private String slot;

            private Builder() { /* Ne rendez pas cela accessible */ }

            public Builder amount(double amount) {

                this.amount = amount;
                return this;
            }

            public Builder operation(Operation operation) {

                this.operation = operation;
                return this;
            }

            public Builder type(AttributeType type) {

                this.type = type;
                return this;
            }

            public Builder name(String name) {

                this.name = name;
                return this;
            }

            public Builder uuid(UUID uuid) {

                this.uuid = uuid;
                return this;
            }

            public Builder slot(String slot) {

                this.slot = slot;
                return this;
            }

            public Attribute build() { return new Attribute(this); }
        }
    }
}