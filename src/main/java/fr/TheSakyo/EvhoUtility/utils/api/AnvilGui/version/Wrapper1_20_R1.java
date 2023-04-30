package fr.TheSakyo.EvhoUtility.utils.api.AnvilGui.version;

import fr.TheSakyo.EvhoUtility.utils.entity.player.PlayerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class Wrapper1_20_R1 implements VersionWrapper {

    private int getRealNextContainerId(Player player) { return toNMS(player).nextContainerCounter(); }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNextContainerId(Player player, Object container) { return ((AnvilContainer) container).getContainerId(); }


    /**
     * {@inheritDoc}
     */
    @Override
    public void handleInventoryCloseEvent(Player player) { CraftEventFactory.handleInventoryCloseEvent(toNMS(player), InventoryCloseEvent.Reason.UNKNOWN); }


    /**
     * {@inheritDoc}
     */
    @Override
    public void sendPacketOpenWindow(Player player, int containerId, String guiTitle) { PlayerEntity.sendPacket(new ClientboundOpenScreenPacket(containerId, MenuType.ANVIL, CraftChatMessage.fromString(guiTitle)[0]), player); }


    /**
     * {@inheritDoc}
     */
    @Override
    public void sendPacketCloseWindow(Player player, int containerId) { PlayerEntity.sendPacket(new ClientboundContainerClosePacket(containerId), player); }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setActiveContainerDefault(Player player) { (toNMS(player)).containerMenu = (toNMS(player)).inventoryMenu; }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setActiveContainer(Player player, Object container) { (toNMS(player)).containerMenu = (AbstractContainerMenu)container; }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setActiveContainerId(Object container, int containerId) { }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addActiveContainerSlotListener(Object container, Player player) { toNMS(player).initMenu((AbstractContainerMenu)container); }

    /**
     * {@inheritDoc}
     */
    @Override
    public Inventory toBukkitInventory(Object container) { return ((AbstractContainerMenu) container).getBukkitView().getTopInventory(); }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object newContainerAnvil(Player player, String guiTitle) { return new AnvilContainer(player, guiTitle); }


    /**
     * Transforme un {@link Player} en un DDN
     *
     * @param player Le joueur à convertir
     * @return  Le joueur (NMS).
     */
    private ServerPlayer toNMS(Player player) { return ((CraftPlayer)player).getHandle(); }


    /**
     * Modifications à ContainerAnvil qui font que vous n'avez pas besoin d'avoir de l'expérience pour utiliser cette enclume.
     */
    private class AnvilContainer extends AnvilMenu {

        public AnvilContainer(Player player, String guiTitle) {

            super(Wrapper1_20_R1.this.getRealNextContainerId(player), ((CraftPlayer) player).getHandle().getInventory(), ContainerLevelAccess.create(((CraftWorld)player.getWorld()).getHandle(), new BlockPos(0, 0, 0)));
            this.checkReachable = false;
            setTitle(CraftChatMessage.fromString(guiTitle)[0]);
        }

        @Override
        public void resumeRemoteUpdates() { super.resumeRemoteUpdates(); this.cost.set(0); }

        @Override
        public void removed(net.minecraft.world.entity.player.Player player) {}

        @Override
        protected void clearContainer(net.minecraft.world.entity.player.Player player, Container container) {}

        public int getContainerId() { return this.containerId; }
    }
}