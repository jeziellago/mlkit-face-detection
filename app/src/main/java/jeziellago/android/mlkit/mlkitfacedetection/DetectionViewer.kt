package jeziellago.android.mlkit.mlkitfacedetection

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.ImageView
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark

internal class DetectionViewer(private val cameraWidth: Int,
                               private val cameraHeight: Int,
                               private val overlayView: ImageView) {

    fun showDetection(faces: List<FirebaseVisionFace>) {
        val detectedFaces = ArrayList<DetectedFace>()
        faces.forEach { face ->
            val detectedFace = DetectedFace(
                    boundingBox = face.boundingBox,
                    leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR),
                    rightEar = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR),
                    leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE),
                    rightEye = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE),
                    leftMouth = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_MOUTH),
                    rightMouth = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_MOUTH),
                    bottomMouth = face.getLandmark(FirebaseVisionFaceLandmark.BOTTOM_MOUTH),
                    leftCheek = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK),
                    rightCheek = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK),
                    noseBase = face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE)
            )
            detectedFaces.add(detectedFace)
        }

        drawDetection(detectedFaces)
    }

    private fun drawDetection(detectedFaces: List<DetectedFace>) {
        val width = cameraWidth
        val height = cameraHeight
        val bmp = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bmp)
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f

        detectedFaces.forEach {
            canvas.drawRect(it.boundingBox, paint)
            canvas.drawPoints(it.earsPoints(), paint)
            canvas.drawPoints(it.cheekPoints(), paint)
            canvas.drawPoints(it.eyesPoints(), paint)
            canvas.drawPoints(it.mouthPoints(), paint)
            canvas.drawPoints(it.nosePoints(), paint)
        }
        overlayView.setImageBitmap(bmp)
    }
}