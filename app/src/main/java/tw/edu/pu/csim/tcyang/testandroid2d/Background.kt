package tw.edu.pu.csim.tcyang.testandroid2d

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas

class Background (var image: Bitmap) {
    var x: Int = 0
    var y: Int = 0

    val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    val screenHeight = Resources.getSystem().displayMetrics.heightPixels

    init {
        image = Bitmap.createScaledBitmap(image, screenWidth, screenHeight, true)
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(image, x.toFloat(), y.toFloat(), null)
    }
}
