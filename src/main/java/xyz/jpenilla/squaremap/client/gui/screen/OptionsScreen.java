package xyz.jpenilla.squaremap.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;
import xyz.jpenilla.squaremap.client.configuration.options.BooleanOption;
import xyz.jpenilla.squaremap.client.configuration.options.IntegerOption;
import xyz.jpenilla.squaremap.client.gui.screen.widget.Button;
import xyz.jpenilla.squaremap.client.gui.screen.widget.Slider;
import xyz.jpenilla.squaremap.client.gui.screen.widget.Tickable;

public class OptionsScreen extends AbstractScreen {
    private List<AbstractWidget> options;
    private static final Component RENDERER = new TranslatableComponent("squaremap-client.screen.options.renderer.title");
    private static final Component MINIMAP = new TranslatableComponent("squaremap-client.screen.options.minimap.title");

    private static final Component RENDERER_ENABLED = new TranslatableComponent("squaremap-client.screen.options.renderer.enabled");
    private static final Component RENDERER_ENABLED_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.renderer.enabled.tooltip");
    private static final Component FOG_OF_WAR = new TranslatableComponent("squaremap-client.screen.options.renderer.fog-of-war");
    private static final Component FOG_OF_WAR_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.renderer.fog-of-war.tooltip");
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
    private static final Component UPDATE_INTERVAL = new TranslatableComponent("squaremap-client.screen.options.minimap.update-interval");
    private static final Component UPDATE_INTERVAL_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.minimap.update-interval.tooltip");
    private static final Component POSITION_SIZE_ZOOM = new TranslatableComponent("squaremap-client.screen.options.minimap.position-size-zoom");
    private static final Component POSITION_SIZE_ZOOM_TOOLTIP = new TranslatableComponent("squaremap-client.screen.options.minimap.position-size-zoom.tooltip");
    private static final Component POSITION_SIZE_ZOOM_ERROR = new TranslatableComponent("squaremap-client.screen.options.minimap.position-size-zoom.error").withStyle(ChatFormatting.RED);

    private static final Component ON = CommonComponents.OPTION_ON.copy().withStyle(ChatFormatting.GREEN);
    private static final Component OFF = CommonComponents.OPTION_OFF.copy().withStyle(ChatFormatting.RED);
    private static final Component YES = CommonComponents.GUI_YES.copy().withStyle(ChatFormatting.GREEN);
    private static final Component NO = CommonComponents.GUI_NO.copy().withStyle(ChatFormatting.RED);

    public OptionsScreen(Screen parent) {
        this(SquaremapClientInitializer.instance(), parent);
    }

    public OptionsScreen(SquaremapClientInitializer squaremap, Screen parent) {
        super(squaremap, parent);
    }

    @Override
    public void init() {
        if (this.minecraft != null && parent instanceof FullMapScreen) {
            this.parent.init(this.minecraft, this.width, this.height);
        }

        int center = (int) (this.width / 2F);

        this.options = List.of(
                new Button(this, center - 154, 65, 150, 20, new BooleanOption(
                        RENDERER_ENABLED, RENDERER_ENABLED_TOOLTIP,
                        () -> this.squaremap.getConfig().getRenderer().getEnabled(),
                        value -> {
                            this.squaremap.getConfig().getRenderer().setEnabled(value);
                            this.squaremap.setRendererEnabled(value);
                        }
                )) {
                    @Override
                    public Component displayText() {
                        return option().getValue() ? ON : OFF;
                    }
                },
                new Button(this, center + 4, 65, 150, 20, new BooleanOption(
                        FOG_OF_WAR, FOG_OF_WAR_TOOLTIP,
                        () -> this.squaremap.getConfig().getRenderer().getFogOfWar(),
                        value -> this.squaremap.getConfig().getRenderer().setFogOfWar(value)
                )) {
                    @Override
                    public Component displayText() {
                        return option().getValue() ? ON : OFF;
                    }
                },
                new Button(this, center - 154, 110, 150, 20, new BooleanOption(
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
                        return option().getValue() ? YES : NO;
                    }
                },
                new Button(this, center + 4, 110, 150, 20, new BooleanOption(
                        NORTH_LOCKED, NORTH_LOCKED_TOOLTIP,
                        () -> this.squaremap.getConfig().getMinimap().getNorthLock(),
                        value -> {
                            this.squaremap.getConfig().getMinimap().setNorthLock(value);
                            this.squaremap.getMiniMap().setNorthLocked(value);
                        }
                )) {
                    @Override
                    public Component displayText() {
                        return option().getValue() ? YES : NO;
                    }
                },
                new Button(this, center - 154, 135, 150, 20, new BooleanOption(
                        FRAME, FRAME_TOOLTIP,
                        () -> this.squaremap.getConfig().getMinimap().getDrawFrame(),
                        value -> {
                            this.squaremap.getConfig().getMinimap().setDrawFrame(value);
                            this.squaremap.getMiniMap().setShowFrame(value);
                        }
                )) {
                    @Override
                    public Component displayText() {
                        return option().getValue() ? ON : OFF;
                    }
                },
                new Button(this, center + 4, 135, 150, 20, new BooleanOption(
                        CIRCULAR, CIRCULAR_TOOLTIP,
                        () -> this.squaremap.getConfig().getMinimap().getCircular(),
                        value -> {
                            this.squaremap.getConfig().getMinimap().setCircular(value);
                            this.squaremap.getMiniMap().setCircular(value);
                        }
                )) {
                    @Override
                    public Component displayText() {
                        return option().getValue() ? YES : NO;
                    }
                },
                new Button(this, center - 154, 160, 150, 20, new BooleanOption(
                        DIRECTIONS, DIRECTIONS_TOOLTIP,
                        () -> this.squaremap.getConfig().getMinimap().getDirections(),
                        value -> {
                            this.squaremap.getConfig().getMinimap().setDirections(value);
                            this.squaremap.getMiniMap().setShowDirections(value);
                        }
                )) {
                    @Override
                    public Component displayText() {
                        return option().getValue() ? ON : OFF;
                    }
                },
                new Button(this, center + 4, 160, 150, 20, new BooleanOption(
                        COORDINATES, COORDINATES_TOOLTIP,
                        () -> this.squaremap.getConfig().getMinimap().getCoordinates(),
                        value -> {
                            this.squaremap.getConfig().getMinimap().setCoordinates(value);
                            this.squaremap.getMiniMap().setShowCoordinates(value);
                        }
                )) {
                    @Override
                    public Component displayText() {
                        return option().getValue() ? ON : OFF;
                    }
                },
                new Slider(this, center - 154, 185, 150, 20, new IntegerOption(
                        UPDATE_INTERVAL, UPDATE_INTERVAL_TOOLTIP, 0, 20,
                        () -> this.squaremap.getConfig().getMinimap().getUpdateInterval(),
                        value -> {
                            this.squaremap.getConfig().getMinimap().setUpdateInterval(value);
                            this.squaremap.getMiniMap().setUpdateInterval(value);
                        }
                )),
                new Button(this, center + 4, 185, 150, 20,
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

        if (parent instanceof FullMapScreen) {
            int centerY = (int) (this.height / 2F);
            this.parent.render(matrixStack, centerX, centerY, 0);
        }

        super.render(matrixStack, mouseX, mouseY, delta);

        drawText(matrixStack, this.title, centerX, 15);

        drawText(matrixStack, RENDERER, centerX, 50);
        drawText(matrixStack, MINIMAP, centerX, 95);
    }

    @Override
    public void tick() {
        this.options.forEach(option -> {
            if (option instanceof Tickable tickable) {
                tickable.tick();
            }
        });
    }
}
