package com.company;

public class OpcodeTable {
    /*
        byte[][][] first 3 boxes are for first 3 letters of opcode
        done for O(1) search time [for any number of opcodes]
    */

    public static byte[][][] definedOpcodes = getDefinedOpcodes();

    public OpcodeTable() {


    }


    private static byte[][][] getDefinedOpcodes() {

        byte[][][] toReturn = new byte[26][26][26];

        for (int i = 0 ; i < 26 ; i++){
            for (int j = 0 ; j < 26 ; j++){
                for (int k = 0 ; k < 26 ; k++){
                    toReturn[i][j][k] = -1;
                }
            }
        }

        // CLA (C-2 ; L-11 ; A-0) [0, 0, 0, 0]
        toReturn[2][11][0] = 0;

        // LAC (L-11 ; A-0 ; C-2) [0, 0, 0, 1]
        toReturn[11][0][2] = 1;

        // SAC (S-18 ; A-0 ; C-2) [0, 0, 1, 0]
        toReturn[18][0][2] = 2;

        // ADD (A-0 ; D-3 ; D-3) [0, 0, 1, 1]
        toReturn[0][3][3] = 3;

        // SUB (S-18 ; U-20 ; B-1) [0, 1, 0, 0]
        toReturn[18][20][1] = 4;

        // BRZ (B-1 ; R-17 ; Z-25) [0, 1, 0, 1]
        toReturn[1][17][25] = 5;

        // BRN (B-1 ; R-17 ; N-13) [0, 1, 1, 0]
        toReturn[1][17][13] = 6;

        // BRP (B-1 ; R-17 ; P-15) [0, 1, 1, 1]
        toReturn[1][17][15] = 7;

        // INP (I-8 ; N-13 ; P-15) [1, 0, 0, 0]
        toReturn[8][13][15] = 8;

        // DSP (D-3 ; S-18 ; P-15) [1, 0, 0, 1]
        toReturn[3][13][15] = 9;

        // MUL (M-12 ; U-20 ; L-11) [1, 0, 1, 0]
        toReturn[12][20][11] = 10;

        // DIV (D-3 ; I-8 ; V-21) [1, 0, 1, 1]
        toReturn[3][8][21] = 11;

        // STP (S-18 ; T-19 ; P-15) [1, 1, 0, 0]
        toReturn[18][19][15] = 12;

        return toReturn;
    }
}
