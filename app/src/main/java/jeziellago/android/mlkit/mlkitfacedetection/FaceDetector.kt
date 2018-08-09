package jeziellago.android.mlkit.mlkitfacedetection

import android.graphics.Bitmap
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.*

internal class FaceDetector(trackingEnabled: Boolean,
                            private val successListener: OnSuccessListener<in List<FirebaseVisionFace>>,
                            private val failureListener: OnFailureListener) {

    private val detector: FirebaseVisionFaceDetector

    init {
        val detectorOptions = FirebaseVisionFaceDetectorOptions.Builder()
            .setModeType(ACCURATE_MODE)
            .setLandmarkType(ALL_LANDMARKS)
            .setClassificationType(ALL_CLASSIFICATIONS)
            .setMinFaceSize(0.1f)
            .setTrackingEnabled(trackingEnabled)
            .build()

        detector = FirebaseVision.getInstance().getVisionFaceDetector(detectorOptions)
    }

    fun detectFromBitmap(bmp: Bitmap) {
        detect(FirebaseVisionImage.fromBitmap(bmp))
    }

    private fun detect(firebaseVisionImage: FirebaseVisionImage) {
        detector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener)
    }
}