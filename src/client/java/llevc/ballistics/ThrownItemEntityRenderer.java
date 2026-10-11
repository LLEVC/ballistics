package llevc.ballistics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.phys.AABB;

public class ThrownItemEntityRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<ThrownItemProjectile,ItemEntityRenderState> {
    private static final float ITEM_MIN_HOVER_HEIGHT = 0.0625F;
    private static final float ITEM_BUNDLE_OFFSET_SCALE = 0.15F;
    private static final float FLAT_ITEM_DEPTH_THRESHOLD = 0.0625F;
    private final ItemModelResolver itemModelResolver;
    private final RandomSource random = RandomSource.create();

    public ThrownItemEntityRenderer(final EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
        this.shadowRadius = 0.15F;
        this.shadowStrength = 0.75F;
    }

    public ItemEntityRenderState createRenderState() {
        return new ItemEntityRenderState();
    }

    public void extractRenderState(final ThrownItemProjectile entity, final ItemEntityRenderState state, final float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.bobOffset = entity.bobOffs;
        state.extractItemGroupRenderState(entity, entity.getItem(), this.itemModelResolver);
    }

    public void submit(final ItemEntityRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        if (!state.item.isEmpty()) {
            poseStack.pushPose();
            AABB boundingBox = state.item.getModelBoundingBox();
            float minOffsetY = -((float)boundingBox.minY) + 0.0625F;
            float bob = Mth.sin((double)(state.ageInTicks / 10.0F + state.bobOffset)) * 0.1F + 0.1F;
            poseStack.translate(0.0F, bob + minOffsetY, 0.0F);
            float spin = ItemEntity.getSpin(state.ageInTicks, state.bobOffset);
            poseStack.rotate(Axis.YP, spin);
            ItemEntityRenderer.submitMultipleFromCount(poseStack, submitNodeCollector, state.lightCoords, state, this.random, boundingBox);
            poseStack.popPose();
            super.submit(state, poseStack, submitNodeCollector, camera);
        }
    }
}
