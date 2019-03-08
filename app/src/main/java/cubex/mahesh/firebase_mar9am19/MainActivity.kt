package cubex.mahesh.firebase_mar9am19

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // FirebaseApp.initializeApp(this@MainActivity)
        var fAuth = FirebaseAuth.getInstance()
      // login
        login_bt1.setOnClickListener {
            fAuth.signInWithEmailAndPassword(
                login_et1.text.toString(), login_et2.text.toString()
            ).addOnCompleteListener {
                if(it.isSuccessful)
                    startActivity(Intent(this@MainActivity,
                        DashboardActivity::class.java))
                else
                    Toast.makeText(this@MainActivity,
                        "Auth Failed",
                        Toast.LENGTH_LONG).show()
            }
        }
           // register
        login_bt2.setOnClickListener {
               fAuth.createUserWithEmailAndPassword(
                   login_et1.text.toString(), login_et2.text.toString()
               ).addOnCompleteListener {
                        if(it.isSuccessful)
                startActivity(Intent(this@MainActivity,
                                RegistrationActivity::class.java))
                        else
                Toast.makeText(this@MainActivity,"Auth Failed",
                                Toast.LENGTH_LONG).show()
               }
        }

    }
}
