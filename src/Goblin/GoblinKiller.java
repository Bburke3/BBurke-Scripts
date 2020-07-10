package Goblin;

import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;
import java.awt.*;
import java.util.concurrent.Callable;

@Script.Manifest(name = "Killer", description = "I Kill Things", properties = "author = Bradley; topic = 999; client = 4;")

public class GoblinKiller extends PollingScript<ClientContext> implements PaintListener {

    final static String ENEMY_NAME = "Goblin";

    final static String FOOD_NAME = "Shrimps";

    final static String ITEM = "Feather";

    int startExp = 0;

    @Override
    public void start() {
        System.out.println("starting");

        if (!hasFood()) {
            System.out.println("No Food");
        }

        startExp = ctx.skills.experience(Constants.SKILLS_MAGIC);

        ctx.camera.pitch(true);
    }

    //Constant loop
    @Override
    public void poll() {
        if (ctx.players.local().healthPercent() <= 25 && !hasFood()) {
            System.out.println("health too low!");
            stop();
        }
        else if (needsHeal()) {
            heal();
        } else if (shouldAttack()) {
            attack();
        }
        else {
            //pickUp();
        }
        changeStyle();
    }

    @Override
    public void stop() {
        System.out.println("Stopping");
    }

    public boolean needsHeal() {
        return ctx.combat.health() <= (int) ctx.combat.maxHealth() / 2;
    }

    public boolean shouldAttack() {
        return !ctx.players.local().healthBarVisible() && ctx.players.local().animation() == -1;
    }

    public boolean hasFood() {
        return ctx.inventory.select().name(FOOD_NAME).count() > 0;
    }

    public void attack() {
        final Npc enemy = ctx.npcs.select().name(ENEMY_NAME).within(25).select(new Filter<Npc>(){

            @Override
            public boolean accept(Npc npc) {
                return !npc.healthBarVisible() && npc.valid() && ctx.movement.reachable(ctx.players.local(), npc);
            }
        }).nearest().poll();

        //ctx.camera.turnTo(enemy);

        enemy.interact("Attack");

        Condition.wait(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().healthBarVisible() && ctx.players.local().animation() == -1;
            }
        }, 800,8);
    }

    public void heal() {
        Item foodToEat = ctx.inventory.select().name(FOOD_NAME).poll();

        final int startingHealth  = ctx.combat.health();

        foodToEat.interact("Eat");

        Condition.wait(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                final int currentHealth = ctx.combat.health();
                return currentHealth != startingHealth;
            }
        }, 200,20);
    }

    public void pickUp() {
        final GroundItem feathers = ctx.groundItems.select().name(ITEM).nearest().poll();

        feathers.interact("Take");

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !ctx.players.local().inMotion();
            }
        }, 400, 10);
    }

    public void changeStyle() {
//        if (ctx.skills.level(Constants.SKILLS_ATTACK) == 30 && ctx.combat.style(Combat.Style.ACCURATE)) {
//            ctx.combat.style(Combat.Style.DEFENSIVE);
//        }
//
//        if (ctx.skills.level(Constants.SKILLS_DEFENSE) == 30 && ctx.combat.style(Combat.Style.DEFENSIVE)) {
//            ctx.combat.style(Combat.Style.AGGRESSIVE);
//        }

        int adamant[] = {1161, 1123, 1199, 1073};

        for (final Item i : ctx.inventory.id(adamant)) {
            i.interact("Wear");
        }
    }

    @Override
    public void repaint(Graphics graphics) {
        long time = this.getTotalRuntime();
        long seconds = (time / 1000) % 60;
        long minutes = (time / (1000 * 60) % 60);
        long hours = (time / (1000 * 60 * 60)) % 24;

        int expGained = ctx.skills.experience(Constants.SKILLS_MAGIC) - startExp;

        Graphics2D g = (Graphics2D) graphics;

        g.setColor(Color.white);

        g.drawString("Killer Bot 3000", 20, 40);

        g.drawString("Running: " + String.format("%02d:%02d:%02d", hours, minutes, seconds), 20, 60);

        g.drawString("EXP/hour: " + (int) (expGained * (3600000D / time)), 20, 80);
    }
}
