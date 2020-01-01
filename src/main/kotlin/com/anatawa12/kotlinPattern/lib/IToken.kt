package com.anatawa12.kotlinPattern.lib

/**
 * Created by anatawa12 on 2020/01/01.
 */
interface IToken {
    val tokenId: TokenId

    companion object {
        operator fun invoke(id: TokenId): IToken = Token(id)

        private data class Token(override val tokenId: TokenId) : IToken {
            override fun toString(): String = "IToken(${tokenId.id})"
        }
    }
}
