package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.tester;

import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Messenger;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class TesterUtils {

    public static final class PlayerInfo {
        Location location;
        int maximumNoDamageTicks;
        ItemStack[] inventoryContents;

        public PlayerInfo(Location location, int maximumNoDamageTicks, ItemStack[] inventoryContents) {

            this.location = location;
            this.maximumNoDamageTicks = maximumNoDamageTicks;
            this.inventoryContents = inventoryContents;
        }
    }

    public static void assertEquals(double a, double b, Tally tally, String testName, CommandSender... senders) {

        if(a == b) {

            tally.passed();
            for(CommandSender sender : senders) Messenger.sendNormalMessage(sender, "&aPASSÉ &f" + testName + " [" + a + "/" + b + "]");

        } else {

            tally.failed();
            for(CommandSender sender : senders) Messenger.sendNormalMessage(sender, "&cÉCHOUÉ &f" + testName + " [" + a + "/" + b + "]");
        }
    }
}
