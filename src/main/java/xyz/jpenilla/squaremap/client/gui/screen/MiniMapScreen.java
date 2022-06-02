package xyz.jpenilla.squaremap.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;
import xyz.jpenilla.squaremap.client.config.options.BooleanOption;
import xyz.jpenilla.squaremap.client.config.options.IntegerOption;
import xyz.jpenilla.squaremap.client.gui.screen.widget.Button;
import xyz.jpenilla.squaremap.client.gui.screen.widget.Slider;

import java.util.List;

public class MiniMapScreen extends AbstractScreen {
    private static final Component MINIMAP_ENABLED = new TranslatableComponent("squaremap-client.screen.options.minimap.enabled");
    private static final Component MINIMAP_ENABLED_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.minimap.enabled.tooltip");
    private static final Component NORTH_LOCKED = new TranslatableComponent("squaremap-client.screen.options.minimap.north-locked");
    private static final Component NORTH_LOCKED_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.minimap.north-locked.tooltip");
    private static final Component FRAME = new TranslatableComponent("squaremap-client.screen.options.minimap.frame");
    private static final Component FRAME_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.minimap.frame.tooltip");
    private static final Component CIRCULAR = new TranslatableComponent("squaremap-client.screen.options.minimap.circular");
    private static final Component CIRCULAR_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.minimap.circular.tooltip");
    private static final Component DIRECTIONS = new TranslatableComponent("squaremap-client.screen.options.minimap.directions");
    private static final Component DIRECTIONS_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.minimap.directions.tooltip");
    private static final Component COORDINATES = new TranslatableComponent("squaremap-client.screen.options.minimap.coordinates");
    private static final Component COORDINATES_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.minimap.coordinates.tooltip");
    private static final Component SHOW_CLOCK = new TranslatableComponent("squaremap-client.screen.options.minimap.clock");
    private static final Component SHOW_CLOCK_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.minimap.clock.tooltip");
    private static final Component CLOCK_TYPE = new TranslatableComponent("squaremap-client.screen.options.minimap.clock-type");
    private static final Component CLOCK_TYPE_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.minimap.clock-type.tooltip");
    private static final Component CLOCK_24_HOURS = new TranslatableComponent("squaremap-client.screen.options.minimap.clock-24-hours");
    private static final Component CLOCK_24_HOURS_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.minimap.clock-24-hours.tooltip");
    private static final Component UPDATE_INTERVAL = new TranslatableComponent("squaremap-client.screen.options.minimap.update-interval");
    private static final Component UPDATE_INTERVAL_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.minimap.update-interval.tooltip");
    private static final Component POSITION_SIZE_ZOOM = new TranslatableComponent("squaremap-client.screen.options.minimap.position-size-zoom");
    private static final Component POSITION_SIZE_ZOOM_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.minimap.position-size-zoom.tooltip");
    private static final Component POSITION_SIZE_ZOOM_ERROR = new TranslatableComponent("squaremap-client.screen.options.minimap.position-size-zoom.error").withStyle(ChatFormatting.RED);
    private static final Component REAL_TIME = new TranslatableComponent("squaremap-client.screen.options.minimap.clock-real-time");
    private static final Component WORLD_TIME = new TranslatableComponent("squaremap-client.screen.options.minimap.clock-world-time");

    protected MiniMapScreen(SquaremapClientInitializer squaremap, Screen parent) {
        super(squaremap, parent);
    }

