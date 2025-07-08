package jp.bizen.app.minimalist.util

interface DisplayCondition<T> {
    fun isDisplayable(obj: T): Boolean
}
