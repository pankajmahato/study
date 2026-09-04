package com.shivam151990.patterns.abstractfactory;

public class MacGuiFactory implements GuiFactory {
    @Override
    public Button getButton() {
        return new MacButton();
    }

    @Override
    public CheckBox getCheckBox() {
        return new MacCheckBox();
    }
}
