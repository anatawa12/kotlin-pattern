package com.anatawa12.kotlinPattern.lib

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by anatawa12 on 2020/01/01.
 */
class LR1ParserTest {
    val ASTARISK = TokenId(1)
    val PLUS = TokenId(2)
    val NUMBER = TokenId(3)
    val EOF = TokenId.EOF

    val EXP = TokenId(-1)
    val LITERAL = TokenId(-2)

    @Test
    fun runActions() {
        assertEquals(
            listOf(
                listOf(listOf(listOf(IToken(NUMBER))), IToken(PLUS), listOf(IToken(NUMBER))),
                IToken(ASTARISK),
                listOf(IToken(NUMBER))
            ),

            LR1Parser(
                makeTable(),
                sequenceOf(IToken(NUMBER), IToken(PLUS), IToken(NUMBER), IToken(ASTARISK), IToken(NUMBER), IToken(EOF))
            )
                .runActions()
        )
    }

    /**
     * (0) EXP → EXP * LITERAL
     * (1) EXP → EXP + LITERAL
     * (2) EXP → LITERAL
     * (3) LITERAL → num
     *
     * | state |  *   |  +   |  num |  EOF | EXP | LITERAL |
     * | ----- | ---- | ---- | ---- |  --- | --- | ------- |
     * |   0   |      |      |  s1  |      |  2  |    3    |
     * |   1   |  r3  |  r3  |  r3  |  r3  |     |         |
     * |   2   |  s4  |  s5  |      |  acc |     |         |
     * |   3   |  r2  |  r2  |  r2  |  r2  |     |         |
     * |   4   |      |      |  s1  |      |     |    6    |
     * |   5   |      |      |  s1  |      |     |    7    |
     * |   6   |  r0  |  r0  |  r0  |  r0  |     |         |
     * |   7   |  r1  |  r1  |  r1  |  r1  |     |         |
     */

    fun makeTable(): ParserTable {
        return ParserTable(
            syntaxes = listOf(
                ParsingSyntaxItem(EXP, listOf(EXP, ASTARISK, LITERAL).size),
                ParsingSyntaxItem(EXP, listOf(EXP, PLUS, LITERAL).size),
                ParsingSyntaxItem(EXP, listOf(LITERAL).size),
                ParsingSyntaxItem(LITERAL, listOf(NUMBER).size)
            ),
            gotoTable = mapOf(
                ParserTableKey(State(0), EXP) to GotoElement(State(2)),
                ParserTableKey(State(0), LITERAL) to GotoElement(State(3)),
                ParserTableKey(State(4), LITERAL) to GotoElement(State(6)),
                ParserTableKey(State(5), LITERAL) to GotoElement(State(7))
            ),
            actionsTable = mapOf(
                ParserTableKey(State(0), NUMBER) to Actions(listOf(ShiftAction(State(1)))),

                ParserTableKey(State(1), ASTARISK) to Actions(listOf(ReduceAction(3))),
                ParserTableKey(State(1), PLUS) to Actions(listOf(ReduceAction(3))),
                ParserTableKey(State(1), NUMBER) to Actions(listOf(ReduceAction(3))),
                ParserTableKey(State(1), EOF) to Actions(listOf(ReduceAction(3))),

                ParserTableKey(State(2), ASTARISK) to Actions(listOf(ShiftAction(State(4)))),
                ParserTableKey(State(2), PLUS) to Actions(listOf(ShiftAction(State(5)))),
                //NUMBER
                ParserTableKey(State(2), EOF) to Actions(listOf(AcceptAction)),

                ParserTableKey(State(3), ASTARISK) to Actions(listOf(ReduceAction(2))),
                ParserTableKey(State(3), PLUS) to Actions(listOf(ReduceAction(2))),
                ParserTableKey(State(3), NUMBER) to Actions(listOf(ReduceAction(2))),
                ParserTableKey(State(3), EOF) to Actions(listOf(ReduceAction(2))),

                ParserTableKey(State(4), NUMBER) to Actions(listOf(ShiftAction(State(1)))),

                ParserTableKey(State(5), NUMBER) to Actions(listOf(ShiftAction(State(1)))),

                ParserTableKey(State(6), ASTARISK) to Actions(listOf(ReduceAction(0))),
                ParserTableKey(State(6), PLUS) to Actions(listOf(ReduceAction(0))),
                ParserTableKey(State(6), NUMBER) to Actions(listOf(ReduceAction(0))),
                ParserTableKey(State(6), EOF) to Actions(listOf(ReduceAction(0))),

                ParserTableKey(State(7), ASTARISK) to Actions(listOf(ReduceAction(1))),
                ParserTableKey(State(7), PLUS) to Actions(listOf(ReduceAction(1))),
                ParserTableKey(State(7), NUMBER) to Actions(listOf(ReduceAction(1))),
                ParserTableKey(State(7), EOF) to Actions(listOf(ReduceAction(1)))
            )
        )
    }
}
