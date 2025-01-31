package cc.uraniummc;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Field;

/**
 * Created by xjboss on 2017/9/4.
 */
@SideOnly(Side.CLIENT)
public class UraniumPlusClient {
    /** A timer for the current title and subtitle displayed */
    private int titlesTimer;
    /** The current title displayed */
    private String displayedTitle = "";
    /** The current sub-title displayed */
    private String displayedSubTitle = "";
    /** The time that the title take to fade in */
    private int titleFadeIn;
    /** The time that the title is display */
    private int titleDisplayTime;
    /** The time that the title take to fade out */
    private int titleFadeOut;
    private Minecraft mc= FMLClientHandler.instance().getClient();
    private GuiIngameForge gui=null;
    private Field recordT;
    private static UraniumPlusClient instance;

    public static UraniumPlusClient getInstance() {
        return instance;
    }

    UraniumPlusClient(){
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        instance = this;
        setDefaultTitlesTimes();
        try{
            recordT=GuiIngame.class.getDeclaredField("field_73845_h");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SubscribeEvent(priority= EventPriority.LOWEST)
    public void onRender(RenderGameOverlayEvent.Post evt){
        if(gui==null){
            gui=(GuiIngameForge)(mc.ingameGUI);
        }
        if(evt.type== RenderGameOverlayEvent.ElementType.ALL){
                ScaledResolution res = gui.getResolution();
                if(res==null)return;
                int width = res.getScaledWidth();
                int height = res.getScaledHeight();
                renderTitle(width, height, evt.partialTicks);
        }
    }
    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent tick){
        if(tick.phase== TickEvent.Phase.START) {
            if (this.titlesTimer > 0) {
                --this.titlesTimer;

                if (this.titlesTimer <= 0) {
                    this.displayedTitle = "";
                    this.displayedSubTitle = "";
                }
            }
        }
    }
    public FontRenderer getFontRenderer(){
        return mc.fontRenderer;
    }
    protected void renderTitle(int width, int height, float partialTicks)
    {
        if (titlesTimer > 0)
        {
            mc.mcProfiler.startSection("titleAndSubtitle");
            float age = (float)this.titlesTimer - partialTicks;
            int opacity = 255;

            if (titlesTimer > titleFadeOut + titleDisplayTime)
            {
                float f3 = (float)(titleFadeIn + titleDisplayTime + titleFadeOut) - age;
                opacity = (int)(f3 * 255.0F / (float)titleFadeIn);
            }
            if (titlesTimer <= titleFadeOut) opacity = (int)(age * 255.0F / (float)this.titleFadeOut);

            opacity = MathHelper.clamp_int(opacity, 0, 255);

            if (opacity > 8)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(width / 2), (float)(height / 2), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0F, 4.0F, 4.0F);
                int l = opacity << 24 & -16777216;
                this.getFontRenderer().drawString(this.displayedTitle, (-this.getFontRenderer().getStringWidth(this.displayedTitle) / 2), -10, 16777215 | l, true);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
                this.getFontRenderer().drawString(this.displayedSubTitle,(-this.getFontRenderer().getStringWidth(this.displayedSubTitle) / 2), 5, 16777215 | l, true);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            this.mc.mcProfiler.endSection();
        }
    }

    /**
     * Set the differents times for the titles to their default values
     */
    public void setDefaultTitlesTimes()
    {
        this.titleFadeIn = 10;
        this.titleDisplayTime = 70;
        this.titleFadeOut = 20;
    }
    public void displayTitle(String title, String subTitle, int timeFadeIn, int displayTime, int timeFadeOut)
    {
        if (title == null && subTitle == null && timeFadeIn < 0 && displayTime < 0 && timeFadeOut < 0)
        {
            this.displayedTitle = "";
            this.displayedSubTitle = "";
            this.titlesTimer = 0;
        }
        else if (title != null)
        {
            this.displayedTitle = title;
            this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
        }
        else if (subTitle != null)
        {
            this.displayedSubTitle = subTitle;
        }
        else
        {
            if (timeFadeIn >= 0)
            {
                this.titleFadeIn = timeFadeIn;
            }

            if (displayTime >= 0)
            {
                this.titleDisplayTime = displayTime;
            }

            if (timeFadeOut >= 0)
            {
                this.titleFadeOut = timeFadeOut;
            }

            if (this.titlesTimer > 0)
            {
                this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
            }
        }
    }

    public void setRecordPlaying(String text, boolean isPlaying)
    {
        gui.func_110326_a(text, isPlaying);
        try {
            recordT.setAccessible(true);
            recordT.set(gui, this.titleDisplayTime);
            recordT.setAccessible(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setRecordPlaying(IChatComponent component, boolean isPlaying)
    {
        setRecordPlaying(component.getUnformattedText(), isPlaying);
    }
}
