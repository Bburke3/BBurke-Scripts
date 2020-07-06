package Skills;

import org.powerbot.bot.rt4.client.BoundaryObject;
import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.Npc;

import javax.swing.*;
import java.util.concurrent.Callable;

@Script.Manifest(name = "Fishing", description = "I fish", properties = "author = Bradley; topic = 999; client = 4;")

public class Fisher extends PollingScript<ClientContext> {

    private final String fishingTypes[] = {"Small Net", "Bait", "Lure", "Harpoon"};

    private String fishingType = ""+(String) JOptionPane.showInputDialog(null, "Choose Fishing Type", "Fishing Options", JOptionPane.PLAIN_MESSAGE, null, fishingTypes, fishingTypes[0]);

    private String fishName = ""+(String) JOptionPane.showInputDialog(null, "Enter Fish Name", "Fish Name", JOptionPane.PLAIN_MESSAGE);

    private String bankOrDrop[] = {"Bank", "Drop"};

    private String userOption = ""+(String) JOptionPane.showInputDialog(null, "Bank or Drop?", "Fishing", JOptionPane.PLAIN_MESSAGE, null, bankOrDrop, bankOrDrop[1]);

    @Override
    public void poll() {
        if (activate()) {
            fish();
        } else {
            if (userOption.equals("Bank"))
                bankLogs();
            else
                drop();
        }
    }

    public boolean activate() {
        return ctx.inventory.select().count() < 28;
    }

    public void fish() {
        String fishingSpotTile = "Fishing spot";

        if (fishingType.equals("Lure")) {
            fishingSpotTile = "Rod Fishing spot";
        }

        Npc fishingSpot = ctx.npcs.select().name(fishingSpotTile).nearest().poll();

        if (!ctx.players.local().inMotion() && ctx.players.local().animation() == -1) {

            if (!fishingSpot.inViewport()) {
                ctx.camera.turnTo(fishingSpot);
            }

            fishingSpot.interact(fishingType);

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().animation() == -1;
                }
            }, 12000, 15);
        }
    }

    public void drop() {
        for (Item i : ctx.inventory.name("Raw " + fishName)) {
            //i.interact("Drop");
            ctx.input.send("{VK_SHIFT down}");
            ctx.inventory.drop(i, true);
        }
        ctx.input.send("{VK_SHIFT up}");
    }

    public void bankLogs() {
        Item fish = ctx.inventory.select().name("Raw " + fishName).poll();

        ctx.camera.turnTo(ctx.bank.nearest());

        if (ctx.bank.open()) {
            fish.interact("Deposit-All");
            ctx.bank.close();
        }
    }
}
