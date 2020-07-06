package Tasks;

import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

@Script.Manifest(name = "Miner", description = "How's it going!", properties = "author = Bradley; topic = 999; client = 4;")

public class Mine extends PollingScript<ClientContext> {

    final static int ROCK_ID = 1391;

    Tile rockLocation = Tile.NIL;

    @Override
    public void poll() {
        if (activate())
            execute();
    }

    public Boolean activate() {
        System.out.println("activating");
        // While player is not doing anything. select() ensures a new query. && has space in inventory.
        return ctx.objects.select().at(rockLocation).id(ROCK_ID).poll().equals(ctx.objects.nil()) || ctx.players.local().animation() == -1 && ctx.inventory.select().count() < 28;
    }

    public void execute() {
        GameObject rockToMine = ctx.objects.select().id(ROCK_ID).nearest().poll();

        rockLocation = rockToMine.tile();

        rockToMine.interact("Mine");

        Condition.wait(new Callable<Boolean>() {

            public Boolean call() throws Exception {
                return ctx.players.local().animation() != -1;
            }
        }, 200, 10);
        // every 200 milliseconds, 10 times.

        System.out.println("Executing");
    }
}
