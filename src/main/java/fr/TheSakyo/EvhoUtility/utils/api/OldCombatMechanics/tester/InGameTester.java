package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.tester;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Messenger;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.damage.WeaponDamages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class InGameTester {

    private final UtilityMain main;
    private final Map<UUID, TesterUtils.PlayerInfo> playerInfo;
    private Tally tally;
    private long delay;
    private Player attacker, defender;
    private Runnable extras; // pour donner une armure au défenseur, etc.

    // todo test de l'armure
    // todo test avec des armes enchantées
    // todo test avec armure enchantée
    // todo test de la durabilité des armures
    // todo test avec les coups critiques
    // todo test avec les effets des potions
    // todo test avec le blocage des boucliers

    public InGameTester(UtilityMain main) {
        this.main = main;
        delay = 0;
        playerInfo = new WeakHashMap<>();
    }

    /**
     * Effectuer tous les tests en utilisant les deux joueurs spécifiés
     */
    public void performTests(Player attacker, Player defender) {

        this.attacker = attacker;
        this.defender = defender;
        beforeAll();
        tally = new Tally();

        runAttacks(); // with no armour
        testArmour();

        Bukkit.getScheduler().runTaskLater(main, this::afterAll, delay);
    }

    private void runAttacks() {

        testMelee();
        testOverDamage();
    }

    private void appendExtras(Runnable runnable) {

        final Runnable oldExtras = extras;

        extras = () -> {

            oldExtras.run();
            runnable.run();
        };
    }

    private void testArmour() {

        extras = () -> {

             // donne au défenseur une armure
            defender.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
            defender.updateInventory();
        };

        runAttacks();
    }

    private void testMelee() {

        for(Material weaponType : WeaponDamages.getMaterialDamages().keySet()) {

            Bukkit.getScheduler().runTaskLater(main, () -> {

                beforeEach();
                testMeleeAttack(weaponType, 0);
            }, delay);

            delay += 2;
        }
    }

    private void testOverDamage() {

        Material[] weapons = {Material.WOODEN_HOE, Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_AXE};
        appendExtras(() -> defender.setMaximumNoDamageTicks(100));

        for(Material weaponType : weapons) {

            Bukkit.getScheduler().runTaskLater(main, () -> {

                beforeEach();
                testMeleeAttack(weaponType, 10);

            }, delay);

            delay += 40;
        }
    }

    private void testMeleeAttack(Material weaponType, long attackDelay) {

        ItemStack weapon = new ItemStack(weaponType);

        //todo inclure les enchantements des armes, les armures, etc. dans les calculs prévus.
        double expectedDamage = WeaponDamages.getDamage(weaponType);

        if((float)defender.getNoDamageTicks() > (float) defender.getMaximumNoDamageTicks() / 2.0F) expectedDamage -= defender.getLastDamage();

        attacker.getInventory().setItemInMainHand(weapon);
        attacker.updateInventory();

        double finalExpectedDamage = expectedDamage;
        monitor(finalExpectedDamage, attackDelay, "Attaque en mêlée " + weaponType);
    }

    private void monitor(double expectedDamage, long attackDelay, String message) {

        final boolean[] eventHappened = { false };

        Listener listener = new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onEvent(EntityDamageByEntityEvent e) {

                if(e.getDamager().getUniqueId() != attacker.getUniqueId() || e.getEntity().getUniqueId() != defender.getUniqueId()) return;
                eventHappened[0] = true;

                TesterUtils.assertEquals(expectedDamage, e.getFinalDamage(), tally, message, attacker, defender);
            }
        };

        Bukkit.getServer().getPluginManager().registerEvents(listener, main);

        // Retarde l'attaque lorsqu'on teste le sûr dommage.
        Bukkit.getScheduler().runTaskLater(main, () -> {

            attacker.attack(defender);
            afterEach();

            EntityDamageByEntityEvent.getHandlerList().unregister(listener);

            if(!eventHappened[0]) tally.failed();

        }, attackDelay);
    }

    private void beforeAll() {

        for(Player player : new Player[]{ attacker, defender }) {

            player.setGameMode(GameMode.SURVIVAL);

            final TesterUtils.PlayerInfo info = new TesterUtils.PlayerInfo(player.getLocation(), player.getMaximumNoDamageTicks(), player.getInventory().getContents());
            playerInfo.put(player.getUniqueId(), info);

            player.setMaximumNoDamageTicks(0);
        }
    }

    private void afterAll() {

        for(Player player : new Player[]{ attacker, defender }) {

            final UUID uuid = player.getUniqueId();
            final TesterUtils.PlayerInfo info = playerInfo.get(uuid);

            playerInfo.remove(uuid);
            player.getInventory().setContents(info.inventoryContents);
            player.setMaximumNoDamageTicks(info.maximumNoDamageTicks);

            Messenger.send(player, "Passed: &a%d &rFailed: &c%d &rTotal: &7%d", tally.getPassed(), tally.getFailed(), tally.getTotal());
        }
    }

    private void beforeEach() {

        for(Player player : new Player[]{ attacker, defender }) {

            player.getInventory().clear();
            player.setExhaustion(0);
            player.setHealth(20);
        }

        extras.run();
    }

    private void afterEach() {

        for (Player player : new Player[]{attacker, defender}) {

            final TesterUtils.PlayerInfo info = playerInfo.get(player.getUniqueId());

            player.setExhaustion(0);
            player.setHealth(20);
            player.teleport(info.location);
        }
    }
}
