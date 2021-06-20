package atsumi.android.appmanager.util

interface DisplayCondition<T> {
    fun isDisplayable(obj: T): Boolean
}
