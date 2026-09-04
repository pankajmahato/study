package com.shivam151990.patterns.memento.text;

public class Runner {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor();

        TextArea textArea = editor.getTextArea();
        textArea.setText("Text first time");

        TextArea.Memento memento = textArea.takeSnapshot();
    }
}
