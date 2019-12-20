package com.dakl;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class CheckerboardTests {

    //This is a test case that you want to make sure works
    @Test
    public void createPiece() {
        //You initialize certain parts that you may want to test
        Piece test1 = new Piece(0);

        //Run logic code that may be needed, in this case it's very simple so it's not required

        //How do you make sure its correct?
        assertEquals(0, test1.id);
    }

    @Test
    public void PiecesAreEqual() {
        Piece test1 = new Piece(0);
        Piece test2 = new Piece(0);

        assertTrue(test1.equals(test2));
    }


}
