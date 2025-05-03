package nodomain.pacjo.smartspacer.plugin.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Icon
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.withTranslation
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.random.Random

@SuppressLint("DiscouragedApi")
fun isFirstRun(context: Context) {
    val file = File(context.filesDir, "data.json")

    // If file doesn't exist, so
    //   - it's the first run after installation / data reset
    //   - something went wrong, but we can blame that on the user
    if (!file.exists()) {
        val resourceId = context.resources.getIdentifier("default_data", "raw", context.packageName)

        if (resourceId != 0) {
            val outputStream: OutputStream = FileOutputStream(file)

            context.resources.openRawResource(resourceId).use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
}

fun imageTargetAdjustDrawable(context: Context, drawableResId: Int): Icon {
    val vectorDrawable = ContextCompat.getDrawable(context, drawableResId)!!

    val canvasWidth = vectorDrawable.intrinsicHeight / 9 * 16
    val canvasHeight = vectorDrawable.intrinsicHeight

    val bitmap = createBitmap(canvasWidth, canvasHeight)
    val canvas = Canvas(bitmap)

    canvas.withTranslation((canvasWidth - vectorDrawable.intrinsicWidth) / 4f, 0f) {
        // we should divide by 2 to be exact, but 4 looks better due to left padding
        vectorDrawable.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        vectorDrawable.draw(this)
    }

    return Icon.createWithBitmap(bitmap.toDrawable(context.resources).bitmap)
}

fun getRandomFromList(elements: List<Any>): Any {
    val randomIndex = Random.nextInt(elements.size)

    return elements[randomIndex]
}

fun <T> List<T>.getRandom(): T {
    val randomIndex = Random.nextInt(size)
    return this[randomIndex]
}

fun <A, B> List<Pair<A, B>>.getRandomFromPairs(): Pair<A, B> {
    val randomIndex = Random.nextInt(size)
    return this[randomIndex]
}