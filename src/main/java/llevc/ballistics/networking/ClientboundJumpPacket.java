package llevc.ballistics.networking;

import llevc.ballistics.Ballistics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public record ClientboundJumpPacket(Vec3 vec3) implements CustomPacketPayload {
    public static final Identifier jumpPacketId = Identifier.fromNamespaceAndPath(Ballistics.MOD_ID,"rocket_jump");
    public static final CustomPacketPayload.Type<ClientboundJumpPacket> TYPE = new CustomPacketPayload.Type<ClientboundJumpPacket>(jumpPacketId);
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundJumpPacket> CODEC = StreamCodec.composite(
            Vec3.STREAM_CODEC,
            ClientboundJumpPacket::vec3,
            ClientboundJumpPacket::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
