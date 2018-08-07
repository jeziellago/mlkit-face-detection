package jeziellago.android.mlkit.mlkitfacedetection

import android.graphics.Bitmap
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace

internal class FaceDetector(private val successListener: OnSuccessListener<in List<FirebaseVisionFace>>,
                            private val failureListener: OnFailureListener) {

    private val detector = FirebaseVision.getInstance().visionFaceDetector

    fun detect(bmp: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bmp)
        detector.detectInImage(image)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener)
    }
}