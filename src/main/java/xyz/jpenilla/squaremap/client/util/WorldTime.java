package xyz.jpenilla.squaremap.client.util;

import net.minecraft.world.level.Level;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class WorldTime {
    public static WorldTime instance = new WorldTime();
    private final Calendar calendar;

    public static WorldTime getInstance() {
        return instance;
    }

    private String time;
    private long lastChecked = 0;

    private WorldTime() {
        this.calendar = new GregorianCalendar();
    }

    public String getWorldTime(Level level, boolean realTime, boolean use24Hrs) {
        long now = System.currentTimeMillis();
        if (now - this.lastChecked > 500) {
            updateWorldTime(level, realTime, use24Hrs);
            this.lastChecked = now;
        }
        return this.time;
    }

    private void updateWorldTime(Level level, boolean realTime, boolean use24Hrs) {
        int hours, minutes;

        if (realTime) {
            calendar.setTime(new Date());
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);
        } else {
            long daytime = level.getDayTime() + 6000;
            hours = (int) (daytime / 1000) % 24;
            minutes = (int) ((daytime % 1000) * 60 / 1000);
        }

        boolean pm = hours >= 12;

        this.time = (use24Hrs ? "HH:mm" : "hh:mm a")
            .replace("HH", String.format("%02d", hours))
            .replace("hh", String.valueOf((hours %= 12) == 0 ? 12 : hours))
            .replace("mm", String.format("%02d", minutes))
            .replace("a", pm ? "PM" : "AM");
    }
}
