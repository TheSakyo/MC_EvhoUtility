package fr.TheSakyo.EvhoUtility.utils.api.CustomEntity.packets;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.TheSakyo.EvhoUtility.utils.api.CustomEntity.CustomEntityType;
import fr.TheSakyo.EvhoUtility.utils.entity.player.utilities.Skin;
import fr.TheSakyo.EvhoUtility.utils.reflections.RemapReflection;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Il est responsable du masquage des entités personnalisées en tant que {@link net.minecraft.server.level.ServerPlayer} pour les clients.
 */
public final class PacketInterceptor extends ChannelDuplexHandler {

    private final JavaPlugin plugin;

    private final Player player;
    private final CraftPlayer craftPlayer;

    private final IntList entityIDs = new IntArrayList();

    /**
     * @param player Le joueur pour lequel il faut gérer l'interception de paquets.
     */
    public PacketInterceptor(JavaPlugin plugin, Player player) {

        this.plugin = plugin;
        this.player = player;
        this.craftPlayer = (CraftPlayer)player;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        if(!(msg instanceof Packet<?> packet)) { return; }

        if(packet instanceof ClientboundAddExperienceOrbPacket xpOrbPacket) {

            int entityID = xpOrbPacket.getId();
            handleEntitySpawn(ctx, msg, promise, entityID);
            return;
        }

        if(packet instanceof ClientboundAddPlayerPacket spawnEntityPacket) {

            int entityID = spawnEntityPacket.getEntityId();
            handleEntitySpawn(ctx, msg, promise, entityID);
            return;
        }

        if(packet instanceof ClientboundAddEntityPacket spawnEntityLivingPacket) {

            int entityID = spawnEntityLivingPacket.getId();
            handleEntitySpawn(ctx, msg, promise, entityID);
            return;
        }

        if(packet instanceof ClientboundRotateHeadPacket entityHeadRotationPacket) {

            Entity entity = getEntity(entityHeadRotationPacket);

            if (entity == null) {
                super.write(ctx, msg, promise);
                return;
            }

            CustomEntityType<?> customEntityType = CustomEntityType.get(entity.getBukkitEntity());

            //Si l'entité personnalisée n'était nulle, nous ne voulons pas non plus de ce paquet.
            if(customEntityType == null) {

                super.write(ctx, msg, promise);
                return;
            }

            byte yaw = entityHeadRotationPacket.getYHeadRot();

            entity.absMoveTo(entity.getX(), entity.getY(), entity.getZ(), (yaw / 256f) * 360f, entity.getXRot());
            ClientboundTeleportEntityPacket entityTeleport = new ClientboundTeleportEntityPacket(entity);

            super.write(ctx, entityTeleport, promise);
            super.write(ctx, packet, new DefaultChannelPromise(ctx.channel()));
            return;
        }

        if(packet instanceof ClientboundMoveEntityPacket.Rot lookPacket) {

            Entity entity = getEntity(lookPacket);

            if(entity == null) {

                super.write(ctx, msg, promise);
                return;
            }

            CustomEntityType<?> customEntityType = CustomEntityType.get(entity.getBukkitEntity());

            //Si l'entité personnalisée n'était nulle, nous ne voulons pas non plus de ce paquet.
            if (customEntityType == null) {
                super.write(ctx, msg, promise);
            }

            //Nous voulons ignorer ces paquets, car nous les gérons nous-mêmes avec les paquets de téléportation dans l'écouteur HeadRotation.
            return;
        }

        if(packet instanceof ClientboundSetEntityDataPacket metadataPacket) {

            int entityID = metadataPacket.getId();
            Entity entity = getEntity(entityID);

            if(entity == null) {

                super.write(ctx, msg, promise);
                return;
            }

            List<SynchedEntityData.DataItem<?>> watchers = metadataPacket.getUnpackedData();
            List<SynchedEntityData.DataItem<?>> remaining = new ArrayList<>();

            if(watchers != null) {

                SynchedEntityData.DataItem<?> customName = null;

                for(SynchedEntityData.DataItem<?> watcher : watchers) {

                    int id = watcher.getAccessor().getId();
                    if(id < 15 || id > 20) { remaining.add(watcher); }

                    if(id == 2) { customName = watcher;}
                }

                //watchers.removeAll(remaining);

                if(customName != null) {

                    Optional<net.minecraft.network.chat.Component> name = (Optional<net.minecraft.network.chat.Component>) customName.getValue();

                    if(entity.isCustomNameVisible()) {

                        Scoreboard scoreboard = new Scoreboard();
                        PlayerTeam team = new PlayerTeam(scoreboard, getInvisibleName(entityID));

                        if(name.isPresent()) {
                            team.setNameTagVisibility(Team.Visibility.ALWAYS);
                            team.setPlayerPrefix(name.get());

                        } else { team.setNameTagVisibility(Team.Visibility.NEVER); }

                        team.getPlayers().add(getInvisibleName(entityID));

                        ClientboundSetPlayerTeamPacket teamPacket = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, false);
                        super.write(ctx, teamPacket, new DefaultChannelPromise(ctx.channel()));
                    }
                }

                if(remaining.isEmpty()) { return; }

                //if(!remaining.isEmpty()) {
                FriendlyByteBuf serializer = new FriendlyByteBuf(Unpooled.buffer());
                serializer.writeVarInt(entityID);
                SynchedEntityData.pack(remaining, serializer);

                ClientboundSetEntityDataPacket newPacket = new ClientboundSetEntityDataPacket(serializer);
                super.write(ctx, newPacket, promise);
                return;
               //}
            }
        }

