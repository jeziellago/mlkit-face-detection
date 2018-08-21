package jeziellago.android.mlkit.mlkitfacedetection

import android.graphics.Bitmap
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.*

internal class FaceDetector(cameraWidth: Int,
                            cameraHeight: Int,
                            trackingEnabled: Boolean,
                            private val successListener: OnSuccessListener<in List<FirebaseVisionFace>>,
                            private val failureListener: OnFailureListener) {

    private val detector: FirebaseVisionFaceDetector
    private val metadata: FirebaseVisionImageMetadata

    init {
        val detectorOptions = FirebaseVisionFaceDetectorOptions.Builder()
            .setModeType(ACCURATE_MODE)
            .setLandmarkType(ALL_LANDMARKS)
            .setClassificationType(ALL_CLASSIFICATIONS)
            .setMinFaceSize(0.1f)
            .setTrackingEnabled(trackingEnabled)
            .build()

        detector = FirebaseVision.getInstance().getVisionFaceDetector(detectorOptions)

        metadata = FirebaseVisionImageMetadata.Builder()
                .setWidth(cameraWidth)
                .setHeight(cameraHeight)
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_YV12)
                .setRotation(FirebaseVisionImageMetadata.ROTATION_90)
                .build()
    }

    fun detectFromBitmap(bmp: Bitmap) {
        detect(FirebaseVisionImage.fromBitmap(bmp))
    }

    fun detectFromByteArray(byteArray: ByteArray) {
        detect(FirebaseVisionImage.fromByteArray(byteArray, metadata))
    }

    private fun detect(firebaseVisionImage: FirebaseVisionImage) {
        detector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener)
    }
}