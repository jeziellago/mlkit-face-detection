package jeziellago.android.mlkit.mlkitfacedetection

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.camera2.CameraDevice
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark.*
import com.otaliastudios.cameraview.SessionType
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var faceDetector: FaceDetector? = null
    private var loadingDetection = false

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupFaceDetector()
        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CAMERA_PERMISSION)
        } else {
            camera_preview.start()
        }
    }

    private fun setupFaceDetector() {
        faceDetector = FaceDetector( trackingEnabled = true,
            successListener = OnSuccessListener { faces ->
                val detectedFaces = ArrayList<DetectedFace>()
                faces.forEach { face ->
                    val detectedFace = DetectedFace(
                        boundingBox = face.boundingBox,
                        leftEar = face.getLandmark(LEFT_EAR),
                        rightEar = face.getLandmark(RIGHT_EAR),
                        leftEye = face.getLandmark(LEFT_EYE),
                        rightEye = face.getLandmark(RIGHT_EYE),
                        leftMouth = face.getLandmark(LEFT_MOUTH),
                        rightMouth = face.getLandmark(RIGHT_MOUTH),
                        bottomMouth = face.getLandmark(BOTTOM_MOUTH),
                        leftCheek = face.getLandmark(LEFT_CHEEK),
                        rightCheek = face.getLandmark(RIGHT_CHEEK),
                        noseBase = face.getLandmark(NOSE_BASE)
                    )
                    detectedFaces.add(detectedFace)
                }
                drawDetection(detectedFaces)
                loadingDetection = false
            },
            failureListener = OnFailureListener {
                Toast.makeText(this@MainActivity,
                        getString(R.string.error_try_again),
                        Toast.LENGTH_SHORT).show()
            })

        camera_preview.addFrameProcessor {
            if (!loadingDetection) {
                detect(it.data)
            }
        }
    }

    private fun detect(data: ByteArray) {
        loadingDetection = true
        faceDetector?.detectFromByteArray(data)
    }

    private fun drawDetection(faces: ArrayList<DetectedFace>) {
        val width = 960
        val height = 1280
        var bmp = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bmp)
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f

        faces.forEach {
            canvas.drawRect(it.boundingBox, paint)
            canvas.drawPoints(it.earsPoints(), paint)
            canvas.drawPoints(it.cheekPoints(), paint)
            canvas.drawPoints(it.eyesPoints(), paint)
            canvas.drawPoints(it.mouthPoints(), paint)
            canvas.drawPoints(it.nosePoints(), paint)
        }
        overlay.setImageBitmap(bmp)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                camera_preview.start()
            }
        }
    }
}
