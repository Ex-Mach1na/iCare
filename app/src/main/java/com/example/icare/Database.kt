package com.example.icare

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.icare.model.InjuryInfo
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Database : AppCompatActivity() {
    lateinit var injuryName:EditText
    lateinit var solution:EditText
    lateinit var upload:Button
    var counter:Int = 0
    lateinit var ref:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)

        //val ref = FirebaseDatabase.getInstance().getReference("icare-501ed")


        injuryName = findViewById(R.id.injury_name)
        solution = findViewById(R.id.solution)
        upload = findViewById(R.id.savedata)

        upload.setOnClickListener {
            val injname:String = injuryName.text.toString().trim();
            val soln : String = solution.text.toString();
            uploadData(injname, soln, ++counter)
            //Toast.makeText(applicationContext, "Working button", Toast.LENGTH_SHORT).show()
            injuryName.text.clear();
            solution.text.clear();
        }
    }

    private fun uploadData(injname:String , soln:String, id:Int)
    {

        ref = FirebaseDatabase.getInstance().getReference("Injury")
        val finalData = InjuryInfo(id.toString(), injname, soln)

        ref.child(id.toString()).setValue(finalData)

    }
}