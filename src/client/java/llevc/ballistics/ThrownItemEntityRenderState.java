package llevc.ballistics;

import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.world.phys.Vec3;

public class ThrownItemEntityRenderState extends ItemEntityRenderState {
    public float xRot;
    public float yRot;
    public Vec3 rotationOffset;
    public Vec3 positionOffset;
    public boolean directional;

    public ThrownItemEntityRenderState() {}
}