    @Override
    public void init() {
        if (this.minecraft != null && this.parent instanceof AbstractScreen abstractScreen && abstractScreen.parent instanceof FullMapScreen) {
            abstractScreen.parent.init(this.minecraft, this.width, this.height);
        }

        int center = (int) (this.width / 2F);

        this.options = List.of(
            new Button(this, center - 154, 65, 150, 20, new BooleanOption(
                MINIMAP_ENABLED, MINIMAP_ENABLED_TOOLTIP,
                () -> this.squaremap.getConfig().getMinimap().getEnabled(),
                value -> {
                    this.squaremap.getConfig().getMinimap().setEnabled(value);
                    if (value && this.squaremap.getServerManager().isOnServer() && this.squaremap.getServerManager().getUrl() != null) {
                        this.squaremap.getMiniMap().enable();
                    } else {
                        this.squaremap.getMiniMap().disable();
                    }
                }
            )) {
                @Override
                public Component displayText() {
                    return option().getValue() ? OptionsScreen.YES : OptionsScreen.NO;
                }
            },
            new Button(this, center + 4, 65, 150, 20, new BooleanOption(
                NORTH_LOCKED, NORTH_LOCKED_TOOLTIP,
                () -> this.squaremap.getConfig().getMinimap().getNorthLock(),
                value -> {
                    this.squaremap.getConfig().getMinimap().setNorthLock(value);
                    this.squaremap.getMiniMap().setNorthLocked(value);
                }
            )) {
                @Override
                public Component displayText() {
                    return option().getValue() ? OptionsScreen.YES : OptionsScreen.NO;
                }
            },
            new Button(this, center - 154, 90, 150, 20, new BooleanOption(
                FRAME, FRAME_TOOLTIP,
                () -> this.squaremap.getConfig().getMinimap().getDrawFrame(),
                value -> {
                    this.squaremap.getConfig().getMinimap().setDrawFrame(value);
                    this.squaremap.getMiniMap().setShowFrame(value);
                }
            )) {
                @Override
                public Component displayText() {
                    return option().getValue() ? OptionsScreen.ON : OptionsScreen.OFF;
                }
            },
            new Button(this, center + 4, 90, 150, 20, new BooleanOption(
                CIRCULAR, CIRCULAR_TOOLTIP,
                () -> this.squaremap.getConfig().getMinimap().getCircular(),
                value -> {
                    this.squaremap.getConfig().getMinimap().setCircular(value);
                    this.squaremap.getMiniMap().setCircular(value);
                }
            )) {
                @Override
                public Component displayText() {
                    return option().getValue() ? OptionsScreen.YES : OptionsScreen.NO;
                }
            },
            new Button(this, center - 154, 115, 150, 20, new BooleanOption(
                DIRECTIONS, DIRECTIONS_TOOLTIP,
                () -> this.squaremap.getConfig().getMinimap().getDirections(),
                value -> {
                    this.squaremap.getConfig().getMinimap().setDirections(value);
                    this.squaremap.getMiniMap().setShowDirections(value);
                }
            )) {
                @Override
                public Component displayText() {
                    return option().getValue() ? OptionsScreen.ON : OptionsScreen.OFF;
                }
            },
            new Button(this, center + 4, 115, 150, 20, new BooleanOption(
                COORDINATES, COORDINATES_TOOLTIP,
                () -> this.squaremap.getConfig().getMinimap().getCoordinates(),
                value -> {
                    this.squaremap.getConfig().getMinimap().setCoordinates(value);
                    this.squaremap.getMiniMap().setShowCoordinates(value);
                }
            )) {
                @Override
                public Component displayText() {
                    return option().getValue() ? OptionsScreen.ON : OptionsScreen.OFF;
                }
            },
            new Button(this, center - 154, 140, 150, 20, new BooleanOption(
                SHOW_CLOCK, SHOW_CLOCK_TOOLTIP,
                () -> this.squaremap.getConfig().getMinimap().getShowClock(),
                value -> {
                    this.squaremap.getConfig().getMinimap().setShowClock(value);
                    this.squaremap.getMiniMap().setShowClock(value);
                }
            )) {
                @Override
                public Component displayText() {
                    return option().getValue() ? OptionsScreen.ON : OptionsScreen.OFF;
                }
            },
            new Button(this, center + 4, 140, 150, 20, new BooleanOption(
                CLOCK_TYPE, CLOCK_TYPE_TOOLTIP,
                () -> this.squaremap.getConfig().getMinimap().getClockRealTime(),
                value -> {
                    this.squaremap.getConfig().getMinimap().setClockRealTime(value);
                    this.squaremap.getMiniMap().setClockRealTime(value);
                }
            )) {
                @Override
                public Component displayText() {
                    return option().getValue() ? REAL_TIME : WORLD_TIME;
                }
            },
            new Button(this, center - 154, 165, 150, 20, new BooleanOption(
                CLOCK_24_HOURS, CLOCK_24_HOURS_TOOLTIP,
                () -> this.squaremap.getConfig().getMinimap().getClock24Hrs(),
                value -> {
                    this.squaremap.getConfig().getMinimap().setClock24Hrs(value);
                    this.squaremap.getMiniMap().setClock24Hrs(value);
                }
            )) {
                @Override
                public Component displayText() {
                    return option().getValue() ? OptionsScreen.YES : OptionsScreen.NO;
                }
            },
            new Slider(this, center + 4, 165, 150, 20, new IntegerOption(
                UPDATE_INTERVAL, UPDATE_INTERVAL_TOOLTIP, 0, 20,
                () -> this.squaremap.getConfig().getMinimap().getUpdateInterval(),
                value -> {
                    this.squaremap.getConfig().getMinimap().setUpdateInterval(value);
                    this.squaremap.getMiniMap().setUpdateInterval(value);
                }
            )),
            new Button(this, center - 154, 190, 150, 20,
                POSITION_SIZE_ZOOM, POSITION_SIZE_ZOOM_TOOLTIP,
                (button) -> {
                    if (this.squaremap.getMiniMap().isEnabled() && this.squaremap.getServerManager().isOnServer() && this.squaremap.getServerManager().getUrl() != null && this.squaremap.getWorld() != null) {
                        openScreen(new PositionScreen(this.squaremap, this));
                    } else {
                        button.active = false;
                        button.setMessage(POSITION_SIZE_ZOOM_ERROR);
                        this.squaremap.getScheduler().addTask(40, () -> {
                            button.setMessage(POSITION_SIZE_ZOOM);
                            button.active = true;
                        });
                    }
                }
            )
        );

        this.options.forEach(this::addRenderableWidget);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
        int centerX = (int) (this.width / 2F);

        if (this.parent instanceof AbstractScreen abstractScreen && abstractScreen.parent instanceof FullMapScreen) {
            int centerY = (int) (this.height / 2F);
            abstractScreen.parent.render(matrixStack, centerX, centerY, 0);
        }

        super.render(matrixStack, mouseX, mouseY, delta);

        drawText(matrixStack, this.title, centerX, 15);
        drawText(matrixStack, OptionsScreen.MINIMAP, centerX, 30);
    }
}
