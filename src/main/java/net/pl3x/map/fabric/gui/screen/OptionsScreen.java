package net.pl3x.map.fabric.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.pl3x.map.fabric.Pl3xMap;
import net.pl3x.map.fabric.configuration.options.BooleanOption;
import net.pl3x.map.fabric.configuration.options.IntegerOption;
import net.pl3x.map.fabric.gui.screen.widget.Button;
import net.pl3x.map.fabric.gui.screen.widget.Slider;
import net.pl3x.map.fabric.gui.screen.widget.Tickable;

public class OptionsScreen extends AbstractScreen {
    private List<AbstractWidget> options;
    private static final String RENDERER = I18n.get("pl3xmap.screen.options.renderer.title");
    private static final String MINIMAP = I18n.get("pl3xmap.screen.options.minimap.title");

    private static final Component RENDERER_ENABLED = new TranslatableComponent("pl3xmap.screen.options.renderer.enabled");
    private static final Component RENDERER_ENABLED_TOOLTIP = new TranslatableComponent("pl3xmap.screen.options.renderer.enabled.tooltip");
    private static final Component FOG_OF_WAR = new TranslatableComponent("pl3xmap.screen.options.renderer.fog-of-war");
    private static final Component FOG_OF_WAR_TOOLTIP = new TranslatableComponent("pl3xmap.screen.options.renderer.fog-of-war.tooltip");
    private static final Component MINIMAP_ENABLED = new TranslatableComponent("pl3xmap.screen.options.minimap.enabled");
    private static final Component MINIMAP_ENABLED_TOOLTIP = new TranslatableComponent("pl3xmap.screen.options.minimap.enabled.tooltip");
    private static final Component NORTH_LOCKED = new TranslatableComponent("pl3xmap.screen.options.minimap.north-locked");
    private static final Component NORTH_LOCKED_TOOLTIP = new TranslatableComponent("pl3xmap.screen.options.minimap.north-locked.tooltip");
    private static final Component FRAME = new TranslatableComponent("pl3xmap.screen.options.minimap.frame");
    private static final Component FRAME_TOOLTIP = new TranslatableComponent("pl3xmap.screen.options.minimap.frame.tooltip");
    private static final Component CIRCULAR = new TranslatableComponent("pl3xmap.screen.options.minimap.circular");
    private static final Component CIRCULAR_TOOLTIP = new TranslatableComponent("pl3xmap.screen.options.minimap.circular.tooltip");
    private static final Component DIRECTIONS = new TranslatableComponent("pl3xmap.screen.options.minimap.directions");
    private static final Component DIRECTIONS_TOOLTIP = new TranslatableComponent("pl3xmap.screen.options.minimap.directions.tooltip");
    private static final Component COORDINATES = new TranslatableComponent("pl3xmap.screen.options.minimap.coordinates");
    private static final Component COORDINATES_TOOLTIP = new TranslatableComponent("pl3xmap.screen.options.minimap.coordinates.tooltip");
    private static final Component UPDATE_INTERVAL = new TranslatableComponent("pl3xmap.screen.options.minimap.update-interval");
    private static final Component UPDATE_INTERVAL_TOOLTIP = new TranslatableComponent("pl3xmap.screen.options.minimap.update-interval.tooltip");
    private static final Component POSITION_SIZE_ZOOM = new TranslatableComponent("pl3xmap.screen.options.minimap.position-size-zoom");
    private static final Component POSITION_SIZE_ZOOM_TOOLTIP = new TranslatableComponent("pl3xmap.screen.options.minimap.position-size-zoom.tooltip");
    private static final Component POSITION_SIZE_ZOOM_ERROR = new TranslatableComponent("pl3xmap.screen.options.minimap.position-size-zoom.error");

    private static final String ON = "§a" + CommonComponents.OPTION_ON.getString();
    private static final String OFF = "§c" + CommonComponents.OPTION_OFF.getString();
    private static final String YES = "§a" + CommonComponents.GUI_YES.getString();
    private static final String NO = "§c" + CommonComponents.GUI_NO.getString();

    public OptionsScreen(Screen parent) {
        this(Pl3xMap.instance(), parent);
    }

