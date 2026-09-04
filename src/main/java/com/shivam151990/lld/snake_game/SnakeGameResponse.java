package com.shivam151990.lld.snake_game;

public class SnakeGameResponse {
    private int len;
    private GameStatus status;

    public SnakeGameResponse(int len, GameStatus status) {
        this.len = len;
        this.status = status;
    }
}
