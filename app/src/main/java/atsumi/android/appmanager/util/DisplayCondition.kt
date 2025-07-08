package atsumi.android.minimalist.util

interface DisplayCondition<T> {
    fun isDisplayable(obj: T): Boolean
}
