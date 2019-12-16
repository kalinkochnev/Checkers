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
        all_pieces.addAll(red.roster);
        all_pieces.addAll(black.roster);
        return all_pieces;
    }

    boolean isPossible(Space end_result) {
        return CheckerBoard.inBounds(end_result, board.length) && this.isEmpty(end_result);
    }

    //assuming that the space is possible
    void movePiece(Piece pc, Space location) throws Exception {
        if(this.isPossible(location)) {
             throw new Exception("The space was already taken, did not check isPossible");
        }

        board[location.row][location.column].setPiece(pc);
    }

    Space[] possibleMoves(Space pc_loc) {
        ArrayList<Space> possible_moves = new ArrayList<>();
        for (Space space : pc_loc.getCorners()) {

            // If the piece on the corner does not match the team of the current piece do...
            if (!isOnSameTeam(space.pc, pc_loc.pc)) {

            }
        }
    }

    Space getLocation(Piece pc) {
        if (pc == null) {
            return null;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Space pc_location = board[row][col];
                if (pc_location.pc != null && pc.equals(pc_location.pc)) {
                    return pc_location;
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

    static boolean inBounds(Space space, int board_length) {
        if (space.column < 0 || space.column > board_length - 1) {
            if (space.row < 0 || space.row > board_length - 1) {
                return false;
            }
        }
        return true;
    }

    Team getPieceTeam(Piece pc) {
        if (Team.onTeam(red, pc)) {
            return red;
        } else {
            return black;
        }
    }

    boolean isOnSameTeam(Piece a, Piece b) {
        return getPieceTeam(a).equals(getPieceTeam(b));
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

    Space(int column, int row) {
        this.column = column;
        this.row = row;
        this.pc = null;
    }

    static Space[] getCorners(Space space) {
        ArrayList<Space> validCorners = new ArrayList<>();
        Space[] posCorners = new Space[]{
                new Space(space.column - 1, space.row - 1),
                new Space(space.column + 1, space.row - 1),
                new Space(space.column - 1, space.row + 1),
                new Space(space.column + 1, space.row + 1)
        };
        for (Space currentSpace : posCorners) {
          if(CheckerBoard.inBounds(currentSpace,8)) {
                validCorners.add(currentSpace);
          }
        }

        Space[] correct_corners = new Space[validCorners.size()];
        for (int i = 0; i < validCorners.size(); i++) {
            correct_corners[i] = validCorners.get(i);
        }
        return correct_corners;
    }

    Space[] getCorners() {
        return getCorners(this);
    }

    void setPiece(Piece pc) {
        this.pc=pc;
    }
}

class Team {
    String color;
    ArrayList<Piece> roster = new ArrayList<>();

    Team(String color) {
        this.color = color;
    }

    Team(String color, ArrayList<Piece> all_pieces) {
        this.color = color;
        this.roster = all_pieces;
    }

    public void initTeam(int start_id, int end_id) {
        for (int id = start_id; id < end_id; id++) {
            roster.add(new Piece(id));
        }
    }

    public Piece[] getPieces(int start, int end) {
        Piece[] pcs = new Piece[end - start];
        for (int i = start; i < end; i++) {
            pcs[i - start] = this.roster.get(i);
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

    static boolean onTeam(Team team, Piece pc) {
        return team.roster.contains(pc);
    }

    public boolean equals(Team other_team) {
        return this.color.equals(other_team.color);
    }
}

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
