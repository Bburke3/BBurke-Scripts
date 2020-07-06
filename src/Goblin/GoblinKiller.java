package Goblin;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.*;

import java.util.concurrent.Callable;

@Script.Manifest(name = "Killer", description = "I Kill Things", properties = "author = Bradley; topic = 999; client = 4;")

public class GoblinKiller extends PollingScript<ClientContext> {

    final static String ENEMY_NAME = "Chicken";

    final static String FOOD_NAME = "Bread";

    final static String ITEM = "Feather";

    @Override
    public void start() {
        System.out.println("starting");

        if (!hasFood()) {
            System.out.println("No Food");
        }
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
            pickUp();
        }
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
        final Npc enemy = ctx.npcs.select().name(ENEMY_NAME).select(new Filter<Npc>(){

            @Override
            public boolean accept(Npc npc) {
                return !npc.healthBarVisible();
            }
        }).nearest().poll();

        //ctx.camera.turnTo(enemy);

        enemy.interact("Attack");

        Condition.wait(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().healthBarVisible() && ctx.players.local().animation() == -1;
            }
        }, 800,4);
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
                return null;
            }
        }, 400, 10);
    }
}
