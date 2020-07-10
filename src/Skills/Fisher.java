package Skills;

import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.*;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;

@Script.Manifest(name = "Fishing", description = "I fish", properties = "author = Bradley; topic = 999; client = 4;")

public class Fisher extends PollingScript<ClientContext> implements PaintListener {

    // List of fishing types
    private final String fishingTypes[] = {"Small Net", "Big Net", "Bait", "Lure", "Cage", "Harpoon"};

    // User input for fishing type
    private String fishingType;

    // List of Most Fish
    private String fishNames[] = {"shrimps", "sardine", "karambwanji", "herring", "anchovies", "mackerel", "oyster", "casket", "trout", "cod", "pike", "salmon", "tuna", "lobster", "bass", "swordfish", "monkfish", "shark"};

    // For multiple fish being caught, e.g. trout and salmon.
    private String fishTypes[];

    // DELETE after switch statement
    //private String fishName = ""+(String) JOptionPane.showInputDialog(null, "Enter Fish Name", "Fish Name", JOptionPane.PLAIN_MESSAGE);

    // Bank or Drop
    private String bankOrDrop[] = {"Bank", "Drop"};

    // User input to Bank or drop
    private String toBankOrDrop;

    private int fishingSpotID = 1522;

    int startExp = 0;

    String runTime = "";

    @Override
    public void start() {
        fishingType =  ""+(String) JOptionPane.showInputDialog(null, "Choose Fishing Type", "Fishing Options", JOptionPane.PLAIN_MESSAGE, null, fishingTypes, fishingTypes[5]);

        toBankOrDrop = ""+(String) JOptionPane.showInputDialog(null, "Bank or Drop?", "Fishing", JOptionPane.PLAIN_MESSAGE, null, bankOrDrop, bankOrDrop[1]);

        assignFishingTypes();

        startExp = ctx.skills.experience(Constants.SKILLS_FISHING);

        ctx.camera.pitch(true);
    }

    @Override
    public void poll() {
        if (ctx.movement.energyLevel() == 100 && !ctx.movement.running())
            ctx.movement.running(true);

        if (activate()) {
            fish();
        } else {
            if (toBankOrDrop.equals("Bank"))
                bank();
            else
                drop();
        }
        cancelIdle();
    }

    @Override
    public void stop() {
        System.out.println(runTime);
    }

    public boolean activate() {
        return !ctx.inventory.isFull();
    }

    public void fish() {
        Npc fishingSpot = ctx.npcs.select().id(fishingSpotID).nearest().poll();

        if (!fishingSpot.inViewport()) {
            // Deviation of 4.
            ctx.camera.turnTo(fishingSpot, 30);
        }

        if (!ctx.players.local().inMotion() && ctx.players.local().animation() == -1) {

            if (fishingSpot.interact(fishingType)) {

                //bug test
                System.out.println("fishing");

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().animation() == -1;
                    }
                    }, 12000, 15);
            }
        }
    }

    public void drop() {
        int i = 0;
        while (i < fishingTypes.length) {
            for (final Item x : ctx.inventory.select().name(fishTypes)) {
                //Standard right-click drop.
                //x.interact("Drop");

                //Shift drop, fastest.
                ctx.input.send("{VK_SHIFT down}");
                ctx.inventory.drop(x, true);
            }
            i++;
        }
    }

    public void bank() {
        ctx.camera.turnTo(ctx.bank.nearest(), 35);

        int i = 0;
        while (i < fishingTypes.length) {
            for (final Item x : ctx.inventory.name(fishingTypes)) {
                if (ctx.bank.open()) {
                    x.interact("Deposit-All");
                    ctx.bank.close();
                }
            }
            i++;
        }
    }

    private void assignFishingTypes() {
        switch (fishingType) {
            case "Small Net":
                fishTypes = new String[4];
                fishTypes[0] = "Raw shrimps";
                fishTypes[1] = "Raw karambwanji";
                fishTypes[2] = "Raw anchovies";
                fishTypes[3] = "Raw monkfish";
                break;
            case "Big Net":
                fishTypes = new String[5];
                fishTypes[0] = "Raw mackerel";
                fishTypes[1] = "oyster";
                fishTypes[2] = "casket";
                fishTypes[3] = "Raw cod";
                fishTypes[4] = "Raw bass";
                break;
            case "Bait":
                fishTypes = new String[3];
                fishTypes[0] = "Raw sardine";
                fishTypes[1] = "Raw herring";
                fishTypes[2] = "Raw pike";
                break;
            case "Lure":
                fishTypes = new String[2];
                fishTypes[0] = "Raw trout";
                fishTypes[1] = "Raw salmon";
                break;
            case "Cage":
                fishTypes = new String[1];
                fishTypes[0] = "Raw lobster";
                break;
            case "Harpoon":
                fishTypes = new String[3];
                fishTypes[0] = "Raw tuna";
                fishTypes[1] = "Raw swordfish";
                fishTypes[2] = "Raw shark";
                break;
        }
    }

    public void cancelIdle() {
        ctx.inventory.peek();

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return null;
            }
        }, 400, 4);
    }

    @Override
    public void repaint(Graphics graphics) {
        long time = this.getTotalRuntime();
        long seconds = (time / 1000) % 60;
        long minutes = (time / (1000 * 60) % 60);
        long hours = (time / (1000 * 60 * 60)) % 24;

        int expGained = ctx.skills.experience(Constants.SKILLS_FISHING) - startExp;

        Graphics2D g = (Graphics2D) graphics;

        g.setColor(Color.white);

        g.drawString("Fishing Bot 3000", 20, 40);

        runTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        g.drawString("Running: " + runTime, 20, 60);

        g.drawString("EXP/hour: " + (int) (expGained * (3600000D / time)), 20, 80);
    }
}