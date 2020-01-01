package com.anatawa12.kotlinPattern.lib

/**
 * Created by anatawa12 on 2020/01/01.
 */
class LR1Parser(parserTable: ParserTable, tokens: Sequence<IToken>) : AbstractLR1Parser() {
    private val tokenIterator = PeekableIterator(tokens.iterator())
    override val syntaxes: List<ParsingSyntaxItem> = parserTable.syntaxes
    override val gotoTable: Map<ParserTableKey, GotoElement> = parserTable.gotoTable
    private val actionTable: Map<ParserTableKey, Action> = parserTable.lr1ActionTable

    private val stack: MutableList<StackElement> = mutableListOf(StackElement(State(0), null))

    ////

    fun runActions(): Any? {
        while (true) {
            val action = actionTable[ParserTableKey(stackPeek().state, peekToken().tokenId)] ?: syntaxError()
            if (runAction(action)) break
        }
        return stackPop().value
    }

    //////

    override fun peekToken() = tokenIterator.peek()
    override fun popToken() = tokenIterator.next()

    override fun stackPush(value: StackElement) = stack.add(stack.size, value)
    override fun stackPop(): StackElement = stack.removeAt(stack.lastIndex)
    override fun stackPeek(): StackElement = stack.last()
}
