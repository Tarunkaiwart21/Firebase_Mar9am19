package cubex.mahesh.firebase_mar9am19

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        reg_bt1.setOnClickListener {
                var dBase = FirebaseDatabase.getInstance()
                var dRef = dBase.getReference("users")
                var uid = FirebaseAuth.getInstance().uid
                var child_dRef = dRef.child(uid.toString())
            child_dRef.child("email").
                setValue(reg_et1.text.toString())
            child_dRef.child("pass").
                setValue(reg_et2.text.toString())
            child_dRef.child("mno").
                setValue(reg_et3.text.toString())
            child_dRef.child("gender").
                setValue(reg_et4.text.toString())
            child_dRef.child("fcm_token").
                setValue(FirebaseInstanceId.getInstance().token)

            startActivity(Intent(this@RegistrationActivity,
                ProfileActivity::class.java))

        }
    }
}
