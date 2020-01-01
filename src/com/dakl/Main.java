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

        CheckerBoard game = CheckerBoard.blankBoard();

        // REVIEW : Setup method
        Space start_loc = game.getBoardSpace(Main.parseSpace("C4R5"));
        Team r = game.red;
        // REVIEW : calling static methods
        Main.threeByThree(game.black, new Team[] {r, r, r, r}, start_loc, true, game);
        // REVIEW : using public members
        game.getBoardSpace(("C5R2")).pc = game.red.roster.get(9);

        game.displayBoard();

        while (!game.isOver()) {
            System.out.println(game.current_turn.color + "'s are up!");
            System.out.println("Where would you like to move?");

            String user_input = input.nextLine();
            //If the input is valid the piece is moved
            if (inputIsValid(game, user_input)) {
                //If the user decides to skip their turn
                if (game.abstain) {
                    game.abstain = false;
                    game.nextTurn();
                    continue;
                } else {
                    Space piece_space = game.validated_pc_space;
                    Space end_space = game.validated_end_space;

                    Move move_to = new Move(game, piece_space);
                    move_to.movePieceTo(end_space);
                    game = move_to.getBoard();

                    game.displayBoard();
                    if (game.num_turns != 1) {
                        game.nextTurn();
                    }
                }

            }
        }

        String output = "Congratulations!!!";
        if (game.red.roster.size() == 0) {
            output += "The X team won!";
        } else {
            output += "The O team won!";
        }
        System.out.println(output);
    }

    // REVIEW : javadocs to explain "why" 
    static void threeByThree(Team center_team, Team[] surrounding, Space center, boolean isKing, CheckerBoard b) {
        Piece center_piece = center_team.roster.get(11);
        if (isKing) {
            center_piece.isKing = true;
        }
        b.getBoardSpace(center).pc = center_piece;

        Space[] corners = b.getCorners(center);

        int last_x_id = 0;
        int last_o_id = 0;
        for (int corner = 0; corner < 4; corner++) {
            if (surrounding[corner] == null) {
                b.getBoardSpace(corners[corner]).pc = null;
                continue;
            }

            if (surrounding[corner].color.equals("X")) {
                b.getBoardSpace(corners[corner]).pc = surrounding[corner].roster.get(last_x_id);
                last_x_id++;
            } else if (surrounding[corner].color.equals("O")){
                b.getBoardSpace(corners[corner]).pc =surrounding[corner].roster.get(last_o_id);
                last_o_id++;
            }
        }
    }

    public static boolean inputIsValid(CheckerBoard game, String input) {
        String[] command = input.split(" ");

        // REVIEW : Variable java naming convention
        Space unvalidated_pc_loc = null;
        Space unvalidated_end_space = null;

        try {
            unvalidated_pc_loc = parseSpace(command[0]);
            unvalidated_end_space = parseSpace(command[2]);
        } catch (Exception a) {
            // REVIEW : Catching specific exceptions
            System.out.println(a.getMessage() + ". The input you entered is not parsable, please try again!");
            return false;
        }

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

        //Generated test move to be used for validation
        Move test_move = new Move(game, unvalidated_pc_loc);


        //If the turn is greater than one, restrict the piece to move to the restricted piece move
        if (game.restricted_piece_move != null) {
            if (!unvalidated_pc_loc.pc.equals(game.restricted_piece_move)) {
                System.out.println("You have the opportunity to make a double jump with piece" + game.getLocation(game.restricted_piece_move).toString() +
                        "!\n You may choose to make another jump or skip your turn." + "Would you like to skip? Y/N");
                try {
                    Scanner scanner = new Scanner(System.in);
                    String user_input = scanner.next();
                    if (user_input.toLowerCase().equals("y")) {
                        game.abstain = true;
                        game.restricted_piece_move = null;
                    } else {
                        return false;
                    }
                } catch (InputMismatchException a) {
                    System.out.println("There was something wrong with your input, please try again!");
                    return false;
                }
            } else {
                if (!test_move.possible_jumps.containsValue(unvalidated_end_space)) {
                    System.out.println("You can't jump there, you must double jump.");
                    return false;
                }
            }
        }

        //Checks if the end location is a possible move
        if (!test_move.isPossible(unvalidated_end_space)) {
            System.out.println("The space you want to move to is not valid! Please try again!");
            return false;
        }

        game.validated_end_space = unvalidated_end_space;
        game.validated_pc_space = unvalidated_pc_loc;

        //If it passes all the criteria
        return true;
    }

    public static Space parseSpace(String piece_str) {
        int r_index = piece_str.indexOf("R");
        int c_index = piece_str.indexOf("C");

        int space_col;
        int space_row;

        String first = "";
        if (c_index < r_index) {
            space_row = Integer.parseInt(piece_str.substring(r_index + 1)) - 1;
            space_col = Integer.parseInt(piece_str.substring(c_index + 1, r_index)) - 1;
        } else {
            space_row = Integer.parseInt(piece_str.substring(r_index + 1, c_index)) - 1;
            space_col = Integer.parseInt(piece_str.substring(c_index + 1)) - 1;
        }

        return new Space(space_col, space_row);
    }

    public static Space[] parseSpace(String[] piece2move) {
        Space[] spaces = new Space[piece2move.length];
        for (int space = 0; space < piece2move.length; space++) {
            spaces[space] = parseSpace(piece2move[space]);
        }
        return spaces;
    }
}

// REVIEW : separate files for classes
class CheckerBoard {
    // REVIEW: member variables
    Space[][] board;
    Team red;
    Team black;

