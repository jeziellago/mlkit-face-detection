package jeziellago.android.mlkit.mlkitfacedetection

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var faceDetector: FaceDetector? = null
    private var currentImage: Bitmap? = null

    companion object {
        private const val REQUEST_CAPTURE_IMAGE = 100
        private const val IMAGE_DATA = "data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_capture.setOnClickListener { capture() }
        setupFaceDetector()
    }

    private fun setupFaceDetector() {
        faceDetector = FaceDetector(
            OnSuccessListener {
                it.forEach {
                    drawBoundingBox(it.boundingBox, currentImage!!)
                }
            },
            OnFailureListener {
                Toast.makeText(this@MainActivity,
                        getString(R.string.error_try_again),
                        Toast.LENGTH_SHORT).show()
            })
    }

    private fun capture() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE)
    }

    private fun drawBoundingBox(rect: Rect, currentImage: Bitmap) {

        var bmp = Bitmap.createBitmap(
                currentImage.width,
                currentImage.height,
                currentImage.config)

        val canvas = Canvas(bmp)
        val paint = Paint()
        paint.color = Color.YELLOW
        paint.style = Paint.Style.STROKE
        canvas.drawRect(rect, paint)
        overlay.setImageBitmap(bmp)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.extras != null) {
                val bmpImage = data.extras.get(IMAGE_DATA) as Bitmap
                currentImage = bmpImage
                image.setImageBitmap(bmpImage)
                faceDetector?.detect(bmpImage)
            }
        }
    }

}