        if(packet instanceof ClientboundRemoveEntitiesPacket destroyPacket) {

            IntList destroyedEntityIDs = destroyPacket.getEntityIds();

            for(int entityID : destroyedEntityIDs) {

                if(!entityIDs.contains(entityID)) { continue; }

                Scoreboard scoreboard = new Scoreboard();
                PlayerTeam team = new PlayerTeam(scoreboard, getInvisibleName(entityID));

                ClientboundSetPlayerTeamPacket removeTeamPacket = ClientboundSetPlayerTeamPacket.createRemovePacket(team);
                super.write(ctx, removeTeamPacket, new DefaultChannelPromise(ctx.channel()));
            }

            entityIDs.removeAll(destroyedEntityIDs);
        }

        //S'il atteint ce point, nous ne voulions pas intercepter ou modifier ce paquet, donc nous appelons super.write
        //pour s'assurer qu'il arrive quand même au client.
        super.write(ctx, msg, promise);
    }

    private void handleEntitySpawn(ChannelHandlerContext ctx, Object msg, ChannelPromise promise, int entityID) throws Exception {

        Entity entity = getEntity(entityID);

        //Si l'entité n'était nulle, nous ne voulons rien avoir à faire avec ce paquet.
        if(entity == null) {

            super.write(ctx, msg, promise);
            return;
        }

        MinecraftServer minecraftServer = entity.getServer();

        if(minecraftServer == null) {

            super.write(ctx, msg, promise);
            return;
        }

        CustomEntityType<?> customEntityType = CustomEntityType.get(entity.getBukkitEntity());

        //Si l'entité personnalisée n'était nulle, nous ne voulons pas non plus de ce paquet.
        if(customEntityType == null) {
            super.write(ctx, msg, promise);
            return;
        }

        entityIDs.add(entityID);
        EntityType displayType = customEntityType.getDisplayType();

        if(displayType == EntityType.PLAYER) {

            GameProfile profile = new GameProfile(UUID.randomUUID(), getInvisibleName(entityID));

            Skin skin = customEntityType.getSkin();

            if(skin != null) { profile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature())); }

            ServerPlayer fakePlayer = new ServerPlayer(minecraftServer, entity.level.getMinecraftWorld(), profile);

            fakePlayer.absMoveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());

            //C'est l'argent, faire que le client associe tous les paquets entrants de
            //l'entité réelle, gérée par le serveur, comme un faux joueur à la place.
            fakePlayer.setId(entityID);

            ClientboundPlayerInfoPacket infoPacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, fakePlayer);
            ClientboundAddPlayerPacket spawnPacket = new ClientboundAddPlayerPacket(fakePlayer);

            Scoreboard scoreboard = new Scoreboard();
            PlayerTeam team = new PlayerTeam(scoreboard, getInvisibleName(entityID));
            team.setNameTagVisibility(Team.Visibility.NEVER);
            team.getPlayers().add(getInvisibleName(entityID));

            ClientboundSetPlayerTeamPacket teamPacket = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true);

            super.write(ctx, infoPacket, promise);
            super.write(ctx, spawnPacket, new DefaultChannelPromise(ctx.channel()));
            super.write(ctx, teamPacket, new DefaultChannelPromise(ctx.channel()));

            //Ceci est fait pour enlever le faux nom de joueur de la tablist. S'il n'est pas retardé, le skin ne sera pas téléchargé.
            int delay = skin == null ? 0 : 40;

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

                ClientboundPlayerInfoPacket removeTablistPacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, fakePlayer);
                sendPacket(player, removeTablistPacket);
            }, delay);

        } else {

            CraftWorld world = (CraftWorld)player.getWorld();
            Location location = new Location(world, entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
            Entity fakeEntity = world.createEntity(location, customEntityType.getDisplayEntityClass(), false);
            fakeEntity.setId(entityID);

            ClientboundAddEntityPacket spawnPacket = new ClientboundAddEntityPacket(fakeEntity);
            super.write(ctx, spawnPacket, promise);
        }
    }

    private static Field entityPacketEntityIDField;
    private static Field headRotationPacketEntityIDField;

    static {
        try {

            // ⬇️ Essait de remapper le Variable "entityID" récupérant l'identifiant de l'entité du packet "ClientboundMoveEntityPacket" et "ClientboundRotateHeadPacket" ⬇️ //
            Class entityMovePacket = RemapReflection.remapClassName(ClientboundMoveEntityPacket.class);
            Class entityRotateHeadPacket = RemapReflection.remapClassName(ClientboundRotateHeadPacket.class);

            String entityMoveId = RemapReflection.remapFieldName(ClientboundMoveEntityPacket.class, "entityId");
            String entityRotateHeadId = RemapReflection.remapFieldName(ClientboundRotateHeadPacket.class, "entityId");
            // ⬆️ Essait de remapper le Variable "entityID" récupérant l'identifiant de l'entité du packet "ClientboundMoveEntityPacket" et "ClientboundRotateHeadPacket" ⬆️ //


            entityPacketEntityIDField = entityMovePacket.getDeclaredField(entityMoveId);
            entityPacketEntityIDField.setAccessible(true);

            headRotationPacketEntityIDField = entityRotateHeadPacket.getDeclaredField(entityRotateHeadId);
            headRotationPacketEntityIDField.setAccessible(true);

        } catch(NoSuchFieldException e) { e.printStackTrace(); }
    }

    private void sendPacket(Player player, Packet<?> packet) { ((CraftPlayer) player).getHandle().connection.send(packet); }

    /**
     * Une méthode pratique pour obtenir la {@link Entity} par ID à partir de la {@link LevelEntityGetter} interne,
     * car cela devrait être une approche sûre pour obtenir l'instance de l'entité.
     *
     * @param entityID L'ID de l'{@link Entity} à trouver.
     * @return L'{@link Entity} si elle est trouvée, sinon, null.
     */
    @Nullable
    private Entity getEntity(int entityID) {

        ServerPlayer entityPlayer = craftPlayer.getHandle();
        return ((ServerLevel)entityPlayer.level).entityManager.getEntityGetter().get(entityID);
    }

    @Nullable
    private Entity getEntity(ClientboundMoveEntityPacket packet) {

        try {

            int entityID = entityPacketEntityIDField.getInt(packet);
            return getEntity(entityID);

        } catch(IllegalAccessException e) { e.printStackTrace(); }
        return null;
    }

    @Nullable
    private Entity getEntity(ClientboundRotateHeadPacket packet) {

        try {

            int entityID = headRotationPacketEntityIDField.getInt(packet);
            return getEntity(entityID);

        } catch(IllegalAccessException e) { e.printStackTrace(); }
        return null;
    }

    private String getInvisibleName(int id) { return Integer.toHexString(id).replaceAll("(.)", "\u00a7$1"); }
}