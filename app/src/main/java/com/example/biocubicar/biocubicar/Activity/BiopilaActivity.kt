package com.example.biocubicar.biocubicar.Activity

import android.support.v4.view.PagerAdapter
import com.example.biocubicar.biocubicar.Adapter.SlideAdapter
import com.example.biocubicar.biocubicar.R
import android.view.Menu
import android.widget.Toast
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_biopila.*
import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import java.nio.file.Files.exists
import java.io.File.separator
import android.os.Environment.getExternalStorageDirectory
import java.io.File
import java.io.FileOutputStream
import java.util.*
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.opengl.ETC1
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import com.example.biocubicar.biocubicar.model.BiopilaModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class BiopilaActivity : AppCompatActivity() {

    lateinit var images : Array<Int>
    lateinit var adapter : PagerAdapter //= SlideAdapter(applicationContext, images) //TODO lateinit
    var idBiopila : Int = -1
    var obj_biopila : BiopilaModel = BiopilaModel(-1, "", "", -1.0,-1.0,-1.0, mutableListOf())
    val REQUEST_PERM_WRITE_STORAGE = 102
    private val CAPTURE_PHOTO = 104
    internal var imagePath: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biopila)
        initFieldsView()
        initToolbar()
        delegate()
    }

    fun initFieldsView() {
        if (idBiopila >= 0){

        } else {
            etTitle.setText("")
            etDescription.setText("")
            etLatitude.setText("0")
            etLongitude.setText("0")
            etVolume.setText("0")
        }
    }

    fun initToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar_biopila))
        //home navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_nav_back)
        if (idBiopila >= 0) {
            supportActionBar?.title = "Información de la biopila"
        } else {
            supportActionBar?.title = "Cargar una biopila"
        }
    }

    fun delegate() {
        toolbar_biopila.setNavigationOnClickListener {
            View -> onBackPressed()
        }

        btAdd.setOnClickListener {
            addBiopila()
        }

        btEdit.setOnClickListener {
            updateBiopila()
        }

        btDelete.setOnClickListener {
            deleteBiopila()
        }

        btLocalitation.setOnClickListener {
            getLocalitation()
        }

        btCalculate.setOnClickListener{
            initViewCalculateVolume()
        }
    }

    /**
     * setting menu in action bar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_biopila, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * actions on click menu items
     */
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_camera -> {
            takePicture()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    //Métodos para sacar y guardar fotos
    private fun takePicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@BiopilaActivity, arrayOf(Manifest.permission.CAMERA), 1)
            }

        }
        if (ActivityCompat.checkSelfPermission(applicationContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this@BiopilaActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERM_WRITE_STORAGE)
        } else {
            takePhotoByCamera()
        }
    }

    private fun takePhotoByCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAPTURE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, returnIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, returnIntent)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAPTURE_PHOTO -> {
                    val capturedBitmap = returnIntent!!.extras!!.get("data") as Bitmap
                    saveImage(capturedBitmap)
                } else -> {
                }
            }
        }
    }

    private fun saveImage(finalBitmap: Bitmap) {

        val root = getExternalStorageDirectory().toString()
        val myDir = File(root + "/biopilas")
        myDir.mkdirs()
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val outletFname = "Image-$n.jpg"
        val file = File(myDir, outletFname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            imagePath = file.absolutePath
            obj_biopila.images.add(file.absolutePath) //Se guarda la url de la imagen en el objeto biopila.
            out.flush()
            out.close()

            //Se agregan valores para que aparezca en todas las galerias
            val values = ContentValues();
            values.put(MediaStore.Images.Media.TITLE, getTitleForImage());
            values.put(MediaStore.Images.Media.DESCRIPTION, getDescriptionForImage());
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase(Locale.getDefault()).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.getDefault()));
            values.put("_data", file.getAbsolutePath());
            val cr = getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTitleForImage() : String = etTitle.text.toString()

    private fun getDescriptionForImage() : String = etDescription.text.toString()

    /**
     * limpia los campos de la vista
     */
    fun cleanFields() {
        etTitle.setText("")
        etDescription.setText("")
        etLatitude.setText("0")
        etLongitude.setText("0")
        etVolume.setText("0")
        loadViewPager()
    }

    /**
     * asigna los valores de la vista al objeto biopila
     */
    fun setValuesOfFields() {
        obj_biopila.title = etTitle.text.toString()
        obj_biopila.description = etDescription.text.toString()
        obj_biopila.latitude = etLatitude.text.toString().toDouble()
        obj_biopila.longitude = etLongitude.text.toString().toDouble()
        obj_biopila.volume = etVolume.text.toString().toDouble()
    }

    /**
     * Carga el viewPager de la vista
     */
    fun loadViewPager() {

    }

    fun checkTitle() : Boolean = etTitle.text.isNullOrEmpty()

    fun addBiopila() {
        var messageToast = ""
        try {
            setValuesOfFields()
            //todo guardar
            messageToast = "Se agregó la biopila "+ obj_biopila.title + " correctamente."

        } catch (ex : Exception) {
            messageToast = "Ocurrió un error al agregar la biopila "+ obj_biopila.title + "."
        }

        Toast.makeText(this, messageToast, Toast.LENGTH_SHORT).show()
        cleanFields()
        obj_biopila = BiopilaModel(-1, "", "", -1.0, -1.0, -1.0, mutableListOf())
    }

    fun updateBiopila() {
        var messageToast = ""
        try {
            if (idBiopila >= 0) {
                setValuesOfFields()
                //TODO editar
                messageToast = "La biopila "+ obj_biopila.title+ " fue editada correctamente."
            } else {
                messageToast = "Debe seleccionar la biopila que desea editar"
            }
        } catch (ex : Exception) {
            messageToast = "Ocurrió un error al editar la biopila "+ obj_biopila.title + "."
        }

        Toast.makeText(this, messageToast, Toast.LENGTH_SHORT).show()
    }

    fun deleteBiopila() {
        var messageToast = ""
        try {
            if (idBiopila >= 0){
                //TODO eliminar
            } else {
                messageToast = "Debe seleccionar la biopila que desea eliminar"
            }
        } catch (ex : Exception) {
            messageToast = "Ocurrió un error al agregar la biopila "+ obj_biopila.title + "."
        }

        Toast.makeText(this, messageToast, Toast.LENGTH_SHORT).show()
        cleanFields()
        obj_biopila = BiopilaModel(-1, "", "", -1.0, -1.0, -1.0, mutableListOf())
    }

    fun initViewCalculateVolume() {
        val intent = Intent(this, CalculateVolume::class.java)
        startActivity(intent)
    }

    fun getLocalitation() {

    }

}
