package jeziellago.android.mlkit.mlkitfacedetection

import android.Manifest
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.content.FileProvider.getUriForFile


class MainActivity : AppCompatActivity() {

    private var faceDetector: FaceDetector? = null
    private var currentImage: Bitmap? = null
    private var currentPath: Uri? = null

    companion object {
        private const val REQUEST_CAPTURE_IMAGE = 100
        private const val REQUEST_CAMERA_PERMISSION = 123
        private const val IMAGE_DATA = "data"
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
        faceDetector = FaceDetector(
            OnSuccessListener {
                val rects = ArrayList<Rect>()
                it.forEach { rects.add(it.boundingBox) }
                drawBoundingBox(rects, currentImage!!)
            },
            OnFailureListener {
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
        faceDetector?.detect(bmpImage)
    }

    private fun drawBoundingBox(rects: List<Rect>, currentImage: Bitmap) {
        var bmp = Bitmap.createBitmap(
                currentImage.width,
                currentImage.height,
                currentImage.config)

        val canvas = Canvas(bmp)
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f

        rects.forEach { canvas.drawRect(it, paint) }
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
