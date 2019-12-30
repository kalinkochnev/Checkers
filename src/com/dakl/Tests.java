package com.dakl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {
    CheckerBoard b = CheckerBoard.blankBoard();

    @AfterEach
    void resetBoard() {
        b.displayBoard();
        b = CheckerBoard.blankBoard();
    }
    void threeByThree(Team center_team, Team[] surrounding, Space center, boolean isKing) {
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


    //Check that it parses space correctly
    //Checks that the piece is in bounds
    //Checks if there is a piece of the same team in the same spot
    //Checks that the current person moving is of the same turn
    @Test
    void testNoMoves() {
        Space start_loc = Main.parseSpace("C4R5");
        threeByThree(b.red, new Team[] {b.red, b.red, b.red, b.red}, start_loc, true);
        Move move1 = new Move(b, start_loc);
        assertFalse(move1.arePossibleMoves());
    }

    @Test
    void test1SlideKing() {
        Space start_loc = Main.parseSpace("C4R5");
        threeByThree(b.red, new Team[] {b.red, b.red, b.red, null}, start_loc, true);
        Move move1 = new Move(b, start_loc);
        assertTrue(move1.arePossibleMoves());
    }

    @Test
    void test2SlideKing() {
        Space start_loc = Main.parseSpace("C4R5");
        threeByThree(b.red, new Team[] {b.red, b.red, null, null}, start_loc, true);
        Move move1 = new Move(b, start_loc);
        assertEquals(2, move1.numPossibleMoves());
    }

    @Test
    void test3SlideKing() {
        Space start_loc = Main.parseSpace("C4R5");
        threeByThree(b.red, new Team[] {b.red, null, null, null}, start_loc, true);
        Move move1 = new Move(b, start_loc);
        assertEquals(3, move1.numPossibleMoves());
    }

    @Test
    void test4SlideKing() {
        Space start_loc = Main.parseSpace("C4R5");
        threeByThree(b.red, new Team[] {null, null, null, null}, start_loc, true);
        Move move1 = new Move(b, start_loc);
        assertEquals(4, move1.numPossibleMoves());
    }

    @Test
    void test1JumpKing() {
        Space start_loc = Main.parseSpace("C4R5");
        threeByThree(b.red, new Team[] {b.red, b.red, b.red, b.black}, start_loc, true);
        Move move1 = new Move(b, start_loc);
        assertEquals(1, move1.numPossibleMoves());
    }

    @Test
    void test2JumpKing() {
        Space start_loc = Main.parseSpace("C4R5");
        threeByThree(b.red, new Team[] {b.red, b.red, b.black, b.black}, start_loc, true);
        Move move1 = new Move(b, start_loc);
        assertEquals(2, move1.numPossibleMoves());
    }

    @Test
    void test3JumpKing() {
        Space start_loc = Main.parseSpace("C4R5");
        threeByThree(b.red, new Team[] {b.red, b.black, b.black, b.black}, start_loc, true);
        Move move1 = new Move(b, start_loc);
        assertEquals(3, move1.numPossibleMoves());
    }

    @Test
    void test4JumpKing() {
        Space start_loc = Main.parseSpace("C4R5");
        threeByThree(b.red, new Team[] {b.black, b.black, b.black, b.black}, start_loc, true);
        Move move1 = new Move(b, start_loc);
        assertEquals(4, move1.numPossibleMoves());
    }

    @Test
    void testOsPossibleJumps1() {
        Space start_loc = Main.parseSpace("C4R5");
        threeByThree(b.red, new Team[] {b.red, b.red, b.black, b.black}, start_loc, false);
        Move move1 = new Move(b, start_loc);
        assertEquals(1, move1.numPossibleMoves());
    }

    @Test
    void testOsPossibleJumps2() {
        Space start_loc = Main.parseSpace("C4R5");
        threeByThree(b.black, new Team[] {b.red, b.red, b.red, b.red}, start_loc, false);
        Move move1 = new Move(b, start_loc);
        assertEquals(2, move1.numPossibleMoves());
    }

    @Test
    void testXsPossibleJumps1() {
        Space start_loc = Main.parseSpace("C4R5");
        threeByThree(b.red, new Team[] {b.red, b.red, b.black, b.black}, start_loc, false);
        Move move1 = new Move(b, start_loc);
        assertEquals(1, move1.numPossibleMoves());
    }

    @Test
    void testXsPossibleJumps2() {
        Space start_loc = Main.parseSpace("C4R5");
        threeByThree(b.black, new Team[] {b.red, b.red, b.red, b.red}, start_loc, false);
        Move move1 = new Move(b, start_loc);
        assertEquals(2, move1.numPossibleMoves());
    }

    @Test
    void testInBounds() {
        assertFalse(b.inBounds(Main.parseSpace("C11R8")));
        assertFalse(b.inBounds(Main.parseSpace("C8R11")));

        assertFalse(b.inBounds(Main.parseSpace("C11R9")));
        assertFalse(b.inBounds(Main.parseSpace("C11R0")));
        assertFalse(b.inBounds(Main.parseSpace("C0R8")));
        assertFalse(b.inBounds(Main.parseSpace("C0R9")));
        assertFalse(b.inBounds(Main.parseSpace("C0R0")));

        assertTrue(b.inBounds(Main.parseSpace("C1R8")));
        assertTrue(b.inBounds(Main.parseSpace("C1R1")));
        assertTrue(b.inBounds(Main.parseSpace("C8R1")));
        assertTrue(b.inBounds(Main.parseSpace("C8R8")));



    }

    @Test
    void testMoveIsPossible() {
        Space start_loc = Main.parseSpace("C4R5");
        threeByThree(b.black, new Team[] {b.red, b.red, b.red, b.red}, start_loc, false);
        Move move1 = new Move(b, start_loc);
        assertEquals(2, move1.numPossibleMoves());

        assertFalse(move1.isPossible(Main.parseSpace("C8R8")));
        assertFalse(move1.isPossible(Main.parseSpace("C3R4")));
    }

    @Test
    void killPieceDir1() {
        Space start_loc = Main.parseSpace("C4R5");
        Piece start_pc = b.getBoardSpace(start_loc).pc;
        threeByThree(b.black, new Team[] {b.red, b.red, b.red, b.red}, start_loc, true);
        Move move1 = new Move(b, start_loc);
        move1.movePieceTo(Main.parseSpace("C6R3"));
        assertNull(b.getBoardSpace(("C5R4")).pc);

        assertEquals(0, b.num_turns);
        assertEquals(start_pc, b.restricted_piece_move);
    }

    @Test
    void killPieceDir2() {
        Space start_loc = Main.parseSpace("C4R5");
        Piece start_pc = b.getBoardSpace(start_loc).pc;

        threeByThree(b.black, new Team[] {b.red, b.red, b.red, b.red}, start_loc, true);
        Move move1 = new Move(b, start_loc);
        move1.movePieceTo(Main.parseSpace("C2R7"));
        assertNull(b.getBoardSpace(("C3R6")).pc);

        assertEquals(0, b.num_turns);
        assertEquals(start_pc, b.restricted_piece_move);

    }

    @Test
    void testDoubleJump() {
        Space start_loc = b.getBoardSpace(Main.parseSpace("C4R5"));

        threeByThree(b.black, new Team[] {b.red, b.red, b.red, b.red}, start_loc, true);
        Piece start_pc = b.getBoardSpace(Main.parseSpace("C4R5")).pc;
        b.getBoardSpace(("C5R2")).pc = b.red.roster.get(9);

        Move move1 = new Move(b, b.getBoardSpace(b.getLocation(start_pc)));
        Space to_move = Main.parseSpace("C6R3");
        move1.movePieceTo(to_move);

        assertNull(b.getBoardSpace(("C5R4")).pc);
        assertEquals(1, b.num_turns);
        assertEquals(start_pc, b.restricted_piece_move);
        assertNotNull(b.restricted_piece_move);


        Move move2 = new Move(b, to_move);
        assertEquals(1, move2.possible_jumps.size());

        to_move = Main.parseSpace("C4R1");
        move2.movePieceTo(to_move);
        assertEquals(0, b.num_turns);

        Move move3 = new Move(b, to_move);
        assertEquals(0, move3.possible_jumps.size());

        assertNull(b.restricted_piece_move);

    }

    @Test
    void testMakeOKing() {
        Piece piece_a = b.red.roster.get(0);
        System.out.println(b.getPieceTeam(piece_a).color);
        Space original_space = b.getBoardSpace("R7C1");
        original_space.pc = piece_a;
        Move move1 = new Move(b, original_space);
        assertEquals(1, move1.numPossibleMoves());
        move1.movePieceTo(Main.parseSpace("C2R8"));

        assertTrue(piece_a.isKing);
    }

    @Test
    void testMakeXKing() {
        Piece piece_a = b.black.roster.get(0);
        Space original_space = b.getBoardSpace("C1R2");
        original_space.pc = piece_a;
        Move move1 = new Move(b, original_space);
        assertEquals(1, move1.numPossibleMoves());
        move1.movePieceTo(Main.parseSpace("C2R1"));

        assertTrue(piece_a.isKing);
    }

    @Test
    void invalidUserInput() {
        b = CheckerBoard.freshBoard();
        String input = "adsf";
        assertFalse(Main.inputIsValid(b, input));
    }

    @Test
    void testInputSpaceOutOfBoundsCheck() {
        b = CheckerBoard.freshBoard();
        String input = "C12R12 to C2R4";
        assertFalse(Main.inputIsValid(b, input));
        input = "C2R3 to C3R4";
        assertTrue(Main.inputIsValid(b, input));
    }

    @Test
    void testInputPieceInSpaceCheck() {
        b.current_turn = b.black;
        Space start_loc = Main.parseSpace("C4R5");
        Piece start_pc = b.getBoardSpace(start_loc).pc;

        threeByThree(b.black, new Team[] {b.black, b.red, b.red, b.red}, start_loc, true);
        String input = "C4R5 to C5R4";
        assertFalse(Main.inputIsValid(b, input));
        assertEquals(b.black, b.current_turn);

        threeByThree(b.black, new Team[] {null, b.red, b.red, b.red}, Main.parseSpace("C7R2"), true);
        input = "C7R2 to R1C6";
        assertEquals(b.black, b.current_turn);
        assertTrue(Main.inputIsValid(b, input));
    }

    @Test
    void testRestrictedMoveModeY() {
        b.current_turn = b.black;
        Space start_loc = b.getBoardSpace(Main.parseSpace("C4R5"));

        threeByThree(b.black, new Team[] {b.red, b.red, b.red, b.red}, start_loc, true);
        Piece start_pc = b.getBoardSpace(Main.parseSpace("C4R5")).pc;
        b.getBoardSpace(("C5R2")).pc = b.red.roster.get(9);
        b.getBoardSpace(("C7R5")).pc = b.black.roster.get(9);

        Move move1 = new Move(b, b.getBoardSpace(b.getLocation(start_pc)));
        Space to_move = Main.parseSpace("C6R3");
        move1.movePieceTo(to_move);

        assertNull(b.getBoardSpace(("C5R4")).pc);
        assertEquals(1, b.num_turns);
        assertEquals(start_pc, b.restricted_piece_move);
        assertNotNull(b.restricted_piece_move);

        //Checks that when the person gets an additional turn and they decide to abstain
        String system_in = "Y";
        InputStream in = new ByteArrayInputStream(system_in.getBytes());
        System.setIn(in);

        String input = "C7R5 to C8R4";
        assertTrue(Main.inputIsValid(b, input));
        assertTrue(b.abstain);
    }

    @Test
    void testRestrictedMoveModeN() {
        b.current_turn = b.black;
        Space start_loc = b.getBoardSpace(Main.parseSpace("C4R5"));

        threeByThree(b.black, new Team[] {b.red, b.red, b.red, b.red}, start_loc, true);
        Piece start_pc = b.getBoardSpace(Main.parseSpace("C4R5")).pc;
        b.getBoardSpace(("C5R2")).pc = b.red.roster.get(9);
        b.getBoardSpace(("C7R5")).pc = b.black.roster.get(9);

        Move move1 = new Move(b, b.getBoardSpace(b.getLocation(start_pc)));
        Space to_move = Main.parseSpace("C6R3");
        move1.movePieceTo(to_move);

        assertNull(b.getBoardSpace(("C5R4")).pc);
        assertEquals(1, b.num_turns);
        assertEquals(start_pc, b.restricted_piece_move);
        assertNotNull(b.restricted_piece_move);

        //Checks that when the person gets an additional turn and they decide to abstain
        String system_in = "N";
        InputStream in = new ByteArrayInputStream(system_in.getBytes());
        System.setIn(in);

        String input = "C7R5 to C8R4";
        assertFalse(Main.inputIsValid(b, input));
        assertFalse(b.abstain);
    }



}
