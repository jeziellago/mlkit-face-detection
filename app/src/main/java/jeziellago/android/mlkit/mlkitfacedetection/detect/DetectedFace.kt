package jeziellago.android.mlkit.mlkitfacedetection.detect

import android.graphics.Rect
import com.google.firebase.ml.vision.common.FirebaseVisionPoint

internal abstract class DetectedFace(val boundingBox: Rect) {

    fun makePoints(points: ArrayList<FirebaseVisionPoint>): FloatArray {
        val floatPoints = FloatArray(points.size * 2)
        var index = 0
        points.forEach {
            floatPoints[index++] = it.x
            floatPoints[index++] = it.y
        }
        return floatPoints
    }

}