package ua.myxazaur.lemonskin.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ua.myxazaur.lemonskin.helpers.ThirstHelper;

/**
 * Simple Difficulty exhaustion sync
 */
public class MessageThirstExhaustionSync implements IMessage, IMessageHandler<MessageThirstExhaustionSync, IMessage>
{
    float exhaustionLevel;

    public MessageThirstExhaustionSync()
    {
    }

    public MessageThirstExhaustionSync(float exhaustionLevel)
    {
        this.exhaustionLevel = exhaustionLevel;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeFloat(exhaustionLevel);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        exhaustionLevel = buf.readFloat();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(final MessageThirstExhaustionSync message, final MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(() ->
                ThirstHelper.setExhaustion(NetworkHelper.getSidedPlayer(ctx), message.exhaustionLevel)
        );
        return null;
    }
}