package xyz.jpenilla.squaremap.client.manager;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import xyz.jpenilla.squaremap.client.SquaremapClientInitializer;
import xyz.jpenilla.squaremap.client.util.Constants;

public class TextureManager {
    public static final ResourceLocation FRAME_CIRCLE = new ResourceLocation(Constants.MODID, "minimap_frame_circle_texture");
    public static final ResourceLocation FRAME_SQUARE = new ResourceLocation(Constants.MODID, "minimap_frame_square_texture");
    public static final ResourceLocation MASK_CIRCLE = new ResourceLocation(Constants.MODID, "minimap_mask_circle_texture");
    public static final ResourceLocation MASK_SQUARE = new ResourceLocation(Constants.MODID, "minimap_mask_square_texture");
    public static final ResourceLocation MAP = new ResourceLocation(Constants.MODID, "minimap_map_texture");
    public static final ResourceLocation SELF = new ResourceLocation(Constants.MODID, "minimap_self_texture");
    public static final ResourceLocation OVERWORLD_SKY = new ResourceLocation(Constants.MODID, "minimap_overworld_sky_texture");
    public static final ResourceLocation NETHER_SKY = new ResourceLocation(Constants.MODID, "minimap_nether_sky_texture");
    public static final ResourceLocation END_SKY = new ResourceLocation(Constants.MODID, "minimap_end_sky_texture");

    public void initialize() {
        loadTexture(FRAME_CIRCLE, "/assets/squaremap-client/gui/frame_circle.png");
        loadTexture(FRAME_SQUARE, "/assets/squaremap-client/gui/frame_square.png");
        loadTexture(MASK_CIRCLE, "/assets/squaremap-client/gui/mask_circle.png");
        loadTexture(MASK_SQUARE, "/assets/squaremap-client/gui/mask_square.png");
        loadTexture(SELF, "/assets/squaremap-client/gui/player.png");
        loadTexture(OVERWORLD_SKY, "/assets/squaremap-client/gui/overworld_sky.png");
        loadTexture(NETHER_SKY, "/assets/squaremap-client/gui/nether_sky.png");
        loadTexture(END_SKY, "/assets/squaremap-client/gui/end_sky.png");
    }

    private void loadTexture(ResourceLocation identifier, String resource) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::initialize);
            return;
        }
        InputStream stream = SquaremapClientInitializer.class.getResourceAsStream(resource);
        if (stream == null) {
            return;
        }
        try {
            DynamicTexture texture = new DynamicTexture(512, 512, true);
            texture.setPixels(NativeImage.read(stream));
            texture.upload();
            Minecraft.getInstance().getTextureManager().register(identifier, texture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawTexture(PoseStack matrixStack, ResourceLocation texture, float x0, float y0, float x1, float y1, float u, float v) {
        drawTexture(matrixStack, texture, x0, y0, x1, y1, u, u, v, v);
    }

    public void drawTexture(PoseStack matrixStack, ResourceLocation texture, float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        Matrix4f model = matrixStack.last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(model, x0, y1, 0F).uv(u0, v1).endVertex();
        bufferBuilder.vertex(model, x1, y1, 0F).uv(u1, v1).endVertex();
        bufferBuilder.vertex(model, x1, y0, 0F).uv(u1, v0).endVertex();
        bufferBuilder.vertex(model, x0, y0, 0F).uv(u0, v0).endVertex();
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
    }

    public ResourceLocation getTexture(Level world) {
        if (world.dimension() == Level.NETHER) {
            return TextureManager.NETHER_SKY;
        } else if (world.dimension() == Level.END) {
            return TextureManager.END_SKY;
        } else {
            return TextureManager.OVERWORLD_SKY;
        }
    }
}
