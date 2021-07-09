package net.square.intect.checks.checks.aim;

import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Aim", type = "D", description = "Checks for low yaw change", maxVL = 20)
public class AimTypeD
    extends Check
{
    public AimTypeD(PlayerStorage data)
    {
        super(data);
    }

    @Override
    public void handle(IntectPacket packet)
    {
        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInUseEntity)
        {
            WrappedPacketInUseEntity wrappedPacketInUseEntity = new WrappedPacketInUseEntity(
                new NMSPacket(packet.getRawPacket()));

            if (wrappedPacketInUseEntity.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK)
            {
                double pitch = Math.abs(
                    getStorage().getRotationProcessor().getPitch() - getStorage().getRotationProcessor()
                        .getLastPitch());

                double yaw = Math.abs(
                    getStorage().getRotationProcessor().getYaw() - getStorage().getRotationProcessor().getLastYaw());

                if (pitch > 3.0 && yaw < 0.0001D)
                {
                    if (increaseBuffer() > 3)
                    {
                        fail("rotated invalid", String.format("yaw=%.4f", yaw), 1);
                    }
                }
                else
                {
                    decreaseBufferBy(0.25);
                }
            }
        }
    }
}
