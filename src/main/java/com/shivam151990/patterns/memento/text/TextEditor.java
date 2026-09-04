package com.shivam151990.patterns.memento.text;

import com.shivam151990.patterns.memento.text.TextArea.Memento;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;

public class TextEditor {

    private Deque<Memento> stk;

    @Getter
    private TextArea textArea;

    public TextEditor() {
        stk = new ArrayDeque<>();
        textArea = new TextArea();
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    public void saveSnapShot() {
        stk.push(textArea.takeSnapshot());
    }

    public void undo() {
        Memento prevState = stk.pop();
        textArea.restore(prevState);
    }

}
