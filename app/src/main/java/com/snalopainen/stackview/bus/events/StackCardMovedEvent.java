package com.snalopainen.stackview.bus.events;

/**
 * Created by snajdan on 2016/12/28.
 */

public class StackCardMovedEvent {
    private final float posX;

    public StackCardMovedEvent(float posX) {
        this.posX = posX;
    }

    public float getPosX() {
        return posX;
    }
}
