package com.example.icare

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.example.icare.databinding.ItemMessageReceiveBinding
import com.example.icare.databinding.ItemMessageSendBinding
import com.example.icare.model.Message
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.databinding.BindableItem
import kotlinx.android.synthetic.main.activity_injury.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class InjuryActivity : AppCompatActivity() {

    private val messageAdapter = GroupAdapter<GroupieViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_injury)

        //Database
        val ref:DatabaseReference = FirebaseDatabase.getInstance().getReference("Injuries/temp")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(applicationContext, "Database Found", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(applicationContext, "Not found", Toast.LENGTH_SHORT).show()
                }

                for(i in snapshot.children){


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })





        val sendButtonInj: Button = findViewById(R.id.sendButtonInj)
        val editTextInj : AppCompatAutoCompleteTextView = findViewById(R.id.editTextInj)

        val values = arrayOf("ABC","Nothing","Anything","Banana","Book","Heart","Help","Happy","Live","Forever")
        val newAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, values)
        editTextInj.threshold = 0
        editTextInj.setAdapter(newAdapter)

        injuryRecycleV.adapter = messageAdapter
        populateData()

        recieveAutoResponse("How are you feeling today?")
        sendButtonInj.setOnClickListener {
            val message = Message(editTextInj.text.toString(), "me"
            )
            val sendMessageItem = SendMessageItem(message)
            messageAdapter.add(sendMessageItem)
            editTextInj.text.clear()

            recieveAutoResponse(message.msg)
        }
    }

    fun populateData(){
        val data = listOf<Message>()
        data.forEach{
            if(it.sendby == "me")
            {
                messageAdapter.add(SendMessageItem(it))
            }
            else
                messageAdapter.add(ReceiveMessageItem(it))
        }
    }

    private fun recieveAutoResponse(temp: String){
        GlobalScope.launch(Dispatchers.Main) {
            delay(500)
            val receive = Message(
                msg = temp, sendby = "me"
            )
            val receiveItem = ReceiveMessageItem(receive)

            messageAdapter.add(receiveItem)
        }
    }
}

class SendMessageItem(private val message: Message) : BindableItem<ItemMessageSendBinding>(){
    override fun getLayout(): Int {
        return R.layout.item_message_send
    }

    override fun bind(viewBinding: ItemMessageSendBinding, position: Int) {
        viewBinding.message = message
    }
}

class ReceiveMessageItem(private val message: Message) : BindableItem<ItemMessageReceiveBinding>(){
    override fun getLayout(): Int {
        return R.layout.item_message_receive
    }

    override fun bind(viewBinding: ItemMessageReceiveBinding, position: Int) {
        viewBinding.message = message
    }
}