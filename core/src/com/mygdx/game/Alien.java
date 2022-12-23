package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class Alien {
    int x;
    int y;
    private int width;
    private int height;
    private Texture texture;

    public Alien(int x, int y, int width, int height, Texture texture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
