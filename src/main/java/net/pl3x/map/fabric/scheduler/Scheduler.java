package net.pl3x.map.fabric.scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Scheduler {
    private final List<Task> tasks = new ArrayList<>();

    public void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Iterator<Task> iter = this.tasks.iterator();
            while (iter.hasNext()) {
                Task task = iter.next();
                if (task.tick++ < task.delay) {
                    continue;
                }
                if (task.cancelled()) {
                    iter.remove();
                    continue;
                }
                task.run();
                if (task.repeat) {
                    task.tick = 0;
                    continue;
                }
                iter.remove();
            }
        });
    }

    public void cancelAll() {
        Iterator<Task> iter = this.tasks.iterator();
        while (iter.hasNext()) {
            iter.next().cancel();
            iter.remove();
        }
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void addTask(int delay, Runnable runnable) {
        addTask(delay, false, runnable);
    }

    public void addTask(int delay, boolean repeat, Runnable runnable) {
        addTask(new Task(delay, repeat) {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }
}
