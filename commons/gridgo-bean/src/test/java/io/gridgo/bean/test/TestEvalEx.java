package io.gridgo.bean.test;

import java.util.List;

import com.udojava.evalex.Expression;

public class TestEvalEx {

    public static void main(String[] args) {
        Expression exp = new Expression("x + y + z");
        List<String> vars = exp.getUsedVariables();
        System.out.println("vars: " + vars);
    }
}
