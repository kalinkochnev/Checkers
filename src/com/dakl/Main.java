package com.dakl;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("How to play: \n" +
                "- To refer to a space or piece use R[row number]C[column number] or vice versa, C[column number]R[row number]\n" +
                "   ex: R1C2 or C2R1\n" +
                "- To move a piece the arguments are \"   (Location of piece to move) to  (End location of piece)   \"\n" +
                "   ex: R1C2 to R3C2\n" +
                "- Kings have a K attached to their piece symbol\n");

        CheckerBoard game = CheckerBoard.freshBoard();
        game.displayBoard();



        while (!game.isOver()) {
            System.out.println(game.current_turn.color + "'s are up!");
            System.out.println("Where would you like to move?");
            String[] user_input = input.nextLine().split(" ");
            //If the input is valid the piece is moved
            if (inputIsValid(user_input, game)) {

                try {
                    Space piece_space = parseSpace(user_input[0]);
                    Space end_space = parseSpace(user_input[2]);

                    game.movePiece(game.getBoardSpace(piece_space).pc, game.getBoardSpace(end_space));
                    game.displayBoard();
                    game.nextTurn();
                } catch (Exception e) {
                    System.out.println("The piece movement is not possible!!! Fatal error!");
                }
            } else {
                continue;
            }

        }
    }

    public static boolean inputIsValid(String[] command, CheckerBoard game) {

        Space unvalidated_pc_loc = parseSpace(command[0]);
        Space unvalidated_end_space = parseSpace(command[2]);

        Space valid_pc_loc;
        Space valid_end_space;

        // Checks if the space is in bounds
        try {
            valid_pc_loc = game.getBoardSpace(unvalidated_pc_loc);
            valid_end_space = game.getBoardSpace(unvalidated_end_space);
        } catch (Exception e) {
            System.out.println(e.getMessage() + ". Please try again...");
            return false;
        }

        //Checks if there is a piece already present in the end space
        if (valid_end_space.pc != null) {
            System.out.println("There was a piece already in the end location, please try again!");
            return false;
        }

        //Checks if there is a piece already present in the end space
        if (valid_pc_loc.pc == null) {
            System.out.println("There is no piece in the given piece location, please try again!");
            return false;
        }

        //Checks if the end space is a possible move
        boolean end_space_possible = false;
        for (Space space : game.possibleMoves(valid_pc_loc)) {
            if (space.equals(valid_end_space)) {
                end_space_possible = true;
                break;
            }
        }

        if (!end_space_possible) {
            System.out.println("The end space you picked is not a possible move, please try again!");
            return false;
        }

        //Checks that the piece being moved is the same team as the current
        if (!game.getPieceTeam(valid_pc_loc.pc).equals(game.current_turn)) {
            System.out.println("You can't move that silly! You're not on the same team!");
            return false;
        }

        //If it passes all the criteria
        return true;
    }

    public static Space parseSpace(String piece2move) {
        int r_index = piece2move.indexOf("R");
        int c_index = piece2move.indexOf("C");
        int space_row = Character.getNumericValue(piece2move.charAt(r_index + 1)) - 1;
        int space_col = Character.getNumericValue(piece2move.charAt(c_index + 1)) - 1;

        return new Space(space_col, space_row);
    }
}

class CheckerBoard {
    Space[][] board;
    Team red;
    Team black;
    Team current_turn;

    CheckerBoard(Space[][] board, Team red, Team black) {
        this.board = board;
        this.red = red;
        this.black = black;
        this.current_turn = red;
    }

