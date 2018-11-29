package jeziellago.android.mlkit.mlkitfacedetection.detect

import android.graphics.Rect
import com.google.firebase.ml.vision.common.FirebaseVisionPoint
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour

internal class ContourDetectedFace(
        boundingBox: Rect,
        private val faceOvalContour: FirebaseVisionFaceContour?,
        private val rightEyeContour: FirebaseVisionFaceContour?,
        private val leftEyeContour: FirebaseVisionFaceContour?,
        private val noseBridgeContour: FirebaseVisionFaceContour?,
        private val noseBottomContour: FirebaseVisionFaceContour?,
        private val upperLipTopContour: FirebaseVisionFaceContour?,
        private val upperLipBottomContour: FirebaseVisionFaceContour?,
        private val lowerLipTopContour: FirebaseVisionFaceContour?,
        private val lowerLipBottomContour: FirebaseVisionFaceContour?,
        private val leftEyeBrowTopContour: FirebaseVisionFaceContour?,
        private val leftEyeBrowBottomContour: FirebaseVisionFaceContour?,
        private val rightEyeBrowTopContour: FirebaseVisionFaceContour?,
        private val rightEyeBrowBottomContour: FirebaseVisionFaceContour?
): DetectedFace(boundingBox) {

    fun faceContourPoints() = makeContourPoints(faceOvalContour)
    fun noseContourPoints() = makeContourPoints(noseBridgeContour, noseBottomContour)
    fun eyeContourPoints() = makeContourPoints(leftEyeContour, rightEyeContour)
    fun lipContourPoints() = makeContourPoints(upperLipTopContour, upperLipBottomContour,
            lowerLipTopContour, lowerLipBottomContour)
    fun eyeBrowContourPoints() = makeContourPoints(leftEyeBrowTopContour, leftEyeBrowBottomContour,
            rightEyeBrowTopContour, rightEyeBrowBottomContour)

    private fun makeContourPoints(vararg contours: FirebaseVisionFaceContour?): List<FloatArray> {
        val contourPoints = ArrayList<FloatArray>()
        contours.forEach {
            if (it != null) {
                contourPoints.add(makePoints(it.points as ArrayList<FirebaseVisionPoint>))
            }
        }
        return contourPoints
    }

}