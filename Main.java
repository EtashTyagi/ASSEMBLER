package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here
        FirstPass x = new FirstPass("trial.txt");
        x.doPassOne();

        System.out.println("Opcode Table");
        for (Opcode o : x.opcodeTable.table){
            System.out.println("Opcode -> " + o.opcodeName + " | " + o.binaryCode);

        }

        System.out.println("\n" + "Literal Table");
        for (Literal l : x.literalTable.table){
            System.out.println("Literal -> " + l.value + " | " + l.address);

        }

        System.out.println("\n" + "Label Table");
        for (Label lab : x.labelTable.table){
            System.out.println("Label -> " + lab.name + " | " + lab.type + " | " + lab.address + " | " + lab.value);

        }

        System.out.println("\n" + "Error Table");
        for (Error e : x.errorTable.table) {
            System.out.println("Error -> " + e.type + " | " + (e.lineNumber + 1) + " | " + e.extraInfo);

        }

    }
}