    public OptionsScreen(Pl3xMap pl3xmap, Screen parent) {
        super(pl3xmap, parent);
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
                        () -> this.pl3xmap.getConfig().getRenderer().getEnabled(),
                        value -> {
                            this.pl3xmap.getConfig().getRenderer().setEnabled(value);
                            this.pl3xmap.setRendererEnabled(value);
                        }
                )) {
                    @Override
                    public String getStringValue() {
                        return getOption().getValue() ? ON : OFF;
                    }
                },
                new Button(this, center + 4, 65, 150, 20, new BooleanOption(
                        FOG_OF_WAR, FOG_OF_WAR_TOOLTIP,
                        () -> this.pl3xmap.getConfig().getRenderer().getFogOfWar(),
                        value -> this.pl3xmap.getConfig().getRenderer().setFogOfWar(value)
                )) {
                    @Override
                    public String getStringValue() {
                        return getOption().getValue() ? ON : OFF;
                    }
                },
                new Button(this, center - 154, 110, 150, 20, new BooleanOption(
                        MINIMAP_ENABLED, MINIMAP_ENABLED_TOOLTIP,
                        () -> this.pl3xmap.getConfig().getMinimap().getEnabled(),
                        value -> {
                            this.pl3xmap.getConfig().getMinimap().setEnabled(value);
                            if (value && this.pl3xmap.getServerManager().isOnServer() && this.pl3xmap.getServerManager().getUrl() != null) {
                                this.pl3xmap.getMiniMap().enable();
                            } else {
                                this.pl3xmap.getMiniMap().disable();
                            }
                        }
                )) {
                    @Override
                    public String getStringValue() {
                        return getOption().getValue() ? YES : NO;
                    }
                },
                new Button(this, center + 4, 110, 150, 20, new BooleanOption(
                        NORTH_LOCKED, NORTH_LOCKED_TOOLTIP,
                        () -> this.pl3xmap.getConfig().getMinimap().getNorthLock(),
                        value -> {
                            this.pl3xmap.getConfig().getMinimap().setNorthLock(value);
                            this.pl3xmap.getMiniMap().setNorthLocked(value);
                        }
                )) {
                    @Override
                    public String getStringValue() {
                        return getOption().getValue() ? YES : NO;
                    }
                },
                new Button(this, center - 154, 135, 150, 20, new BooleanOption(
                        FRAME, FRAME_TOOLTIP,
                        () -> this.pl3xmap.getConfig().getMinimap().getDrawFrame(),
                        value -> {
                            this.pl3xmap.getConfig().getMinimap().setDrawFrame(value);
                            this.pl3xmap.getMiniMap().setShowFrame(value);
                        }
                )) {
                    @Override
                    public String getStringValue() {
                        return getOption().getValue() ? ON : OFF;
                    }
                },
                new Button(this, center + 4, 135, 150, 20, new BooleanOption(
                        CIRCULAR, CIRCULAR_TOOLTIP,
                        () -> this.pl3xmap.getConfig().getMinimap().getCircular(),
                        value -> {
                            this.pl3xmap.getConfig().getMinimap().setCircular(value);
                            this.pl3xmap.getMiniMap().setCircular(value);
                        }
                )) {
                    @Override
                    public String getStringValue() {
                        return getOption().getValue() ? YES : NO;
                    }
                },
                new Button(this, center - 154, 160, 150, 20, new BooleanOption(
                        DIRECTIONS, DIRECTIONS_TOOLTIP,
                        () -> this.pl3xmap.getConfig().getMinimap().getDirections(),
                        value -> {
                            this.pl3xmap.getConfig().getMinimap().setDirections(value);
                            this.pl3xmap.getMiniMap().setShowDirections(value);
                        }
                )) {
                    @Override
                    public String getStringValue() {
                        return getOption().getValue() ? ON : OFF;
                    }
                },
                new Button(this, center + 4, 160, 150, 20, new BooleanOption(
                        COORDINATES, COORDINATES_TOOLTIP,
                        () -> this.pl3xmap.getConfig().getMinimap().getCoordinates(),
                        value -> {
                            this.pl3xmap.getConfig().getMinimap().setCoordinates(value);
                            this.pl3xmap.getMiniMap().setShowCoordinates(value);
                        }
                )) {
                    @Override
                    public String getStringValue() {
                        return getOption().getValue() ? ON : OFF;
                    }
                },
                new Slider(this, center - 154, 185, 150, 20, new IntegerOption(
                        UPDATE_INTERVAL, UPDATE_INTERVAL_TOOLTIP, 0, 20,
                        () -> this.pl3xmap.getConfig().getMinimap().getUpdateInterval(),
                        value -> {
                            this.pl3xmap.getConfig().getMinimap().setUpdateInterval(value);
                            this.pl3xmap.getMiniMap().setUpdateInterval(value);
                        }
                )),
                new Button(this, center + 4, 185, 150, 20,
                        POSITION_SIZE_ZOOM, POSITION_SIZE_ZOOM_TOOLTIP,
                        (button) -> {
                            if (this.pl3xmap.getMiniMap().isEnabled() && this.pl3xmap.getServerManager().isOnServer() && this.pl3xmap.getServerManager().getUrl() != null && this.pl3xmap.getWorld() != null) {
                                openScreen(new PositionScreen(this.pl3xmap, this));
                            } else {
                                button.active = false;
                                button.setMessage(POSITION_SIZE_ZOOM_ERROR);
                                this.pl3xmap.getScheduler().addTask(40, () -> {
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
