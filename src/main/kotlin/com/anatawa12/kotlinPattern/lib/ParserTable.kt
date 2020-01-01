package com.anatawa12.kotlinPattern.lib

import org.jetbrains.annotations.Range

/**
 * Created by anatawa12 on 2020/01/01.
 */
class ParserTable(
    val syntaxes: List<ParsingSyntaxItem>,
    val gotoTable: Map<ParserTableKey, GotoElement>,
    val actionsTable: Map<ParserTableKey, Actions>
) {
    val isLr1Syntax: Boolean by lazy { actionsTable.values.all { it.actions.size == 1 } }
    val lr1ActionTable: Map<ParserTableKey, Action> by lazy {
        check(isLr1Syntax) { "this is not lr1 syntax" }
        actionsTable.mapValues { it.value.actions.single() }
    }
}

data class ParsingSyntaxItem(
    val result: TokenId,
    val tokensCount: @Range(from = 0, to = Int.MAX_VALUE.toLong()) Int
) {
    init {
        require(0 <= tokensCount) { "tokensCount must be positive or zero" }
    }
}

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class TokenId constructor(val id: Int) {
    override fun toString(): String = "Token($id)"

    companion object {
        val EOF = TokenId(0)
    }
}

inline class State constructor(val state: Int) {
    override fun toString(): String = "State($state)"
}

inline class GotoElement constructor(val state: State) {
    override fun toString(): String = "GoTo($state)"
}

data class ParserTableKey(val state: State, val token: TokenId)

data class Actions(val actions: List<Action>) {
    override fun toString(): String = "Actions($actions)"
}

sealed class Action

data class ShiftAction(val to: State) : Action() {
    override fun toString(): String = "Shift(to=$to)"
}

data class ReduceAction(val syntax: Int) : Action() {
    override fun toString(): String = "Reduce(syntax=$syntax)"
}

object AcceptAction : Action() {
    override fun toString(): String = "Accept"
}
