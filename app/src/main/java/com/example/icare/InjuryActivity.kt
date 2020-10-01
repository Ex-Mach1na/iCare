package com.example.icare

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.example.icare.databinding.ItemMessageReceiveBinding
import com.example.icare.databinding.ItemMessageSendBinding
import com.example.icare.model.InjuryInfo
import com.example.icare.model.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.databinding.BindableItem
import kotlinx.android.synthetic.main.activity_injury.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class InjuryActivity : AppCompatActivity() {

    private val messageAdapter = GroupAdapter<GroupieViewHolder>()
    lateinit var sendButtonInj:Button
    lateinit var editTextInj : AppCompatAutoCompleteTextView
    lateinit var injuryInfo: InjuryInfo
    lateinit var allInjuryInfo: MutableList<InjuryInfo>
    var flag:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_injury)

        getValueFromDatabase()

        val values = arrayOf("ABC","Nothing","Anything","Banana","Book","Heart","Help","Happy","Live","Forever")

        val newAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, values)

        injuryRecycleV.adapter = messageAdapter

        flag = 1
        magic()
    }

    private fun magic()
    {
        //Do the magic
        sendButtonInj = findViewById(R.id.sendButtonInj)
        editTextInj = findViewById(R.id.editTextInj)

        if(flag == 1)
        {
            receiveAutoResponse( "start" , flag)
        }


        sendButtonInj.setOnClickListener {
            val message = Message(editTextInj.text.toString(), "me")
            val sendMessageItem = SendMessageItem(message)
            messageAdapter.add(sendMessageItem)
            editTextInj.text.clear()

            //receiveAutoResponse(message.msg)
        }
    }

    private fun getValueFromDatabase()
    {

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        val ref = FirebaseDatabase.getInstance().getReference("Injury")
        allInjuryInfo = mutableListOf()

        Log.d("Fun", "getValueFromDatabase working")
        
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    for(i in snapshot.children)
                    {
                        val temp = i.getValue(InjuryInfo::class.java)
                        allInjuryInfo.add(temp!!)
                    }
                }
                else
                    Toast.makeText(applicationContext, "Error!! Database Not Found.", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun receiveAutoResponse(temp: String, flag :Int){

        var finalString:String = "Sorry. I didn't understand. Can you repeat?"
        Log.d("tag", finalString)
        if(flag == 1 || startOfChat(temp))
        {
            finalString = getString(R.string.start_msg_injury)
            /*finalString += "\n"
            for(i in allInjuryInfo)
            {
                finalString += i.Name
                finalString += '\n'
            }*/
        }
        if(endOfChat(temp))
            finalString = getString(R.string.end_of_conversation)

        GlobalScope.launch(Dispatchers.Main) {

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