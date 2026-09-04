package com.shivam151990.patterns.memento.text2;

import lombok.Getter;

@Getter
public class TextEditorMemento {
    private final String text;

    public TextEditorMemento(String text) {
        this.text = text;
    }
}
