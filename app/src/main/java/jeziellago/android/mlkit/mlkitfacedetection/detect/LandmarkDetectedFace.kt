package jeziellago.android.mlkit.mlkitfacedetection.detect

import android.graphics.Rect
import com.google.firebase.ml.vision.common.FirebaseVisionPoint
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark

internal class LandmarkDetectedFace(
    boundingBox: Rect,
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
): DetectedFace(boundingBox) {

    fun earsPoints() =  makeLandmarkPoints(leftEar, rightEar)
    fun eyesPoints() =  makeLandmarkPoints(leftEye, rightEye)
    fun cheekPoints() =  makeLandmarkPoints(leftCheek, rightCheek)
    fun mouthPoints() =  makeLandmarkPoints(leftMouth, rightMouth, bottomMouth)
    fun nosePoints() =  makeLandmarkPoints(noseBase)

    private fun makeLandmarkPoints(vararg landmarks: FirebaseVisionFaceLandmark?): FloatArray {

        val points = ArrayList<FirebaseVisionPoint>()
        landmarks.forEach { if (it != null) points.add(it.position) }
        return makePoints(points)
    }

}