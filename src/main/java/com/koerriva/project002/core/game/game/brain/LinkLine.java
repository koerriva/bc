package com.koerriva.project002.core.game.game.brain;

import java.util.Objects;

public class LinkLine {
    public final Integer from;
    public final Integer to;
    public LinkLine(Integer from, Integer to) {
        this.from = from;
        this.to = to;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkLine linkLine = (LinkLine) o;
        return Objects.equals(from, linkLine.from) && Objects.equals(to, linkLine.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
