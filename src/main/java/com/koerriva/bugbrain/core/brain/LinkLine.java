package com.koerriva.bugbrain.core.brain;

import java.util.Objects;

public class LinkLine {
    public final Integer from;
    public final Integer to;
    public final Integer type;
    public LinkLine(Integer from, Integer to, Integer type) {
        this.from = from;
        this.to = to;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkLine linkLine = (LinkLine) o;
        return Objects.equals(from, linkLine.from) && Objects.equals(to, linkLine.to) && Objects.equals(type, linkLine.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, type);
    }
}
