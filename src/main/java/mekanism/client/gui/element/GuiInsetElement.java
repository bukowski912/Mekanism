package mekanism.client.gui.element;

import java.util.function.BooleanSupplier;
import mekanism.client.gui.IGuiWrapper;
import mekanism.common.inventory.warning.ISupportsWarning;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GuiInsetElement<DATA_SOURCE> extends GuiSideHolder implements ISupportsWarning<GuiInsetElement<DATA_SOURCE>> {

    private static final ResourceLocation WARNING_LEFT = MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "warning_left.png");
    private static final ResourceLocation WARNING_RIGHT = MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "warning_right.png");

    protected final int border;
    protected final int innerWidth;
    protected final int innerHeight;
    protected final DATA_SOURCE dataSource;
    protected final ResourceLocation overlay;

    @Nullable
    protected BooleanSupplier warningSupplier;

    public GuiInsetElement(ResourceLocation overlay, IGuiWrapper gui, DATA_SOURCE dataSource, int x, int y, int height, int innerSize, boolean left) {
        super(gui, x, y, height, left, false);
        this.overlay = overlay;
        this.dataSource = dataSource;
        this.innerWidth = innerSize;
        this.innerHeight = innerSize;
        //TODO: decide what to do if this doesn't divide nicely
        this.border = (width - innerWidth) / 2;
        this.clickSound = BUTTON_CLICK_SOUND;
        active = true;
    }

    @Override
    public GuiInsetElement<DATA_SOURCE> warning(@NotNull WarningType type, @NotNull BooleanSupplier warningSupplier) {
        this.warningSupplier = ISupportsWarning.compound(this.warningSupplier, gui().trackWarning(type, warningSupplier));
        return this;
    }

    @Override
    public boolean isMouseOver(double xAxis, double yAxis) {
        //TODO: override isHovered
        return this.active && this.visible && xAxis >= getX() + border && xAxis < getRight() - border && yAxis >= getY() + border && yAxis < getBottom() - border;
    }

    @Override
    protected int getButtonX() {
        return super.getButtonX() + border + (left ? 1 : -1);
    }

    @Override
    protected int getButtonY() {
        return super.getButtonY() + border;
    }

    @Override
    protected int getButtonWidth() {
        return innerWidth;
    }

    @Override
    protected int getButtonHeight() {
        return innerHeight;
    }

    protected ResourceLocation getOverlay() {
        return overlay;
    }

    @Override
    protected void draw(@NotNull GuiGraphics guiGraphics) {
        boolean warning = warningSupplier != null && warningSupplier.getAsBoolean();
        if (warning) {
            innerDraw(guiGraphics, left ? WARNING_LEFT : WARNING_RIGHT);
        } else {
            super.draw(guiGraphics);
        }
    }

    @Override
    public void drawBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(guiGraphics, mouseX, mouseY, partialTicks);
        //Draw the button background
        if (buttonBackground != ButtonBackground.NONE) {
            //Validate the background didn't get set to none by a child
            drawButton(guiGraphics, mouseX, mouseY);
        }
        drawBackgroundOverlay(guiGraphics);
    }

    protected void drawBackgroundOverlay(@NotNull GuiGraphics guiGraphics) {
        guiGraphics.blit(getOverlay(), getButtonX(), getButtonY(), 0, 0, innerWidth, innerHeight, innerWidth, innerHeight);
    }
}