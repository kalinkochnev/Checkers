package com.dakl;

import java.util.*;

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
                //If the user decides to skip their turn
                if (game.abstain) {
                    game.abstain = false;
                    game.nextTurn();
                } else {
                    Space piece_space = game.getBoardSpace(parseSpace(user_input[0]));
                    Space end_space = game.getBoardSpace(parseSpace(user_input[2]));

                    Move move_to = new Move(game, piece_space);
                    move_to.movePiece(end_space);
                    game = move_to.getBoard();

                    game.displayBoard();
                    game.nextTurn();
                }

            }
        }
    }

    public static boolean inputIsValid(String[] command, CheckerBoard game) {

        Space unvalidated_pc_loc = parseSpace(command[0]);
        Space unvalidated_end_space = parseSpace(command[2]);

        // Checks if the space is in bounds
        if (!(game.inBounds(unvalidated_pc_loc) && game.inBounds(unvalidated_end_space))) {
            System.out.println("One of the parameters entered is out of bounds, please try again!");
            return false;
        }

        unvalidated_pc_loc = game.getBoardSpace(unvalidated_pc_loc);
        unvalidated_end_space = game.getBoardSpace(unvalidated_end_space);

        //Checks if there is a piece already present in the end space
        if (unvalidated_end_space.pc != null) {
            System.out.println("There is a piece in the given location already, please try again, please try again!");
            return false;
        }

        //Checks that the piece being moved is the same team as the current
        if (!game.getPieceTeam(unvalidated_pc_loc.pc).equals(game.current_turn)) {
            System.out.println("You can't move that silly! You're not on the same team!");
            return false;
        }


        //If the turn is greater than one, restrict the piece to move to the restricted piece move
        if (game.num_turns > 1) {
            if (!unvalidated_pc_loc.pc.equals(game.restricted_piece_move)) {
                System.out.println("You have the opportunity to make a double jump with piece " + game.getLocation(game.restricted_piece_move).toString() +
                        ". You may choose to make another jump or skip your turn." + "Would you like to skip? Y/N");
                Scanner input = new Scanner(System.in);
                String user_input = input.nextLine();
                if (user_input.toLowerCase().equals("Y")) {
                    game.abstain = true;
                } else {
                    return false;
                }
            }
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
    int num_turns = 1;
    boolean abstain = false;
    Piece restricted_piece_move;

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

    Space[] getCorners(Space space) {
        ArrayList<Space> validCorners = new ArrayList<>();
        Space[] posCorners = new Space[]{
                new Space(space.column - 1, space.row - 1),
                new Space(space.column + 1, space.row + 1),
                new Space(space.column + 1, space.row - 1),
                new Space(space.column - 1, space.row + 1),

        };
        for (Space currentSpace : posCorners) {
            if (inBounds(currentSpace)) {
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
        Space[] posCorners = new Space[]{
            new Space(space.column - 1, space.row - 1),
            new Space(space.column + 1, space.row + 1),
            new Space(space.column + 1, space.row - 1),
            new Space(space.column - 1, space.row + 1),
        };

        return new Space[][]{{posCorners[0], posCorners[1]}, {posCorners[2], posCorners[3]}};
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
    boolean isOver() {
        return red.roster.size() == 0 || black.roster.size() == 0;
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
    ArrayList<Piece> getAll() {
        ArrayList<Piece> all_pieces = new ArrayList<>();
        all_pieces.addAll(red.roster);
        all_pieces.addAll(black.roster);
        return all_pieces;
    }
    Space getBoardSpace(Space space) {
        return board[space.row][space.column];
    }


    boolean inBounds(Space space) {
        if (space.column >= 0 && space.column <= board.length - 1) {
            if (space.row >= 0 && space.row <= board.length - 1) {
                return true;
            }
        }
        return false;
    }
    boolean inBounds(Space[] spaces) {
        for (Space space : spaces) {
            if (!inBounds(space)) {
                return false;
            }
        }
        return true;
    }


    boolean isEmpty(Space space) {
        return space.pc == null;
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
    void nextTurn() {
        Team[] teams = {red, black};

        if (num_turns > 1) {
            num_turns--;
        } else {
            if (teams[0].equals(current_turn)) {
                current_turn = teams[1];
            } else if (teams[1].equals(current_turn)) {
                current_turn = teams[0];
            }
        }


    }

    public static Space[] ArrayListToArray(ArrayList<Space> list) {
        Space[] arr = new Space[list.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i);
        }
        return arr;
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

    //Gets corner sets but may not be in bounds


    void setPiece(Piece pc) {
        this.pc = pc;
    }

    public boolean equals(Space other) {
        return this.column == other.column && this.row == other.row;
    }
    public String toString() {
        return "R" + (this.row+1) + " C" + (this.column+1);
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
    static boolean onTeam(Team team, Piece pc) {
        return team.roster.contains(pc);
    }

    public void killPiece(Piece killed_pc) {
        roster.remove(killed_pc);
        killed.add(killed_pc);
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

class Move {
    CheckerBoard board;
    Space start_location;
    ArrayList<Space> possible_slides = new ArrayList<>();
    Map<Space, Space> possible_jumps = new HashMap<Space, Space>(); //<jumped space, end location>

    Move(CheckerBoard board, Space start_location) {
        this.board = board;
        this.start_location = board.getBoardSpace(start_location);
        possibleSlides();
        possibleJumps();
    }

    CheckerBoard getBoard() {
        return board;
    }

    boolean isPossible(Space end_result) {
        return possible_slides.contains(end_result) || possible_jumps.containsValue(end_result);
    }

    boolean arePossibleMoves() {
        //If the piece is restricted
        return possible_jumps.size() > 1 || possible_slides.size() > 1;

    }

    Team getStartLocTeam() {
        return board.getPieceTeam(start_location.pc);
    }

    void possibleSlides() {
        ArrayList<Space> unchecked_moves = new ArrayList<>();

        for (Space possible_space : board.getCorners(start_location)) {
            //if the space is empty it adds it to possible moves
            Space board_space = board.getBoardSpace(possible_space);
            if (board.isEmpty(possible_space)) {
                unchecked_moves.add(board_space);
            }
        }

        //Checks that the pieces are moving in the correct direction
        Team piece_team = board.getPieceTeam(start_location.pc);
        possible_slides = restrictMovement(CheckerBoard.ArrayListToArray(unchecked_moves));

    }

    void possibleJumps() {
        //For a given jump end location there is a space that it was jumped over and is stored here <end_loc, jumped_pc>
        Map<Space, Space> jump_map = new HashMap<Space, Space>();

        //Checks all corners that are not empty and and have a piece of the opposite team
        for (Space piece_to_jump : board.getCorners(start_location)) {
            Space space_jumped_on_board = board.getBoardSpace(piece_to_jump);

            if (!board.isEmpty(space_jumped_on_board) && !board.isOnSameTeam(start_location.pc, space_jumped_on_board.pc)) {
                //Get the corner sets, find the set that contains the start location
                Space[][] opp_corners = board.getCornerSet(space_jumped_on_board);
                for(Space[] corner_set : opp_corners) {
                    //Check that all corners are in bounds
                    if (board.inBounds(corner_set)) {
                        //If the set contains the start location
                        for (int corner = 0; corner < 2; corner++) {
                            Space corner_on_board = board.getBoardSpace(corner_set[corner]);
                            if (corner_on_board.equals(start_location)) {

                                Space opp_corner;
                                if (corner == 0) {
                                    opp_corner = board.getBoardSpace(corner_set[1]);
                                } else {
                                    opp_corner = board.getBoardSpace(corner_set[0]);
                                }

                                //Check the opposite corner is empty
                                if (board.isEmpty(opp_corner)) {
                                    jump_map.put(opp_corner, space_jumped_on_board);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //assuming that the space is possible
    void movePiece(Space end_loc) {
        //Sets the initial loc piece to empty, sets the end location to the new piece
        board.board[start_location.row][start_location.column].pc = null;
        board.board[end_loc.row][end_loc.column].setPiece(start_location.pc);

        //If a piece moves to the opposite side of the board make it a king
        Team piece_team = getStartLocTeam();
        if (piece_team.direction.equals("ascending") && end_loc.row == board.board.length - 1) {
            board.board[end_loc.row][end_loc.column].pc.isKing = true;
        } else if (piece_team.direction.equals("descending") && end_loc.row == 0) {
            board.board[end_loc.row][end_loc.column].pc.isKing = true;
        }

        Space end_loc_on_board = board.getBoardSpace(end_loc);

        //If the jump possibilities are greater than 0
        if (possible_jumps.containsKey(end_loc_on_board)) {

            //Gets the space that was jumped
            Space jumped_space = null;
            for (Map.Entry<Space, Space> jump_set : possible_jumps.entrySet()) {
                if (jump_set.getValue().equals(end_loc)) {
                    jumped_space = jump_set.getKey();
                }
            }

            //Add the killed piece to the killed arraylist and remove from roster
            Team jumped_pc_team = board.getPieceTeam(jumped_space.pc);
            jumped_pc_team.killPiece(jumped_space.pc);

            //Set the jumped space piece to null
            board.board[jumped_space.row][jumped_space.column].pc = null;

            //If another move is possible add another turn but restrict the possible piece to move to be the same one
            Move next = new Move(board, end_loc);
            if (next.arePossibleMoves()) {
                board.num_turns++;
                board.restricted_piece_move = start_location.pc;
            }

        }

    }


    //Make sure to pass the board space that has the piece attribute contained in it
    public ArrayList<Space> restrictMovement(Space[] current_possibilities) {
        ArrayList<Space> possible_moves = new ArrayList<>();

        for (int space = 0; space < current_possibilities.length; space++) {
            Space move = restrictMove(start_location.pc, current_possibilities[space]);
            if (move != null) {
                possible_moves.add(move);
            }
        }
        return possible_moves;
    }
    public Space restrictMove(Piece piece, Space end_move) {
        String direction = board.getPieceTeam(piece).direction;
        Space piece_loc = board.getLocation(piece);

        //If the piece is a king there are no restrictions
        if (piece.isKing) {
            return end_move;
        }

        if (direction.equals("ascending")) {
            //If the possible move row is greater than than the piece row it is a valid move
            if (end_move.row > piece_loc.row) {
                return end_move;
            }
        } else if (direction.equals("descending")) {
            //If the move row is less than the piece row it is a valid move for pieces going up the board
            if (end_move.row < piece_loc.row) {
                return end_move;
            }
        }

        return null;
    }

}