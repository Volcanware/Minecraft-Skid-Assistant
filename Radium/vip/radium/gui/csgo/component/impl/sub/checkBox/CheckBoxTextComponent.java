// 
// Decompiled by Procyon v0.5.36
// 

package vip.radium.gui.csgo.component.impl.sub.checkBox;

import java.util.function.Consumer;
import java.util.function.Supplier;
import vip.radium.gui.csgo.component.impl.sub.text.TextComponent;
import vip.radium.gui.csgo.component.PredicateComponent;
import vip.radium.gui.csgo.component.Component;

public final class CheckBoxTextComponent extends Component implements PredicateComponent
{
    private final CheckBoxComponent checkBox;
    private final TextComponent textComponent;
    private static final int CHECK_BOX_SIZE = 5;
    private static final int TEXT_OFFSET = 3;
    
    public CheckBoxTextComponent(final Component parent, final String text, final Supplier<Boolean> isChecked, final Consumer<Boolean> onChecked, final Supplier<Boolean> isVisible, final float x, final float y) {
        super(parent, x, y, 0.0f, 5.0f);
        this.checkBox = new CheckBoxComponent(this, 0.0f, 0.0f, 5.0f, 5.0f) {
            @Override
            public boolean isChecked() {
                return isChecked.get();
            }
            
            @Override
            public void setChecked(final boolean checked) {
                onChecked.accept(checked);
            }
            
            @Override
            public boolean isVisible() {
                return isVisible.get();
            }
        };
        this.textComponent = new TextComponent(this, text, 8.0f, 1.0f);
        this.addChild(this.checkBox);
        this.addChild(this.textComponent);
    }
    
    public CheckBoxTextComponent(final Component parent, final String text, final Supplier<Boolean> isChecked, final Consumer<Boolean> onChecked, final Supplier<Boolean> isVisible) {
        this(parent, text, isChecked, onChecked, isVisible, 0.0f, 0.0f);
    }
    
    public CheckBoxTextComponent(final Component parent, final String text, final Supplier<Boolean> isChecked, final Consumer<Boolean> onChecked) {
        this(parent, text, isChecked, onChecked, () -> true);
    }
    
    @Override
    public float getWidth() {
        return 8.0f + this.textComponent.getWidth();
    }
    
    @Override
    public boolean isVisible() {
        return this.checkBox.isVisible();
    }
}
