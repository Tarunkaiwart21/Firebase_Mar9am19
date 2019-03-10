package cubex.mahesh.firebase_mar9am19

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_profile.view.*
import kotlinx.android.synthetic.main.fcm_dialog.view.*
import kotlinx.android.synthetic.main.indiview.view.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        var users_ref = FirebaseDatabase.getInstance().
                                getReference("users")
        users_ref.addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(users_ds: DataSnapshot) {
                 var ds_it = users_ds.children
                    var users_list = mutableListOf<User>()
                    ds_it.forEach {
                            var u = User( )
                            var childs_of_uid = it.children
                            childs_of_uid.forEach {
                                when(it.key){
                                    "email" -> { u.email  = it.value.toString() }
                                    "pass" -> { u.pass  = it.value.toString() }
                                    "mno" -> { u.mno  = it.value.toString() }
                                    "gender" -> { u.gender  = it.value.toString() }
                                    "fcm_token" -> { u.fcm_token  = it.value.toString() }
                                    "profile_pic_url" -> { u.profile_pic_url  = it.value.toString() }
                                }
                            }
                        users_list.add(u)
                 }

             dashboard_lview.adapter = object:BaseAdapter()
             {
                 override fun getCount(): Int  = users_list.size

                 override fun getItem(p0: Int): Any  = 0

                 override fun getItemId(p0: Int): Long  = 0

                 override fun getView(pos: Int, p1: View?,
                                      p2: ViewGroup?): View {

                     var inflater = LayoutInflater.from(this@DashboardActivity)
                     var v = inflater.inflate(R.layout.indiview,null)
                     v.email.text = users_list.get(pos).email
                     v.mno.text = users_list.get(pos).mno
                     v.gender.text = users_list.get(pos).gender
                     Glide.with(this@DashboardActivity).
                         load(users_list.get(pos).profile_pic_url).
                         into(v.profile_pic)
                     v.notify.setOnClickListener {
                         var aDialog = AlertDialog.Builder(this@DashboardActivity)
                         aDialog.setTitle("FCM Message")
                         var inflater = LayoutInflater.from(this@DashboardActivity)
                         var v1 = inflater.inflate(R.layout.fcm_dialog,null)
                         aDialog.setView(v1)
                         aDialog.setPositiveButton("Send",
                             { dialogInterface, i ->
                                   sendFcmMessage(users_list.get(pos).fcm_token,v1.et1.text.toString())
                             })
                         aDialog.setNegativeButton("Cancel",
                             { dialogInterface, i ->
                                 dialogInterface.dismiss()
                             })
                         aDialog.setNeutralButton("SendToAll",
                             { dialogInterface, i ->

                                     var templist = mutableListOf<String>()
                                     for (s in users_list){
                                         templist.add(s.fcm_token!!)
                                     }
                                     sendFcmMessageToAll(v1.et1.text.toString(), templist)
                             })
                         aDialog.show()
                     }

                     return  v
                 }
             }

               }
            })


    }







    fun showAlertDialog( )
    {

    } // showAlertDialog


    fun sendFcmMessage(token:String?, msg:String?)
    {
        var jsonObjec: JSONObject? = null
        var bodydata:String = msg!!

        jsonObjec =  JSONObject()
        var list = mutableListOf<String>()
        list.add(token!!)

        var   jsonArray: JSONArray = JSONArray(list)
        jsonObjec.put("registration_ids", jsonArray);
        var jsonObjec2: JSONObject = JSONObject()
        jsonObjec2.put("body", bodydata);
        jsonObjec2.put("title", "Text Message from Android 7AMJan19")
        jsonObjec2.put("fcm_type", "text")
        jsonObjec.put("notification", jsonObjec2);

        jsonObjec.put("time_to_live", 172800);
        jsonObjec.put("priority", "HIGH");

        println("*************")
        print(jsonObjec)
        println("*************")


        val client = OkHttpClient()
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObjec.toString())
        val request = Request.Builder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "key=AAAALjcGAUg:APA91bHHjwTc5vnKJnvd5_iNT3pLKgnlzCSfsJXt54DZgKaLRSXUe7e6-HR7SgbqtW29dd2uRk3EXCE47bMF1rvWBoJYyvIVi32RoGq8wOSfZzezXDMc9_X9OCBFDX-NBCMNXLqqXP5t")
            .url("https://fcm.googleapis.com/fcm/send")
            .post(body)
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {

//                Toast.makeText(this@DashboardActivity,
//                    "Message Sending Success",
//                    Toast.LENGTH_LONG).show()

                Log.i("msg",response!!.body().toString())

            }
            override fun onFailure(call: Call?, e: IOException?) {
//                Toast.makeText(this@DashboardActivity,
//                    "Message Sending Failure",
//                    Toast.LENGTH_LONG).show()

                Log.i("msg","Fail....")

            }
        })
    }

    fun sendFcmMessageToAll(msg:String,fcm_tokens_list:MutableList<String>)
    {
        var bodydata:String = msg

        var  jsonObjec =  JSONObject()

        var   jsonArray: JSONArray = JSONArray(fcm_tokens_list)
        jsonObjec.put("registration_ids", jsonArray);
        var jsonObjec2: JSONObject = JSONObject()
        jsonObjec2.put("body", bodydata);
        jsonObjec2.put("title", "Text Message from FbNov7AM Jan19 ")
        jsonObjec.put("notification", jsonObjec2);

        jsonObjec.put("time_to_live", 172800);
        jsonObjec.put("priority", "HIGH");

        println("*************")
        print(jsonObjec)
        println("*************")


        val client = OkHttpClient()
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObjec.toString())
        val request = Request.Builder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "key=AAAALjcGAUg:APA91bHHjwTc5vnKJnvd5_iNT3pLKgnlzCSfsJXt54DZgKaLRSXUe7e6-HR7SgbqtW29dd2uRk3EXCE47bMF1rvWBoJYyvIVi32RoGq8wOSfZzezXDMc9_X9OCBFDX-NBCMNXLqqXP5t")
            .url("https://fcm.googleapis.com/fcm/send")
            .post(body)
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {

//                Toast.makeText(this@DashboardActivity,
//                    "Message Sending Success",
//                    Toast.LENGTH_LONG).show()

                Log.i("msg",response!!.body().toString())

            }
            override fun onFailure(call: Call?, e: IOException?) {
//                Toast.makeText(this@DashboardActivity,
//                    "Message Sending Failure",
//                    Toast.LENGTH_LONG).show()

                Log.i("msg","Fail....")

            }
        })
    }

}
