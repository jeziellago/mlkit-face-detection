package jeziellago.android.mlkit.mlkitfacedetection

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.Facing
import com.otaliastudios.cameraview.Frame
import com.otaliastudios.cameraview.SessionType
import jeziellago.android.mlkit.mlkitfacedetection.detect.DetectionViewer
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var faceDetector: FaceDetector? = null
    private var detectionViewer: DetectionViewer? = null

    private var cameraWidth: Int = 0
    private var cameraHeight: Int = 0
    private var isLoadingDetection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkCameraPermission()

        with(camera_preview) {
            facing = Facing.FRONT
            scaleX = -1f
            addFrameProcessor { if (!isLoadingDetection) detect(it) }
        }

    }

    private fun detect(frame: Frame) {
        if (cameraWidth > 0 && cameraHeight > 0) {
            faceDetector?.detectFromByteArray(frame.data)
            isLoadingDetection = true
        } else {
            cameraWidth = frame.size.width
            cameraHeight = frame.size.height
            setupFaceDetector()
        }
    }

    private fun setupFaceDetector() {
        faceDetector = FaceDetector(
            cameraWidth = cameraWidth,
            cameraHeight = cameraHeight,
            successListener = OnSuccessListener {
                val bmp = detectionViewer?.showDetection(it)
                overlay.setImageBitmap(bmp)
                isLoadingDetection = false
            },
            failureListener = OnFailureListener {
                Toast.makeText(this@MainActivity,
                    getString(R.string.error_in_detection),
                    Toast.LENGTH_SHORT
                ).show()
            })

        detectionViewer = DetectionViewer(cameraWidth, cameraHeight)
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE),
                    REQUEST_CAMERA_PERMISSION)
        } else {
            camera_preview.start()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED
                    && grantResults[1] == PERMISSION_GRANTED) {
                camera_preview.start()
            }
        }
    }

    companion object { private const val REQUEST_CAMERA_PERMISSION = 123 }
}
