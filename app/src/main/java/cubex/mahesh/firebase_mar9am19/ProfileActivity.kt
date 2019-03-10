package cubex.mahesh.firebase_mar9am19

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profile_iview.setOnClickListener {
            var aDialog = AlertDialog.Builder(this@ProfileActivity)
            aDialog.setTitle("Message")
            aDialog.setMessage("Take a photo/select a file for adding an attachment")
            aDialog.setPositiveButton("Camera",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    var i = Intent( )
                    i.setAction("android.media.action.IMAGE_CAPTURE")
                    startActivityForResult(i,123)
                })
            aDialog.setNegativeButton("File Explorer",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    var i = Intent( )
                    i.setAction(Intent.ACTION_GET_CONTENT)
                    i.setType("*/*")
                    startActivityForResult(i,124)
                })
            aDialog.setNeutralButton("Cancel",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
            aDialog.show()

        }
    } // onCreate( )

    var uri:Uri? = null
    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 123 && resultCode == Activity.RESULT_OK)
        {
            var bmp = data?.extras?.get("data") as Bitmap
            uri = getImageUri(this@ProfileActivity, bmp)
            profile_iview.setImageURI(uri)
            upload(uri)
        }else  if(requestCode == 124 && resultCode == Activity.RESULT_OK)
        {
            uri = data?.data
            profile_iview.setImageURI(uri)
            upload(uri)
        }

    }  // onActivityResult( )

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null)
        return Uri.parse(path)
    }

    fun upload(uri:Uri?)
    {
        var sRef = FirebaseStorage.getInstance().
            getReference("profile_pics")
       var child_ref = sRef.child(FirebaseAuth.getInstance().uid.toString())
       var file_ref =    child_ref.child("profile_pic.png").putFile(uri!!)
       file_ref.addOnSuccessListener {
           var file_url = it.downloadUrl.toString()
           var dBase = FirebaseDatabase.getInstance()
           var dRef = dBase.getReference("users")
           var uid = FirebaseAuth.getInstance().uid
           var child_db_dRef = dRef.child(uid.toString())
           child_db_dRef.child("profile_pic_url").
               setValue(file_url)
       }
    }  //upload
}
