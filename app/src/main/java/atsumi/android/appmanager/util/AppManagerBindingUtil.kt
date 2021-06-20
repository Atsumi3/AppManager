package atsumi.android.appmanager.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@BindingAdapter("setImageDrawable")
fun ImageView.setImageDrawable(drawable: Drawable?) {
    setImageDrawable(drawable)
}
