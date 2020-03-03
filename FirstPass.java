package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FirstPass {

    List<String> toAssemble;
    UsedOpcodeTable opcodeTable = new UsedOpcodeTable();
    LabelTable labelTable = new LabelTable();
    LiteralTable literalTable = new LiteralTable();
    ErrorTable errorTable = new ErrorTable();

    FirstPass(String fileName) throws IOException {

        this.toAssemble =
                Files.readAllLines(Paths.get( "D:/ETASH/Programing/ASSEMBLER/src/com/company/"+ fileName));

    }

    void doPassOne() {

        boolean moreInput = true;

        Line line;
        String symbol;
        String literal;
        Opcode opcode;

        long locationCounter = 0;
        int lineNumber = 0;

        while (moreInput){

            if (lineNumber < toAssemble.size()) { // for EOF Error
                line = new Line(toAssemble.get(lineNumber));

                if (!line.isCommentLine()){

                    symbol = line.getSymbol();
                    if (symbol != null){
                        if (!labelTable.addSymbol(symbol, locationCounter)){
                            errorTable.addError("Symbol defined more than once", lineNumber,
                                                        locationCounter, line.getLiteral()        );
                            line.type = -1;

                        }
                    }

                    literal = line.getLiteral();
                    if (literal != null) {
                        try {
                            literalTable.addLiteral(Integer.parseInt(literal));
                        } catch (Exception as) {
                            line.type = -1;
                            errorTable.addError("LITERAL NOT OF INT TYPE", lineNumber, locationCounter, literal);

                        }

                    }

                    opcode = new Opcode(line.getOpcode());
                    if (opcode.binaryCode != null) {
                        if (opcode.binaryCode == -1) {

                            if (5 == line.value.strip().length() &&
                                    line.value.strip().substring(0,5).equals(".DATA")) {
                                errorTable.addError("DATA DECLARATION BEFORE STP", lineNumber, locationCounter,"");
                                line.type = -1; //err


                            }else {

                                line.type = -1; // bad opcode
                                errorTable.addError("Opcode Not Defined", lineNumber, locationCounter,
                                                                    line.getOpcode());

                            }

                        } else if (opcode.binaryCode != 12) {
                            opcodeTable.addOpcode(opcode.opcodeName);
                            locationCounter++;

                        } else {
                            //end Statement
                            //opcodeTable.clearDuplicates();
                            literalTable.clearDuplicates();
                            locationCounter++;
                            //literalTable.assignAddresses(locationCounter);
                            opcodeTable.clearDuplicates();
                            moreInput = false;


                        }
                    }else {
                        // non opcode Statement
                        locationCounter++;

                    }
                }
                lineNumber++;
            } else {
                // record END OF FILE error missing stop
                errorTable.addError("EOF : missing STP", lineNumber, locationCounter,"");
                moreInput = false;
                opcodeTable.clearDuplicates();

            }
        }
        // for data
        boolean searchingForVariables = false;

        while (lineNumber < toAssemble.size()) {
            Line currentLine= new Line(toAssemble.get(lineNumber));

            if (!currentLine.isCommentLine()) {
                if (searchingForVariables) {
                    String variable = currentLine.getVariable();

                    if (variable.equals(" INVALID ")) {
                        errorTable.addError("INVALID DECLARATION OF VARIABLE", lineNumber,
                                                       locationCounter, "no = in line");
                        currentLine.type = - 1;

                    } else if (variable.equals(" INVALID 2")) {
                        errorTable.addError("INVALID DECLARATION OF VARIABLE", lineNumber, locationCounter,
                                            "variable name can not start with \" ' \"");
                        currentLine.type = -1;

                    } else {
                        String value = currentLine.getLiteral();
                        if (value != null) {

                            try {

                                if (!labelTable.addVariable(variable, Integer.parseInt(value), locationCounter)) {
                                    errorTable.addError("VARIABLE DEFINED MORE THAN ONCE", lineNumber,
                                                                     locationCounter, value);
                                    currentLine.type = -1;

                                } else {
                                    locationCounter++;

                                }

                            } catch (Exception as) {
                                errorTable.addError("LITERAL NOT OF INT TYPE", lineNumber,
                                                               locationCounter, value);
                                currentLine.type = -1;

                            }

                        } else {
                            errorTable.addError("NO LITERAL DEFINED FOR VARIABLE", lineNumber,
                                                          locationCounter, variable);
                            currentLine.type = -1;

                        }

                    }

                } else if (currentLine.value.strip().equals(".DATA")) {
                        searchingForVariables = true;
                        currentLine.type = 10; // directive

                } else {
                    errorTable.addError("SOME ERROR", lineNumber, locationCounter, "");
                    currentLine.type = -1;

                }

            }
            lineNumber++;

        }
        literalTable.assignAddresses(locationCounter);

    }

}

class UsedOpcodeTable {

    ArrayList<Opcode> table = new ArrayList<>();

    void addOpcode(String opcode) {
        Opcode toAdd = new Opcode(opcode);
        table.add(toAdd);

    }

    void clearDuplicates() {
        for (int index1 = 0 ; index1 < table.size() - 1 ; index1++) {
            for (int index2 = index1 + 1 ; index2 < table.size() ; index2++) {
                if (table.get(index1).binaryCode.equals(table.get(index2).binaryCode)) {
                    table.remove(index2);
                    index2--;

                }
            }
        }
    }
}

class LabelTable {

    ArrayList<Label> table = new ArrayList<>();

