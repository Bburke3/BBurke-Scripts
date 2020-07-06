package osrs;

import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TreeCutter extends PollingScript<ClientContext> {

    //final static int REGTREES_IDS[] = {1570, 1637};

    // All woodcutting axes in order from bronze to dragon.
    final static int AXE_IDS[] = {1351, 1349, 1353, 1361, 1355, 1357, 1359, 6739};

    private List<Task> taskList = new ArrayList<Task>();

    @Override
    public void start() {
        taskList.addAll(Arrays.asList(new Chop(ctx), new Drop(ctx)));
    }

    @Override
    public void poll() {
        for (Task task : taskList) {
            if (task.activate()) {
                task.execute();
            }
        }
    }

    @Override
    public void stop() {

    }
}
