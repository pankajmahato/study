package com.shivam151990.lld.snake_game;

import java.util.*;

public class SnakeGame {

    class Move {
        int x;
        int y;
        Direction dir;
        public Move(int x, int y, Direction dir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Move move = (Move) o;
            return x == move.x && y == move.y && dir == move.dir;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, dir);
        }
    }

    private Deque<Move> snake;
    private Set<Move> occupied;
    private int curX;
    private int curY;
    private int maxRow;
    private int maxCol;
    private int maxInc;
    private int curInc;

    public SnakeGame(int maxRow, int maxCol, int maxInc) {
        snake = new LinkedList<>();
        occupied = new HashSet<>();
        this.curX = 0;
        this.curY = 0;
        this.maxRow = maxRow;
        this.maxCol = maxCol;
        this.maxInc = maxInc;
        this.curInc = 0;
    }

    private boolean canMove(Move move) {
        if (occupied.contains(move)) {
            return false;
        }
        return true;
    }

    private void moveSnake(Move move) {
        int newX = move.x;
        int newy = move.y;
        Direction curDir = move.dir;

        if (curDir == Direction.RIGHT && newX > maxCol) {
            move.x = 0;
        } else if (curDir == Direction.UP && newX < 0) {
            move.x = maxRow;
        }

        if (curDir == Direction.DOWN && newy > maxRow) {
            move.y = 0;
        } else if (curDir == Direction.LEFT && newy < 0) {
            move.y = maxCol;
        }

        if (curInc + 1 == maxInc) {
            snake.addFirst(move);
            curInc = 0;
            occupied.add(move);
        } else {

        }
        curInc++;
    }

    private SnakeGameResponse move(Direction dir) {
        Move newMove = null;
        if (dir == Direction.UP) {
            newMove = new Move(curX - 1, curY, Direction.UP);
        } else if (dir == Direction.DOWN) {
            newMove = new Move(curX + 1, curY, Direction.DOWN);
        } else if (dir == Direction.LEFT){
            newMove = new Move(curX, curY - 1, Direction.LEFT);
        } else {
            newMove = new Move(curX, curY + 1, Direction.RIGHT);
        }

        if (!canMove(newMove)) {
            return new SnakeGameResponse(-1, GameStatus.FAIL);
        }
        moveSnake(newMove);
        curX = newMove.x;
        curY = newMove.y;
        return new SnakeGameResponse(snake.size(), GameStatus.OK);
    }
}
