package com.example.biocubicar.biocubicar.Activity

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.example.biocubicar.biocubicar.Adapter.SlideAdapter
import com.example.biocubicar.biocubicar.R
import com.example.biocubicar.biocubicar.helperDB.BiopilaDao
import com.example.biocubicar.biocubicar.model.BiopilaModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_biopila.*
import kotlinx.android.synthetic.main.activity_calculate_volume.*
import java.io.File
import java.util.*


class BiopilaActivity : AppCompatActivity() {

    private var imagesList = ArrayList<String>()
    private val dao = BiopilaDao()
    private var idBiopila: Int = -1
    private var obj_biopila: BiopilaModel = BiopilaModel(-1, "", "", -.1, -.1, -.1, -.1, -.1, -.1)
    private val REQUEST_PERM_WRITE_STORAGE = 102
    private val REQUEST_PERM_FINE_LOCATION = 1
    private val CAPTURE_PHOTO = 104
    internal var pictureFilePath: String? = ""
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biopila)
        initView()
        delegate()
    }

    override fun onRestart() {
        super.onRestart()
        etVolume.setText(dao.getVolumeDao(idBiopila, this))
    }

    private fun initView() {
        val bundle = intent.extras
        idBiopila = bundle.getInt("ID_BIOPILA")
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        initFieldsView()
        initToolbar()
    }

    fun initFieldsView() {
        if (idBiopila >= 0){
            obj_biopila = dao.getDao(idBiopila, this)
            setValuesObjectToView()
            loadImages()
            loadViewPager()
        } else {
            setValuesObjectToView()
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
            if (idBiopila > 0) {
                takePicture()
            } else {
                Toast.makeText(this, "Primero debe cargar una biopila", Toast.LENGTH_LONG).show()
            }
            true
        }
        R.id.action_broom -> {
            supportActionBar?.title = "Cargar una biopila"
            idBiopila = -1
            cleanFields()
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
        if (cameraIntent.resolveActivity(packageManager) != null) {
            var pictureFile: File? = null
            try {
                pictureFile = getPictureFile()
            } catch (ex: Exception) {
            }

            if (pictureFile != null) {
                var photoURI = FileProvider.getUriForFile(this,
                        "com.fmollea.android.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(cameraIntent, CAPTURE_PHOTO)
            }
        }
    }

    private fun getPictureFile(): File {
        val storageDir = getExternalFilesDir("biopilas")
        val nameImage = "Image-BC-00" + idBiopila + "-" + imagesList.size + ".jpg"
        val image = File(storageDir, nameImage)
        pictureFilePath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, returnIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, returnIntent)

        if (requestCode == CAPTURE_PHOTO && resultCode == Activity.RESULT_OK) {
            val imgFile = File(pictureFilePath)
            if (imgFile.exists()) {
                imagesList.add(Uri.fromFile(imgFile).toString())
                var values = ContentValues()
                values.put("_data", imgFile.absolutePath);
                val cr = getContentResolver();
                cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                loadViewPager()
            }
        }
    }

    fun cleanFields() {
        etTitle.text = null
        etDescription.text = null
        etLatitude.text = null
        etLongitude.text = null
        etVolume.text = null
        etHTP.text = null
        etTemperature.text = null
        etMoisture.text = null
        imagesList.clear()
        loadViewPager()
    }

    /**
     * asigna los valores de la vista al objeto biopila
     */
    fun setValuesViewToObject() {
        if (!etTitle.text.isNullOrEmpty()){
            obj_biopila.title = etTitle.text.toString()
        }
        if (!etDescription.text.isNullOrEmpty()){
            obj_biopila.description = etDescription.text.toString()
        }
        if (!etLatitude.text.isNullOrEmpty()) {
            obj_biopila.latitude = etLatitude.text.toString().toDouble()
        }
        if (!etLongitude.text.isNullOrEmpty()) {
            obj_biopila.longitude = etLongitude.text.toString().toDouble()
        }
        if (!etVolume.text.isNullOrEmpty()) {
            obj_biopila.volume = etVolume.text.toString().toDouble()
        }
        if (!etHTP.text.isNullOrEmpty()) {
            obj_biopila.htp = etHTP.text.toString().toDouble()
        }
        if (!etTemperature.text.isNullOrEmpty()) {
            obj_biopila.temperature = etTemperature.text.toString().toDouble()
        }
        if (!etMoisture.text.isNullOrEmpty()) {
            obj_biopila.moisture = etMoisture.text.toString().toDouble()
        }
    }

    fun setValuesObjectToView() {
        if (!obj_biopila.title.isNullOrEmpty()){
            etTitle.setText(obj_biopila.title)
        }
        if (!obj_biopila.description.isNullOrEmpty()){
            etDescription.setText(obj_biopila.description)
        }
        if (obj_biopila.latitude != -.1){
            etLatitude.setText(obj_biopila.latitude.toString())
        }
        if (obj_biopila.longitude != -.1) {
            etLongitude.setText(obj_biopila.longitude.toString())
        }
        if (obj_biopila.volume != -.1) {
            etVolume.setText(obj_biopila.volume.toString())
        }
        if (obj_biopila.htp != -.1) {
            etHTP.setText(obj_biopila.htp.toString())
        }
        if (obj_biopila.temperature != -.1) {
            etTemperature.setText(obj_biopila.temperature.toString())
        }
        if (obj_biopila.moisture != -.1) {
            etMoisture.setText(obj_biopila.moisture.toString())
        }
    }

    /**
     * Carga el viewPager de la vista
     */
    fun loadViewPager() {
        viewPager.adapter = SlideAdapter(this, imagesList)
    }

    fun loadImages() {
        val myDir = getExternalFilesDir("biopilas")
        var nameImage = "Image-BC-00" + idBiopila + "-" + imagesList.size + ".jpg"
        var file = File(myDir, nameImage)

        while (file.exists()) {
            imagesList.add(Uri.fromFile(file).toString())
            nameImage = "Image-BC-00" + obj_biopila.id + "-" + imagesList.size + ".jpg"
            file = File(myDir, nameImage)
        }
    }

    fun deleteImages(id_biopila: Int) {
        var index = 0
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File(root + "/biopilas")
        var nameImage = "Image-BC-00" + id_biopila + "-" + index + ".jpg"
        var file = File(myDir, nameImage)

        while (file.exists()) {
            file.delete()
            index++
            nameImage = "Image-BC-00" + id_biopila + "-" + index + ".jpg"
            file = File(myDir, nameImage)
        }
    }

    fun checkTitle() : Boolean = etTitle.text.isNullOrEmpty()

    fun addBiopila() {
        if (checkTitle()) {
            Toast.makeText(this, "Debe ingresar el nombre de la biopila.", Toast.LENGTH_SHORT).show()
        } else {
            var messageToast = ""
            setValuesViewToObject()
            if (dao.insertDao(obj_biopila, this)) {
                idBiopila = dao.getIdDao(obj_biopila.title, obj_biopila.description, this)
                messageToast = "Se agregó la biopila " + obj_biopila.title + " correctamente."
            } else {
                messageToast = "Ocurrió un error al agregar la biopila " + obj_biopila.title + "."
            }

            Toast.makeText(this, messageToast, Toast.LENGTH_SHORT).show()
        }

    }

    fun updateBiopila() {
        var messageToast = ""

        if (idBiopila >= 0) {
            setValuesViewToObject()
            if (dao.updateDao(idBiopila, obj_biopila, this)) {
                messageToast = "La biopila "+ obj_biopila.title+ " fue editada correctamente."
            } else {
                messageToast = "Ocurrió un error al editar la biopila " + obj_biopila.title + "."
            }

        } else {
            messageToast = "Debe seleccionar la biopila que desea editar"
        }
        Toast.makeText(this, messageToast, Toast.LENGTH_SHORT).show()
    }

    fun deleteBiopila() {
        var messageToast = ""

        if (idBiopila >= 0) {
            if (dao.deleteDao(idBiopila, this)) {
                messageToast = "Se eliminó la biopila " + obj_biopila.title + " correctamente."
                deleteImages(idBiopila)
                idBiopila = -1
                obj_biopila = BiopilaModel(-1, "", "", -.1, -.1, -.1, -.1,-.1,-.1)
                cleanFields()
                supportActionBar?.title = "Cargar una biopila"
            } else {
                messageToast = "Ocurrió un error al eliminar la biopila " + obj_biopila.title + "."
            }
        } else {
            messageToast = "Debe seleccionar la biopila que desea eliminar"
        }

        Toast.makeText(this, messageToast, Toast.LENGTH_SHORT).show()
    }

    fun initViewCalculateVolume() {
        if (idBiopila >= 0) {
            val intent = Intent(this, CalculateVolume::class.java)
            intent.putExtra("ID_BIOPILA", idBiopila)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Primero debe cargar una biopila", Toast.LENGTH_LONG).show()
        }
    }

    fun getLocalitation() {
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this@BiopilaActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERM_FINE_LOCATION)
        } else {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    obj_biopila.latitude = location.latitude;
                    obj_biopila.longitude = location.longitude
                    etLatitude.setText(location.latitude.toString())
                    etLongitude.setText(location.longitude.toString())
                }
            }
        }
    }

}
