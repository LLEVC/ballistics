package llevc.ballistics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.AABB;

public class ThrownWeaponEntityRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<ThrownWeaponProjectile,ThrownItemEntityRenderState> {
    private static final float ITEM_MIN_HOVER_HEIGHT = 0.0625F;
    private static final float ITEM_BUNDLE_OFFSET_SCALE = 0.15F;
    private static final float FLAT_ITEM_DEPTH_THRESHOLD = 0.0625F;
    private final ItemModelResolver itemModelResolver;
    private final RandomSource random = RandomSource.create();

    public ThrownWeaponEntityRenderer(final EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
        this.shadowRadius = 0.15F;
        this.shadowStrength = 0.75F;
    }

    public ThrownItemEntityRenderState createRenderState() {
        return new ThrownItemEntityRenderState();
    }

    public void extractRenderState(final ThrownWeaponProjectile entity, final ThrownItemEntityRenderState state, final float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.bobOffset = entity.bobOffs;
        state.xRot = entity.getXRot(partialTicks);
        state.yRot = entity.getYRot(partialTicks);
        state.directional = true;
        state.extractItemGroupRenderState(entity, entity.getItem(), this.itemModelResolver);
        state.rotationOffset = entity.getModelRotationOffset();
        state.positionOffset = entity.getModelPositionOffset();
        itemModelResolver.updateForNonLiving(state.item, entity.getItem(), ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, entity);
    }

    public void submit(final ThrownItemEntityRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        if (!state.item.isEmpty()) {
            poseStack.pushPose();
            AABB boundingBox = state.item.getModelBoundingBox();
            //poseStack.rotateDegrees(Axis.XP, (float)state.rotationOffset.x);
            //poseStack.rotateDegrees(Axis.YP, state.yRot + 90.0F + (float)state.rotationOffset.y);
            //poseStack.rotateDegrees(Axis.ZP, state.xRot + 90f + (float)state.rotationOffset.z);
            poseStack.rotateDegrees(Axis.YP, state.yRot - 90.0F);
            poseStack.rotateDegrees(Axis.ZP, state.xRot + 90.0F);
            poseStack.rotate(Axis.XP, (float) Math.PI);
            poseStack.translate(boundingBox.getCenter().multiply(-1,-1,-1));
            ItemEntityRenderer.submitMultipleFromCount(poseStack, submitNodeCollector, state.lightCoords, state, this.random, boundingBox);
            poseStack.popPose();
            super.submit(state, poseStack, submitNodeCollector, camera);
        }
    }
}
