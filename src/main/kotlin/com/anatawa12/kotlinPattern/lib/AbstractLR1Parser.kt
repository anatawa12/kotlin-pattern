package com.anatawa12.kotlinPattern.lib

/**
 * Created by anatawa12 on 2020/01/01.
 */
abstract class AbstractLR1Parser() {
    protected abstract val syntaxes: List<ParsingSyntaxItem>
    protected abstract val gotoTable: Map<ParserTableKey, GotoElement>

    ////

    protected fun runAction(action: Action): Boolean = when (action) {
        is ShiftAction -> {
            stackPush(StackElement(action.to, popToken()))
            false
        }
        is ReduceAction -> {
            val syntax = syntaxes[action.syntax]
            val elements = List(syntax.tokensCount) { stackPop().value }.reversed()
            val goto = gotoTable[ParserTableKey(stackPeek().state, syntax.result)] ?: syntaxError()
            stackPush(StackElement(goto.state, elements))
            false
        }
        AcceptAction -> {
            popToken()
            true
        }
    }

    //////

    protected abstract fun stackPush(value: StackElement)
    protected abstract fun stackPop(): StackElement
    protected abstract fun stackPeek(): StackElement

    protected open fun syntaxError(): Nothing = error("syntax error")

    protected abstract fun peekToken(): IToken
    protected abstract fun popToken(): IToken

    protected data class StackElement(val state: State, val value: Any?)
}
