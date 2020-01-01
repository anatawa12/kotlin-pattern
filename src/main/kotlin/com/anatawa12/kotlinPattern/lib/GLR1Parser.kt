package com.anatawa12.kotlinPattern.lib

/**
 * Created by anatawa12 on 2020/01/01.
 */
class GLR1Parser(parserTable: ParserTable, tokens: Sequence<IToken>) : AbstractLR1Parser() {
    private val tokenIterator = PeekableIterator(tokens.iterator())
    override val syntaxes: List<ParsingSyntaxItem> = parserTable.syntaxes
    override val gotoTable: Map<ParserTableKey, GotoElement> = parserTable.gotoTable
    private val actionsTable: Map<ParserTableKey, Actions> = parserTable.actionsTable

    ////

    fun runActions(): List<Any?> {
        val accepts = mutableListOf<StackNodeRef>()
        var refs = listOf(StackNodeRef(StackNode(null, StackElement(State(0), null))))
        while (true) {
            val newRefs = mutableListOf<StackNodeRef>()
            refs.forEach { ref ->
                runActionForRef(ref).forEach { (accept, newRef) ->
                    if (accept) accepts += newRef
                    else newRefs += newRef
                }
            }
            if (newRefs.isEmpty()) break
            refs = newRefs
        }
        return accepts.map { it.peek().value }
    }

    private fun runActionForRef(oldCurrent: StackNodeRef): List<Pair<Boolean, StackNodeRef>> {
        val actions = actionsTable[ParserTableKey(oldCurrent.peek().state, peekToken().tokenId)]?.actions.orEmpty()
        val result = mutableListOf<Pair<Boolean, StackNodeRef>>()

        when {
            actions.isEmpty() -> {
            }
            actions.size == 1 -> {
                try {
                    result += runActionWith(oldCurrent, actions.single())
                } catch (e: SyntaxException) {
                }
            }
            else -> {
                val oldCurrent = currentRef
                actions.forEach { action ->
                    try {
                        result += runActionWith(oldCurrent.clone(), action)
                    } catch (e: SyntaxException) {
                    }
                }
            }
        }

        return result
    }

    private lateinit var currentRef: StackNodeRef

    private fun runActionWith(ref: StackNodeRef, action: Action): Pair<Boolean, StackNodeRef> {
        currentRef = ref
        val accept = runAction(action)
        return accept to ref
    }

    //////

    override fun peekToken() = tokenIterator.peek()

    override fun popToken() = tokenIterator.next()

    override fun syntaxError(): Nothing = throw SyntaxException

    override fun stackPush(value: StackElement) = currentRef.push(value)

    override fun stackPop(): StackElement = currentRef.pop()

    override fun stackPeek(): StackElement = currentRef.peek()

    private class StackNodeRef(var refTo: StackNode) {
        fun pop(): StackElement {
            val result = refTo
            refTo = refTo.parent ?: throw NoSuchElementException()
            return result.element
        }

        fun peek(): StackElement = refTo.element

        fun push(value: StackElement) {
            refTo = StackNode(refTo, value)
        }

        fun clone(): StackNodeRef = StackNodeRef(refTo)
        fun toList(): List<StackElement> {
            var resultList = mutableListOf<StackElement>()
            var ref: StackNode? = refTo
            while (ref != null) {
                resultList.add(ref.element)
                ref = ref.parent
            }
            return resultList.reversed()
        }
    }

    private class StackNode(val parent: StackNode?, val element: StackElement)

    private object SyntaxException : Exception()
}
