package jeziellago.android.mlkit.mlkitfacedetection

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark

internal class DetectionViewer(private val cameraWidth: Int,
                               private val cameraHeight: Int) {

    fun showDetection(faces: List<FirebaseVisionFace>): Bitmap {
        val detectedFaces = ArrayList<DetectedFace>()
        faces.forEach {
            with(it) {
                val detectedFace = DetectedFace(
                    boundingBox = boundingBox,
                    leftEar = getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR),
                    rightEar = getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR),
                    leftEye = getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE),
                    rightEye = getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE),
                    leftMouth = getLandmark(FirebaseVisionFaceLandmark.LEFT_MOUTH),
                    rightMouth = getLandmark(FirebaseVisionFaceLandmark.RIGHT_MOUTH),
                    bottomMouth = getLandmark(FirebaseVisionFaceLandmark.BOTTOM_MOUTH),
                    leftCheek = getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK),
                    rightCheek = getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK),
                    noseBase = getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE)
                )
                detectedFaces.add(detectedFace)
            }
        }
        return drawDetection(detectedFaces)
    }

    private fun drawDetection(detectedFaces: List<DetectedFace>): Bitmap {
        val bmp = Bitmap.createBitmap(
                cameraHeight,
                cameraWidth,
                Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bmp)
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f

        detectedFaces.forEach {
            with(canvas) {
                drawRect(it.boundingBox, paint)
                drawPoints(it.earsPoints(), paint)
                drawPoints(it.cheekPoints(), paint)
                drawPoints(it.eyesPoints(), paint)
                drawPoints(it.mouthPoints(), paint)
                drawPoints(it.nosePoints(), paint)
            }
        }
        return bmp
    }
}