    Space validated_pc_space;
    Space validated_end_space;

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

    // REVIEW : good usage of static methods
    static CheckerBoard blankBoard() {
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

    // REVIEW : method naming
    ArrayList<Piece> getAll() {
        ArrayList<Piece> all_pieces = new ArrayList<>();
        all_pieces.addAll(red.roster);
        all_pieces.addAll(black.roster);
        return all_pieces;
    }

    Space getBoardSpace(Space space) {
        return board[space.row][space.column];
    }

    Space getBoardSpace(String space) {
        Space space_obj = Main.parseSpace(space);
        return board[space_obj.row][space_obj.column];

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
        if (teams[0].equals(current_turn)) {
            current_turn = teams[1];
        } else if (teams[1].equals(current_turn)) {
            current_turn = teams[0];
        }
        num_turns = 1;
    }

    // REVIEW : method naming convention
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
        return "R" + (this.row + 1) + " C" + (this.column + 1);
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

    public String toString() {
        if (color.equals("X")) {
            return "Team Black";
        } else {
            return "Team Red";
        }
    }
}

class Piece {
    public int id;
    boolean isKing = false;

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
        return possible_jumps.size() >= 1 || possible_slides.size() >= 1;

    }

    int numPossibleMoves() {
        return possible_jumps.size() + possible_slides.size();
    }


    Team getStartLocTeam() {
        return board.getPieceTeam(start_location.pc);
    }

    void possibleSlides() {
        ArrayList<Space> unchecked_moves = new ArrayList<>();

        for (Space possible_space : board.getCorners(start_location)) {
            //if the space is empty it adds it to possible moves
            Space board_space = board.getBoardSpace(possible_space);
            if (board.isEmpty(board_space)) {
                unchecked_moves.add(board_space);
            }
        }

        //Checks that the pieces are moving in the correct direction
        Team piece_team = board.getPieceTeam(start_location.pc);
        possible_slides = restrictMovement(CheckerBoard.ArrayListToArray(unchecked_moves));

    }

    void possibleJumps() {
        //For a given jump end location there is a space that it was jumped over and is stored here <end_loc, jumped_pc>
        //Checks all corners that are not empty and and have a piece of the opposite team
        for (Space piece_to_jump : board.getCorners(start_location)) {
            Space space_jumped_on_board = board.getBoardSpace(piece_to_jump);

            if (!board.isEmpty(space_jumped_on_board) && !board.isOnSameTeam(start_location.pc, space_jumped_on_board.pc)) {
                //Get the corner sets, find the set that contains the start location
                Space[][] opp_corners = board.getCornerSet(space_jumped_on_board);
                for (Space[] corner_set : opp_corners) {
                    //Check that all corners are in bounds
                    if (board.inBounds(corner_set)) {
                        //If the set contains the start location
                        for (int corner = 0; corner < 2; corner++) {
                            Space corner_on_board = board.getBoardSpace(corner_set[corner]);
                            if (corner_on_board.equals(start_location)) {

                                Space final_jump_loc;
                                if (corner == 0) {
                                    final_jump_loc = board.getBoardSpace(corner_set[1]);
                                } else {
                                    final_jump_loc = board.getBoardSpace(corner_set[0]);
                                }

                                //Check the opposite corner is empty
                                if (board.isEmpty(final_jump_loc)) {
                                    //Restrict the move to the proper direction
                                    Piece to_move = start_location.pc;
                                    if (restrictMove(to_move, final_jump_loc) != null) {
                                        possible_jumps.put(space_jumped_on_board, final_jump_loc);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //assuming that the space is possible
    void movePieceTo(Space end_loc) {
        board.num_turns--;
        Space end_loc_on_board = board.getBoardSpace(end_loc);

        //Sets the initial loc piece to empty, sets the end location to the new piece
        board.board[end_loc.row][end_loc.column].setPiece(start_location.pc);

        //If the jump possibilities are greater than 0
        if (possible_jumps.containsValue(end_loc_on_board)) {

            //Gets the space that was jumped
            Space jumped_space = null;
            for (Map.Entry<Space, Space> jump_set : possible_jumps.entrySet()) {
                if (jump_set.getValue().equals(end_loc)) {
                    jumped_space = jump_set.getKey();
                    break;
                }
            }

            //Add the killed piece to the killed arraylist and remove from roster
            Team jumped_pc_team = board.getPieceTeam(jumped_space.pc);
            jumped_pc_team.killPiece(jumped_space.pc);

            //Set the jumped space piece to null
            board.board[jumped_space.row][jumped_space.column].pc = null;

            //If another move is possible add another turn but restrict the possible piece to move to be the same one
            Move next = new Move(board, board.getBoardSpace(end_loc));

            if (next.possible_jumps.size() >= 1) {
                board.num_turns+=1;
                board.restricted_piece_move = start_location.pc;
            } else {
                board.restricted_piece_move = null;
            }

        }


        //If a piece moves to the opposite side of the board make it a king
        Team piece_team = board.getPieceTeam(end_loc_on_board.pc);
        if (piece_team.direction.equals("ascending") && end_loc.row == board.board.length - 1) {
            board.board[end_loc.row][end_loc.column].pc.isKing = true;
        } else if (piece_team.direction.equals("descending") && end_loc.row == 0) {
            board.board[end_loc.row][end_loc.column].pc.isKing = true;
        }

        //Sets the original spot to null
        board.board[start_location.row][start_location.column].pc = null;

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