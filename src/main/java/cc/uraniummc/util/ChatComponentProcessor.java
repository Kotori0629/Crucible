package cc.uraniummc.util;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

public class ChatComponentProcessor {
    public static IChatComponent processComponent(ICommandSender commandSender, IChatComponent component, Entity entityIn) throws CommandException {
        IChatComponent ichatcomponent = null;
        if (component instanceof ChatComponentText) {
            ichatcomponent = new ChatComponentText(((ChatComponentText) component).getChatComponentText_TextValue());
        } else {
            if (!(component instanceof ChatComponentTranslation)) {
                return component;
            }

            Object[] aobject = ((ChatComponentTranslation) component).getFormatArgs();

            for (int i = 0; i < aobject.length; ++i) {
                Object object = aobject[i];

                if (object instanceof IChatComponent) {
                    aobject[i] = processComponent(commandSender, (IChatComponent) object, entityIn);
                }
            }

            ichatcomponent = new ChatComponentTranslation(((ChatComponentTranslation) component).getKey(), aobject);
        }

        ChatStyle chatstyle = component.getChatStyle();

        if (chatstyle != null) {
            ichatcomponent.setChatStyle(chatstyle.createShallowCopy());
        }

        for (Object ichatcomponent2 : component.getSiblings()) {
            IChatComponent ichatcomponent1 = (IChatComponent) ichatcomponent2;
            ichatcomponent.appendSibling(processComponent(commandSender, ichatcomponent1, entityIn));
        }

        return ichatcomponent;
    }
}