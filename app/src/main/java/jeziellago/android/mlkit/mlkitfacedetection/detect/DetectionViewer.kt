package jeziellago.android.mlkit.mlkitfacedetection.detect

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark

internal class DetectionViewer(private val cameraWidth: Int,
                               private val cameraHeight: Int,
                               private val enableContours: Boolean = true) {

    fun showDetection(faces: List<FirebaseVisionFace>): Bitmap {
        val contourFaces = ArrayList<ContourDetectedFace>()
        val landmarkFaces = ArrayList<LandmarkDetectedFace>()

        faces.forEach {
            if (enableContours) {
                val contourFace = ContourDetectedFace(
                        boundingBox = it.boundingBox,
                        faceOvalContour = it.getContour(FirebaseVisionFaceContour.FACE),
                        leftEyeContour = it.getContour(FirebaseVisionFaceContour.LEFT_EYE),
                        rightEyeContour = it.getContour(FirebaseVisionFaceContour.RIGHT_EYE),
                        noseBridgeContour = it.getContour(FirebaseVisionFaceContour.NOSE_BRIDGE),
                        noseBottomContour = it.getContour(FirebaseVisionFaceContour.NOSE_BOTTOM),
                        upperLipTopContour = it.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP),
                        upperLipBottomContour = it.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM),
                        lowerLipTopContour = it.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP),
                        lowerLipBottomContour = it.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM),
                        leftEyeBrowTopContour = it.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_TOP),
                        leftEyeBrowBottomContour = it.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM),
                        rightEyeBrowTopContour = it.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP),
                        rightEyeBrowBottomContour = it.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM))
                contourFaces.add(contourFace)
            } else {
                val landmarkFace = LandmarkDetectedFace(
                        boundingBox = it.boundingBox,
                        leftEar = it.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR),
                        rightEar = it.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR),
                        leftEye = it.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE),
                        rightEye = it.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE),
                        leftMouth = it.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT),
                        rightMouth = it.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT),
                        bottomMouth = it.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM),
                        leftCheek = it.getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK),
                        rightCheek = it.getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK),
                        noseBase = it.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE))
                landmarkFaces.add(landmarkFace)
            }
        }
        return drawDetection(contourFaces, landmarkFaces)
    }

    private fun drawDetection(contourFaces:List<ContourDetectedFace>,
                              landmarkFaces: List<LandmarkDetectedFace>
    ): Bitmap {
        val bmp = Bitmap.createBitmap(
                cameraHeight,
                cameraWidth,
                Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bmp)
        val paint = Paint()
        paint.strokeWidth = 4f

        contourFaces.forEach {
            with(canvas) {
                paint.color = Color.argb(180, 0, 0, 0)
                paint.style = Paint.Style.FILL
                drawRect(it.boundingBox, paint)

                paint.color = Color.RED
                paint.style = Paint.Style.STROKE
                drawRect(it.boundingBox, paint)

                paint.style = Paint.Style.STROKE
                paint.color = Color.CYAN
                it.faceContourPoints().forEach { pts -> drawLines(pts, paint) }

                paint.color = Color.YELLOW
                it.noseContourPoints().forEach { pts -> drawLines(pts, paint) }

                paint.color = Color.WHITE
                it.eyeContourPoints().forEach { pts -> drawLines(pts, paint) }

                paint.color = Color.MAGENTA
                it.lipContourPoints().forEach { pts -> drawLines(pts, paint) }

                paint.color = Color.LTGRAY
                it.eyeBrowContourPoints().forEach { pts -> drawLines(pts, paint) }
            }
        }

        paint.style = Paint.Style.STROKE
        landmarkFaces.forEach {
            with(canvas) {
                paint.color = Color.RED
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
