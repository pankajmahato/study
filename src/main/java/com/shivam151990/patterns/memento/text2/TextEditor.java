package com.shivam151990.patterns.memento.text2;

import lombok.Getter;
import lombok.Setter;

public class TextEditor {
    @Setter
    @Getter
    private String text;

    public TextEditor(String text) {
        this.text = text;
    }

    public TextEditorMemento save() {
        return new TextEditorMemento(text);
    }

    public void restore(TextEditorMemento memento) {
        this.text = memento.getText();
    }

}
