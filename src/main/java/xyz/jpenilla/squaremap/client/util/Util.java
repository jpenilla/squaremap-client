package xyz.jpenilla.squaremap.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public final class Util {
    private Util() {
    }

    @SuppressWarnings("unchecked")
    public static <X extends Throwable> RuntimeException rethrow(final Throwable t) throws X {
        throw (X) t;
    }

    public static void rotateScene(final PoseStack matrixStack, final double x, final double y, final float degrees) {
        matrixStack.translate(x, y, 0);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(degrees));
        matrixStack.translate(-x, -y, 0);
    }
}
