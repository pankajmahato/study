package com.shivam151990.lld.text_wrapper;

import lombok.Getter;

public class TextWrapperRules {

    private TextWrapperRules(Builder builder) {
        this.wordWrapWidth = builder.wordWrapWidth;
        this.hyphenation = builder.hyphenation;
        this.indentation = builder.indentation;
    }

    public enum Indentation {
        SPACE, TABS
    }

    @Getter
    private Integer wordWrapWidth;

    @Getter
    private Indentation indentation;

    @Getter
    private boolean hyphenation;

    public class Builder {
        private Integer wordWrapWidth;
        private Indentation indentation;
        private boolean hyphenation;

        public Builder setWordWrapWidth(Integer wordWrapWidth) {
            this.wordWrapWidth = wordWrapWidth;
            return this;
        }

        public Builder setIndentation(Indentation indentation) {
            this.indentation = indentation;
            return this;
        }

        public Builder setHyphenation(boolean hyphenation) {
            this.hyphenation = hyphenation;
            return this;
        }

        public TextWrapperRules build() {
            return new TextWrapperRules(this);
        }
    }
}
