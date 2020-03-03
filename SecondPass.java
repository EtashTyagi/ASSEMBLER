package com.company;

import java.io.IOException;

public class SecondPass {

    FirstPass firstPassData;

    SecondPass(String fileName) throws IOException {
        firstPassData = new FirstPass(fileName);

    }

    void doSecondPass() {

    }

}
