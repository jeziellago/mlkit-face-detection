package jeziellago.android.mlkit.mlkitfacedetection

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider.getUriForFile
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    private var faceDetector: FaceDetector? = null
    private var currentImage: Bitmap? = null
    private var currentPath: Uri? = null

    companion object {
        private const val REQUEST_CAPTURE_IMAGE = 100
        private const val REQUEST_CAMERA_PERMISSION = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_capture.setOnClickListener { capture() }
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
            btn_capture.isEnabled = true
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
                drawDetection(detectedFaces, currentImage!!)
            },
            failureListener = OnFailureListener {
                Toast.makeText(this@MainActivity,
                        getString(R.string.error_try_again),
                        Toast.LENGTH_SHORT).show()
            })
    }

    private fun capture() {
        currentPath = createImageFile()
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPath)
        startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE)
    }

    private fun createImageFile(): Uri {
        val imgDir = externalCacheDir
        if (!imgDir.exists()) imgDir.mkdirs()

        val imgFile = File(imgDir, "face.jpg")
        return getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".fileprovider", imgFile)
    }

    private fun detect(bmpImage: Bitmap) {
        image.setImageBitmap(bmpImage)
        currentImage = bmpImage
        faceDetector?.detectFromBitmap(bmpImage)
    }

    private fun drawDetection(faces: ArrayList<DetectedFace>, currentImage: Bitmap) {
        var bmp = Bitmap.createBitmap(
                currentImage.width,
                currentImage.height,
                currentImage.config)

        val canvas = Canvas(bmp)
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 15f

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
                btn_capture.isEnabled = true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            val input = contentResolver.openInputStream(currentPath)
            val bmpImage = BitmapFactory.decodeStream(input)
            detect(bmpImage)
        }
    }

}
