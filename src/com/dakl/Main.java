package com.dakl;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        //Add pieces moving diagonal
        //Jump opponents
        //Do double jumps
        //Make kings that can move in any direction
        CheckerBoard game = CheckerBoard.freshBoard();
    }

}

class CheckerBoard {
    Space[][] board;
    Team red;
    Team black;

    CheckerBoard(Space[][] board, Team red, Team black) {
        this.board = board;
        this.red = red;
        this.black = black;
    }

    static CheckerBoard freshBoard() {
        Space[][] board = new Space[8][8];
        Team red = new Team("red");
        Team black = new Team("black");

        // Initalizes the piece variables in the checkerboard class
        red.initTeam(0, 12);
        black.initTeam(12, 24);

        //Sets the first and last the rows to generated ones
        Space[][] first_three = Team.genSide(red);
        Space[][] last_three = Team.genSide(black);
        for (int row = 0; row < 3; row++) {
            board[row] = first_three[row];
            board[row + 5] = last_three[row];
        }

        return new CheckerBoard(board, red, black);
    }

    ArrayList<Piece> getAll() {
        ArrayList<Piece> all_pieces = new ArrayList<>();
        all_pieces.addAll(red.all_pieces);
        all_pieces.addAll(black.all_pieces);
        return all_pieces;
    }

    Space getLocation(Piece pc) {
        if (pc == null) {
            return null;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Space cur_space = board[row][col];
                if (cur_space.pc != null && pc.equals(cur_space.pc)) {
                    return cur_space;
                }
            }
        }
        return null;
    }

    Space getLocation(int piece_id) {
        Piece found = null;
        for (Piece pc : getAll()) {
            if (pc.id == piece_id) {
                found = pc;
            }
        }
        return getLocation(found);
    }

    boolean isEmpty(Space space) {
        return space.pc == null;
    }

}

class Space {
    int column;
    int row;
    public Piece pc;

    Space(int column, int row, Piece piece) {
        this.column = column;
        this.row = row;
        this.pc = piece;
    }

}

class Team {
    String color;
    ArrayList<Piece> all_pieces = new ArrayList<>();

    Team(String color) {
        this.color = color;
    }

    Team(String color, ArrayList<Piece> all_pieces) {
        this.color = color;
        this.all_pieces = all_pieces;
    }

    public void initTeam(int start_id, int end_id) {
        for (int id = start_id; id < end_id; id++) {
            all_pieces.add(new Piece(id));
        }
    }

    public Piece[] getPieces(int start, int end) {
        Piece[] pcs = new Piece[end - start];
        for (int i = start; i < end; i++) {
            pcs[i - start] = this.all_pieces.get(i);
        }
        return pcs;
    }

    public static Space[][] genSide(Team team) {
        Space[][] three_rows = new Space[3][8];

        for (int row = 0; row < 3; row++) {
            Piece[] four_pcs = team.getPieces(4 * row, 4 * row + 4);
            Space[] new_row = new Space[8];
            int piece_count = 0;

            // Accounts for the piece offsets for each starting row
            int space_offset = 0;
            if (row % 2 == 0) {
                space_offset = 1;
            }

            for (int col = space_offset; col < new_row.length; col += 2) {
                new_row[col] = new Space(col, row, four_pcs[piece_count]);
                piece_count++;
            }

            three_rows[row] = new_row;
        }

        return three_rows;
    }
}
// Get all the possible corners that the piece can move
// Don't get corners that have a piece or are out of bounds
//Display the options to the user to select
// Include 4 max possible corners if king
// 2 max possible if not king
// Depending on the team the piece can only move in a direction

class Piece {
    public int id;
    boolean isKing = false;
    boolean killed = false;

    Piece(int id) {
        this.id = id;
    }

    public boolean equals(Piece other_pc) {
        return this.id == other_pc.id;
    }
}
