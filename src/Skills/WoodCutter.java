package Skills;

import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Item;

import javax.swing.*;
import java.util.concurrent.Callable;

@Script.Manifest(name = "Wood Cutter", description = "I cut trees and bank them if can, else I drop them", properties = "author = Bradley; topic = 999; client = 4;")

public class WoodCutter extends PollingScript<ClientContext> {

    private String userOptions[] = {"Bank", "Drop"};

    private String userOption = ""+(String) JOptionPane.showInputDialog(null, "Bank or Drop?", "Wood Cutting", JOptionPane.PLAIN_MESSAGE, null, userOptions, userOptions[1]);

    private String treeType = ""+(String) JOptionPane.showInputDialog(null, "Enter Tree Type", "Wood Cutting", JOptionPane.PLAIN_MESSAGE);

    @Override
    public void start() {
        System.out.println("Starting");
        ctx.camera.pitch(true);
    }

    @Override
    public void poll() {
        if (ctx.movement.energyLevel() == 100 && !ctx.movement.running())
            ctx.movement.running(true);

        if (activate()) {
            chop();
        } else {
            if(userOption.equals("Bank"))
                bankLogs();
            else
                drop();
        }
    }

    @Override
    public void stop() {
        System.out.println("Stopping");
    }

    public void chop() {
        final GameObject tree = ctx.objects.select().name(treeType).nearest().poll();

        if (!ctx.players.local().inMotion() && ctx.players.local().animation() == -1) {
//            // Puts tree in centre of screen.
//            if (!tree.inViewport()) {
//                ctx.camera.turnTo(tree);
//            }

            tree.interact("Chop down");

            Condition.wait(new Callable<Boolean>() {

                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().animation() == -1;
                }
            }, 4000, 10);
        }
    }

    public boolean activate() {
        return !ctx.inventory.isFull();
    }

    public void drop() {
        for (Item i : ctx.inventory.name(treeType + " logs")) {
            //i.interact("Drop");
            ctx.input.send("{VK_SHIFT down}");
            ctx.inventory.drop(i, true);
        }
        ctx.input.send("{VK_SHIFT up}");
    }

    public void bankLogs() {
        GameObject banker = ctx.objects.select().id(2897).nearest().poll();

        ctx.camera.turnTo(banker);

        ctx.movement.step(banker);

        banker.interact("Bank");
        ctx.bank.depositInventory();
        ctx.bank.close();
    }
}
