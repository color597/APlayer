package remix.myplayer.ui.widget

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import remix.myplayer.theme.ThemeStore

class SongIndicatorView : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        val bg = GradientDrawable()
        bg.cornerRadius = 5000f
        bg.setColor(ThemeStore.highLightTextColor)
        background = bg
    }

}