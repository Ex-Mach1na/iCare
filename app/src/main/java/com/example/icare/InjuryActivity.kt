package com.example.icare

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.example.icare.databinding.ItemMessageReceiveBinding
import com.example.icare.databinding.ItemMessageSendBinding
import com.example.icare.model.Message
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
        /**val ref:DatabaseReference = FirebaseDatabase.getInstance().getReference("Injuries/temp")

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

        })**/



        val sendButtonInj: Button = findViewById(R.id.sendButtonInj)
        val editTextInj : AppCompatAutoCompleteTextView = findViewById(R.id.editTextInj)

        val values = arrayOf("ABC","Nothing","Anything","Banana","Book","Heart","Help","Happy","Live","Forever")

        val newAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, values)
        editTextInj.threshold = 1
        editTextInj.setAdapter(newAdapter)

        injuryRecycleV.adapter = messageAdapter

        receiveAutoResponse( getString(R.string.start_msg_injury) )
        sendButtonInj.setOnClickListener {
            val message = Message(editTextInj.text.toString(), "me")

            val sendMessageItem = SendMessageItem(message)
            messageAdapter.add(sendMessageItem)
            editTextInj.text.clear()

            receiveAutoResponse(message.msg)
        }
    }


    private fun receiveAutoResponse(temp: String){

        var finalString:String = "Sorry. I didn't understand. Can you repeat?"

        if(temp.equals(getString(R.string.start_msg_injury)))
            finalString = temp
        if(startOfChat(temp))
            finalString = getString(R.string.start_msg_injury)
        if(endOfChat(temp))
            finalString = getString(R.string.end_of_conversation)

        GlobalScope.launch(Dispatchers.Main) {
            delay(500)
            val receive = Message(
                msg = finalString, sendby = "me"
            )
            val receiveItem = ReceiveMessageItem(receive)

            messageAdapter.add(receiveItem)
        }
    }

    private fun endOfChat (msg: String) : Boolean
    {
        val terminateCommands:Array<String> = arrayOf("ok", "thanks", "thank you", "bye")

        for(i in terminateCommands)
        {
            if(msg.equals(i, true))
            {
                return true
            }
        }
        return false
    }

    private fun startOfChat (msg: String) : Boolean
    {
        val startCommands:Array<String> = arrayOf("Hello", "Hi", "Oi", "Can you help?", "can you help")

        for(i in startCommands)
        {
            if(msg.equals(i, true))
                return true
        }
        return false
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