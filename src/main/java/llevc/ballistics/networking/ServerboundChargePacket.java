package llevc.ballistics.networking;

import llevc.ballistics.Ballistics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.Identifier;

public record ServerboundChargePacket(int integer) implements CustomPacketPayload {
    public static final Identifier chargePacketID = Identifier.fromNamespaceAndPath(Ballistics.MOD_ID,"charge_packet");
    public static final CustomPacketPayload.Type<ServerboundChargePacket> TYPE = new CustomPacketPayload.Type<ServerboundChargePacket>(chargePacketID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundChargePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ServerboundChargePacket::integer,
            ServerboundChargePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
