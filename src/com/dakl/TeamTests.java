package com.dakl;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class TeamTests {
    Team base_team;
    ArrayList<Piece> base_pieces;

    @BeforeEach
    public void setUpClass() {
        for (int i = 0; i < 12; i++) {
            this.base_pieces.add(new Piece(i));
        }

        this.base_team = new Team("black", this.base_pieces);
    }

    @Test
    public void TestInit() {

        //Constructor one
        Team testColor=new Team("red");
        assertEquals("red",testColor.color);

        //Constructor two
        Team two=new Team("red",new ArrayList<Piece>());
        assertEquals(0,two.roster.size());
    }

    @Test
    public void TestInitTeam() {

    }

    @Test
    public void  GetPieceIdsFromRoster() {
        base_team.initTeam(0, 12);

        Piece[] expected = new Piece[12];
        Piece[] roster_arr = new Piece[base_team.roster.size()];
        for (int i = 0; i < 12; i++) {
            roster_arr[i] = base_team.roster.get(i);
            expected[i] = new Piece(i);
        }



        assertEquals(expected,  roster_arr);
    }

    @Test
    public void TestGenerateSide() {

    }

    @Test
    public void TestOnTeam() {

    }


}