    boolean addVariable(String name, int value, long address) {
        Label toAdd = new Label("Variable", address, value, name);
        boolean isPresent = false;

        for (Label preExisting : table) {
            if (preExisting.name.equals(toAdd.name)){
                //error defined more than once
                return false;

            }
        }

        table.add(toAdd);
        return true;

    }

    boolean addSymbol(String name, long address){
        Label toAdd = new Label("Symbol", address, 0, name);


        for (Label preExisting : table) {
            if (preExisting.name.equals(toAdd.name)){
                //error handled in main
                return false;

            }
        }
        table.add(toAdd);
        return true;

    }
}

class LiteralTable {

    ArrayList<Literal> table = new ArrayList<>();

    void addLiteral(int value) {
        Literal toAdd = new Literal(value);
        table.add(toAdd);

    }

    void clearDuplicates() {
        for (int index1 = 0 ; index1 < table.size() - 1 ; index1++)  {
            for (int index2 = index1 + 1 ; index2 < table.size() ; index2++) {

                if (table.get(index1).value == table.get(index2).value) {
                    table.remove(index2);
                    index2--;

                }

            }
        }

    }

    void assignAddresses(long from){
        for (Literal literal : table){
            literal.address = from;
            from++;

        }
    }
}

class ErrorTable {
    ArrayList<Error> table = new ArrayList<>();

    void addError(String type, int lineNumber, long memoryLocation, String extraData) {
        table.add(new Error(type, lineNumber, memoryLocation));
        table.get(table.size() - 1).addExtraInfo(extraData);

    }
}

class Opcode {
    String opcodeName;
    Byte binaryCode;
    public static byte[][][] definedOpcodes = getDefinedOpcodes();

    Opcode(String opcodeName) {

        if (opcodeName != null) {
            this.opcodeName = opcodeName;
            this.binaryCode = definedOpcodes
                    [opcodeName.charAt(0) - 65]
                    [opcodeName.charAt(1) - 65]
                    [opcodeName.charAt(2) - 65];

        } // skip for now

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

class Literal {
    int value;
    long address;

    Literal(int value) {
        this.value = value;

    }

    void assignAddress(long address) {
        this.address = address;

    }
}

class Label {
    int value = 0;
    long address;
    String type;
    String name;

    Label(String type, long address, int value, String name) {
        this.address = address;
        this.type = type;
        this.value = value;
        this.name = name;

    }
}

class Line {
    String value;
    int type;

    Line(String value) {
        this.value = value;

        if (this.isCommentLine()) {
            type = 0;

        }

    }

    boolean isCommentLine() {
        boolean commentOrEmpty = true;

        for (int lineIndex = 0; lineIndex < value.length(); lineIndex++) {
            if (value.charAt(lineIndex) != '/' && value.charAt(lineIndex) != ' ') {
                commentOrEmpty = false;
                break;

            } else if (value.charAt(lineIndex) == '/') {

                if (lineIndex + 1 >= value.length() || value.charAt(lineIndex + 1) != '/') {
                    commentOrEmpty = false;
                    // may be error type = invalid (-1) comment is multi line
                    type = -1;

                }
                break;
            }
        }
        return commentOrEmpty;

    }

    String getSymbol() {

        int index = 0;
        String symbol = null;

        while (value.charAt(index) == ' ') {
            index++;

        }

        if (value.charAt(index) == '#') {
            String subS = value.substring(index);
            int last = subS.indexOf(' ');
            if (last == -1) {
                type = -1; // bad symbol

            } else {

                symbol = subS.substring(0, last);
            }

        }

        return symbol;

    }

    String getLiteral() {

        String literal = null;

        int start = value.indexOf('\'');

        if (start != -1) {
            if (start + 1 >= value.length()) {
                type = -1; // literal is bad

            } else {
                String subS = value.substring(start + 1);
                int end = subS.indexOf('\'');

                if (end == -1) {
                    type = -1; // literal is bad

                } else {
                    literal = subS.substring(0, end);

                }
            }
        }
        return literal;

    }

    String getOpcode() {
        String opcode = null;

        int index = 0;

        while (index < value.length()) {
            if (value.charAt(index) == '#') {
                index += (value.substring(index)).indexOf(' ');


                if (index == -1) {
                    type = -1; // no Opcode
                    return null;

                }

            } else if (value.charAt(index) != ' ') {
                int start = index;

                if (start != value.length() - 1) {
                    int end = value.substring(start).indexOf(' ');

                    if (end != -1) {
                        opcode = value.substring(start).substring(0, end);

                        if (opcode.length() == 3) {
                            if (opcode.toUpperCase().equals(opcode)) {
                                return opcode;

                            }
                        }

                    } else {
                        if (value.substring(start).equals("CLA")) {
                            return "CLA";

                        }else if (value.substring(start).equals("STP")){
                            return "STP";

                        }
                    }
                }
                break;

            } else {
                index++;

            }
        }
        return "IVD";

    }

    String getVariable() {
        String toCheck = value.strip();
        int lastIndexOfVariable = toCheck.indexOf('=');

        if (lastIndexOfVariable == -1) {
            //error
            return " INVALID "; //spaces to avoid similar

        } else {
            if (toCheck.charAt(0) == '\'') {
                return " INVALID 2";

            }

            return toCheck.substring(0, lastIndexOfVariable);

        }

    }
}

class Error {
    String type;
    int lineNumber;
    long memoryLocation;
    String extraInfo = "";

    Error(String type, int lineNumber, long memoryLocation) {
        this.type = type;
        this.lineNumber = lineNumber;
        this.memoryLocation = memoryLocation;

    }

    void addExtraInfo(String value) {
        this.extraInfo = value;
    }

}