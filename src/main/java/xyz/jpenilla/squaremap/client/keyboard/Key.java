package xyz.jpenilla.squaremap.client.keyboard;

public class Key {
    private final Action action;

    private int down = -1;

    public Key(Action action) {
        this.action = action;
    }

    public void press() {
        this.down++;
        if (this.down == 0 || this.down > 5) {
            this.action.execute();
        }
    }

    public void release() {
        this.down = -1;
    }

    public boolean pressed() {
        return this.down > -1;
    }

    @FunctionalInterface
    public interface Action {
        void execute();
    }
}
