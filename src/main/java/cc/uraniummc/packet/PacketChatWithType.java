package cc.uraniummc.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;

/**
 * Created by xjboss on 2017/9/4.
 */
public class PacketChatWithType implements IMessage {
    private byte type;
    private IChatComponent chat;

    public PacketChatWithType() {

    }

    public byte getType() {
        return type;
    }

    public IChatComponent getChat() {
        return chat;
    }

    public PacketChatWithType(IChatComponent chat, byte type) {
        this.type = type;
        this.chat = chat;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        try {
            chat = IChatComponent.Serializer.func_150699_a(pb.readStringFromBuffer(32767));
        } catch (Exception e) {
            throw new IndexOutOfBoundsException(e.getMessage());
        }
        type = pb.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {

        PacketBuffer pb = new PacketBuffer(buf);
        try {
            pb.writeStringToBuffer(IChatComponent.Serializer.func_150696_a(chat));
        } catch (Exception e) {
            throw new IndexOutOfBoundsException(e.getMessage());
        }
        pb.writeByte(type);
    }

}
