package jeziellago.android.mlkit.mlkitfacedetection

import android.graphics.Rect
import com.google.firebase.ml.vision.common.FirebaseVisionPoint
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark

internal class DetectedFace(
    val boundingBox: Rect,
    private val leftEar: FirebaseVisionFaceLandmark?,
    private val rightEar: FirebaseVisionFaceLandmark?,
    private val leftEye: FirebaseVisionFaceLandmark?,
    private val rightEye: FirebaseVisionFaceLandmark?,
    private val leftMouth: FirebaseVisionFaceLandmark?,
    private val rightMouth: FirebaseVisionFaceLandmark?,
    private val bottomMouth: FirebaseVisionFaceLandmark?,
    private val leftCheek: FirebaseVisionFaceLandmark?,
    private val rightCheek: FirebaseVisionFaceLandmark?,
    private val noseBase: FirebaseVisionFaceLandmark?
) {

    fun earsPoints() =  makePoints(leftEar, rightEar)
    fun eyesPoints() =  makePoints(leftEye, rightEye)
    fun cheekPoints() =  makePoints(leftCheek, rightCheek)
    fun mouthPoints() =  makePoints(leftMouth, rightMouth, bottomMouth)
    fun nosePoints() =  makePoints(noseBase)

    private fun makePoints(vararg landmarks: FirebaseVisionFaceLandmark?): FloatArray {

        val points = ArrayList<FirebaseVisionPoint>()
        landmarks.forEach { if (it != null) points.add(it.position) }

        val floatPoints = FloatArray(points.size * 2)
        var index = 0
        points.forEach {
            floatPoints[index++] = it.x
            floatPoints[index++] = it.y
        }
        return floatPoints
    }

}