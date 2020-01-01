package com.anatawa12.kotlinPattern.lib

/**
 * Created by anatawa12 on 2020/01/01.
 */
class LR1Parser<TToken : IToken>(parserTable: ParserTable, tokens: Sequence<TToken>) {
    private val tokenIterator = tokens.iterator()
    private val syntaxes: List<ParsingSyntaxItem> = parserTable.syntaxes
    private val gotoTable: Map<ParserTableKey, GotoElement> = parserTable.gotoTable
    private val actionTable: Map<ParserTableKey, Action> = parserTable.lr1ActionTable

    private val stack: MutableList<StackElement> = mutableListOf(StackElement(State(0), null))

    ////

    private fun runAction(action: Action, token: TToken): Boolean = when (action) {
        is ShiftAction -> {
            moveNextToken()
            stack.push(StackElement(action.to, token))
            false
        }
        is ReduceAction -> {
            val syntax = syntaxes[action.syntax]
            val elements = List(syntax.tokensCount) { stack.pop().value }.reversed()
            val goto = gotoTable[ParserTableKey(stack.peek().state, syntax.result)] ?: syntaxError()
            stack.push(StackElement(goto.state, elements))
            false
        }
        AcceptAction -> {
            moveNextToken()
            true
        }
    }

    fun runActions(): Any? {
        while (true) {
            val action = actionTable[ParserTableKey(stack.peek().state, peekToken().tokenId)] ?: syntaxError()
            if (runAction(action, peekToken())) break
        }
        return stack.pop().value
    }

    //////

    private fun syntaxError(): Nothing = error("syntax error")

    private var cachedToken: TToken? = null

    private fun peekToken() = cachedToken ?: tokenIterator.next().also { cachedToken = it }

    private fun moveNextToken() {
        if (cachedToken != null) {
            cachedToken = null
        } else {
            tokenIterator.next()
        }
    }

    private fun MutableList<StackElement>.peek(): StackElement = last()
    private fun MutableList<StackElement>.pop(): StackElement = removeAt(lastIndex)
    private fun MutableList<StackElement>.push(value: StackElement) {
        add(size, value)
    }

    private data class StackElement(val state: State, val value: Any?)
}