    static CheckerBoard freshBoard() {
        Space[][] board = new Space[8][8];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                board[row][col] = new Space(col, row);
            }
        }


        Team red = new Team("O", "ascending");
        Team black = new Team("X", "descending");

        // Initalizes the piece variables in the checkerboard class
        red.initTeam(0, 12);
        black.initTeam(12, 24);

        //Sets the first and last the rows to generated ones
        Space[][] board_stage1 = Team.genSide(red, board, 0, 3);
        Space[][] new_board = Team.genSide(black, board_stage1, 5, 8);

        return new CheckerBoard(board, red, black);
    }

    public static Space[] ArrayListToArray(ArrayList<Space> list) {
        Space[] arr = new Space[list.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    void displayBoard() {
        for (int row = 0; row < board.length; row++) {
            String row_str = "R" + String.valueOf(row + 1) + " ";

            for (int space = 0; space < board.length; space++) {
                Piece curr_piece = board[row][space].pc;

                if (curr_piece == null) {
                    row_str += "|___";
                } else {
                    row_str += "|_" + getPieceTeam(curr_piece).color;
                    if (curr_piece.isKing) {
                        row_str += "K";
                    } else {
                        row_str += "_";
                    }
                }

                if (space == board.length - 1) {
                    row_str += "|";
                }

            }
            System.out.println(row_str);

            if (row == board.length - 1) {
                System.out.println("    C1  C2  C3  C4  C5  C6  C7  C8");
            }
        }
    }

    void nextTurn() {
        Team[] teams = {red, black};

        if (teams[0].equals(current_turn)) {
            current_turn = teams[1];
        } else if (teams[1].equals(current_turn)) {
            current_turn = teams[0];
        }
    }

    ArrayList<Piece> getAll() {
        ArrayList<Piece> all_pieces = new ArrayList<>();
        all_pieces.addAll(red.roster);
        all_pieces.addAll(black.roster);
        return all_pieces;
    }

    boolean isPossible(Piece curr, Space end_result) {
        boolean inBounds = CheckerBoard.inBounds(end_result, board.length);
        boolean spaceEmpty = this.isEmpty(end_result);
        boolean notForwardMove = false;

        Space pc_loc = getLocation(curr);
        try {
            for (Space possible : possibleMoves(pc_loc)) {
                if (end_result.equals(possible)) {
                    notForwardMove = true;
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("A move was not possible error!");
        }

        return inBounds && spaceEmpty && notForwardMove;
    }

    Space getBoardSpace(Space space) throws Exception {
        if (!inBounds(space, 8)) {
            throw new Exception("A space given is out of bounds");
        }

        return board[space.row][space.column];
    }

    //assuming that the space is possible
    void movePiece(Piece pc, Space end_loc) throws Exception {

        Space pc_loc = this.getLocation(pc);
        board[pc_loc.row][pc_loc.column].pc = null;
        board[end_loc.row][end_loc.column].setPiece(pc);

        //Checks whether or not the move made the piece move to the last row
        Team pc_team = getPieceTeam(pc);
        if (pc_team.direction.equals("ascending") && end_loc.row == board.length-1) {
            board[end_loc.row][end_loc.column].pc.isKing = true;
        } else if (pc_team.direction.equals("descending") && end_loc.row == 0) {
            board[end_loc.row][end_loc.column].pc.isKing = true;
        }

    }

    Space[] possibleMoves(Space pc_loc) {
        ArrayList<Space> possible_moves = new ArrayList<>();

        //Checks that the piece being moved has the same team as the current player


        //Checks that the corners are empty
        for (Space poss_space : pc_loc.getCorners(pc_loc)) {
            //if the space is empty it adds it to possible moves
            try {
                Space board_space = getBoardSpace(poss_space);
                if (isEmpty(getBoardSpace(board_space))) {
                    possible_moves.add(poss_space);
                }
            } catch (Exception e) {
                System.out.println("Fatal error, space not in board");
            }
        }

        //Checks that the pieces are moving in the correct direction
        try {
            Space board_space = getBoardSpace(pc_loc);
            Team piece_team = getPieceTeam(board_space.pc);
            Space[] restricted_moves = Team.restrictMovement(board_space, CheckerBoard.ArrayListToArray(possible_moves), piece_team);
            return restricted_moves;
        } catch (Exception e) {
            System.out.println("Space does not exist on board!!!");
            return new Space[0];
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
                    pc_location.row = row;
                    pc_location.column = col;
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
        if (space.column >= 0 && space.column <= board_length - 1) {
            if (space.row >= 0 && space.row <= board_length - 1) {
                return true;
            }
        }
        return false;
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

    boolean isOver() {
        return red.roster.size() == 0 || black.roster.size() == 0;
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
                new Space(space.column + 1, space.row + 1),
                new Space(space.column + 1, space.row - 1),
                new Space(space.column - 1, space.row + 1),

        };
        for (Space currentSpace : posCorners) {
            if (CheckerBoard.inBounds(currentSpace, 8)) {
                validCorners.add(currentSpace);
            }
        }

        Space[] correct_corners = new Space[validCorners.size()];
        for (int i = 0; i < validCorners.size(); i++) {
            correct_corners[i] = validCorners.get(i);
        }
        return correct_corners;
    }

    Space[][] getCornerSet(Space space) {
        Space[] corners = getCorners(space);
        return new Space[][]{{corners[0], corners[1]}, {corners[2], corners[3]}};
    }

    Space[] getCorners() {
        return getCorners(this);
    }

    void setPiece(Piece pc) {
        this.pc = pc;
    }

    public boolean equals(Space other) {
        return this.column == other.column && this.row == other.row;
    }
}

class Team {
    String color;
    ArrayList<Piece> roster = new ArrayList<>();
    ArrayList<Piece> killed = new ArrayList<>();
    String direction;

    Team(String color, String direction) {
        this.color = color;
        this.direction = direction;
    }

    Team(String color, ArrayList<Piece> all_pieces, String direction) {
        this.color = color;
        this.roster = all_pieces;
        this.direction = direction;
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

    public static Space[][] genSide(Team team, Space[][] board, int row_start, int row_end) {
        int modified_row_count = 0;

        for (int row = row_start; row < row_end; row++) {
            Piece[] four_pcs = team.getPieces(4 * modified_row_count, 4 * modified_row_count + 4);
            Space[] new_row = board[row];
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

            board[row] = new_row;
            modified_row_count++;
        }

        return board;
    }

    static boolean onTeam(Team team, Piece pc) {
        return team.roster.contains(pc);
    }

    public boolean equals(Team other_team) {
        return this.color.equals(other_team.color);
    }

    //Make sure to pass the board space that has the piece attribute contained in it
    public static Space[] restrictMovement(Space cur_loc, Space[] current_possibilities, Team piece_team) {
        ArrayList<Space> possible_moves = new ArrayList<>();
        String dir = piece_team.direction;

        //If the piece is a king there are no restrictions
        if (cur_loc.pc.isKing) {
            return current_possibilities;
        }

        for (int space = 0; space < current_possibilities.length; space++) {
            if (dir.equals("ascending")) {
                //If the possible move row is greater than than the piece row it is a valid move
                if (current_possibilities[space].row > cur_loc.row) {
                    possible_moves.add(current_possibilities[space]);
                }
            } else if (dir.equals("descending")) {
                //If the move row is less than the piece row it is a valid move for pieces going up the board
                if (current_possibilities[space].row < cur_loc.row) {
                    possible_moves.add(current_possibilities[space]);
                }
            }
        }
        return CheckerBoard.ArrayListToArray(possible_moves);
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
