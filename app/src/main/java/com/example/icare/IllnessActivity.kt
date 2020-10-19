package com.example.icare

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.example.icare.model.Message
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_illness.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IllnessActivity : AppCompatActivity() {

    private val messageAdapter = GroupAdapter<GroupieViewHolder>()
    lateinit var sendButtonIll: Button
    lateinit var editTextIll : AppCompatAutoCompleteTextView
    private val terminateCommands:Array<String> = arrayOf("ok", "thanks", "thank you", "bye")
    val startCommands:Array<String> = arrayOf("Hello", "Hi", "Oi", "Can you help?", "can you help")
    val startConvo:Array<String> = arrayOf("Check symptoms", "Give Medication", "Show Disease", "Show Symptoms")
    var flag: Int = 0
    private var failure: Boolean = false
    private lateinit var answerArray : Array<String>
    private lateinit var symptomArray : Array<String>
    private lateinit var diseaseArray : Array<String>
    private lateinit var answerList : MutableList<String>
    private lateinit var results: MutableList<Int>

    lateinit var symptomsAdapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_illness)

        illnessRecycleV.adapter = messageAdapter
        sendButtonIll = findViewById(R.id.sendButtonIll)
        editTextIll = findViewById(R.id.editTextIll)
        answerArray = resources.getStringArray(R.array.IllnessAnswer)
        symptomArray = resources.getStringArray(R.array.symptoms)
        diseaseArray = resources.getStringArray(R.array.disease)
        answerList = mutableListOf()
        results = mutableListOf()

        Magic()
    }


    private fun Magic()
    {
        if(flag == 0)
        {
            receiveAutoResponse(flag)
            flag =1
            startConversation()
         }
        else if(flag > 0)
        {

        }
    }

    private fun startConversation()
    {
        var options: Int = -1
        var ans: String = ""
        var match : Boolean = false


        receiveAutoResponse(getString(R.string.askSymptom))

        val initAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, startConvo)
        editTextIll.setAdapter(initAdapter)
        editTextIll.threshold = 1

        sendButtonIll.setOnClickListener{

            ans = editTextIll.text.toString().toLowerCase()

            val message = Message(ans, "me")
            val sendMessageItem = SendMessageItem(message)
            messageAdapter.add(sendMessageItem)
            editTextIll.text.clear()

            externalCommand(ans)

            if(match && options==1)
            {
                if(isDisease(ans))
                    solution(ans)
            }

            if(options == -1)
           {
               if(ans.equals(startConvo[0], true))
               {
                   options = 1
                   receiveAutoResponse("Tell me your symptoms")
                   val symptomsAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, symptomArray)
                   editTextIll.setAdapter(symptomsAdapter)
                   editTextIll.threshold = 1
                   //check Symptom
               }
               else
               {
                   //show medication
                   options = 0
                   receiveAutoResponse("What illness do you have?")
                   val diseaseAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, diseaseArray)
                   editTextIll.setAdapter(diseaseAdapter)
                   editTextIll.threshold = 1
               }
           }
           else if(options == 0)
           {
               //get illness name and show meds
               if(isDisease(ans))
                   solution(ans)
           }
           else if(options == 1)
           {
               if(!ans.equals("nothing else",true))
                   receiveAutoResponse("Is there anything else?")
               if(ans.equals("nothing else",true))
                {
                    //do something
                    findMatch()

                    if(results.isNotEmpty())
                    {
                        showResult()
                        match = true
                        receiveAutoResponse("Write the name of the disease to get solution")
                        val diseaseAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, diseaseArray)
                        editTextIll.setAdapter(diseaseAdapter)
                        editTextIll.threshold = 1
                    }
                    else
                    {
                        receiveAutoResponse("Sorry. Please go see a doctor. I can't help you.")
                    }
                }
               else
                {
                    answerList.add(ans)
                }
           }





        }


        /*val symptomAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, symptomArray)
        editTextIll.setAdapter(symptomAdapter)
        editTextIll.threshold = 1*/





    }

    private fun findMatch()
    {
        for(i in answerList)
            Log.d("temp", "anslist = ${i.toString()}")

        var migraine = arrayOf("headache", "vomiting")
        var pneumonia = arrayOf("dry cough", "rapid heartbeat", "breathing difficulty", "fever", "loss of appetite", "chest pain")
        var insomnia = arrayOf("stress", "anxiety", "less sleep","tiredness")
        var dehydration = arrayOf("dizziness", "tiredness", "dry mouth")

        var migrainCount: Int = 0
        var pneumoniaCount: Int = 0
        var insomniaCount: Int = 0
        var dehydrationCount: Int = 0

        for(i in answerList)
        {
            for(j in migraine)
            {
                if(i.equals(j,true))
                    migrainCount++
            }

            for(j in pneumonia)
            {
                if(i.equals(j,true))
                    pneumoniaCount++
            }

            for(j in insomnia)
            {
                if(i.equals(j,true))
                    insomniaCount++

            }

            for(j in dehydration)
            {
                if(i.equals(j,true))
                    dehydrationCount++
            }
        }

        Log.d("flag", "migrain = ${migrainCount}")
        Log.d("flag", "pneumonia = ${pneumoniaCount}")
        Log.d("flag", "insomonia = ${insomniaCount}")

        results.add(((migrainCount/2)*100))
        results.add(((pneumoniaCount/6)*100))
        results.add(((insomniaCount/4)*100))
        results.add(((dehydrationCount/3)*100))
    }

    private fun showResult()
    {
        var resultMessage:String = "Here what I have for you\n"
        var count: Int = 0
        for(i in results)
        {
            resultMessage = resultMessage + diseaseArray[count].toString() + ": " + i.toString() +'%' + '\n'
            count++
        }
        receiveAutoResponse(resultMessage)
    }

    private fun isDisease(name: String) : Boolean
    {
        for (i in diseaseArray)
        {
            if (i.equals(name, true))
                return true
        }
        return false
    }
    private fun solution(name: String)
    {
        if( name.equals("migraine",true))
            receiveAutoResponse(getString(R.string.migrain))
        if (name.equals("pneumonia",true))
            receiveAutoResponse(getString(R.string.pneumonia))
        if (name.equals("insomnia",true))
            receiveAutoResponse(getString(R.string.insomnia))
        if (name.equals("dehydration",true))
            receiveAutoResponse(getString(R.string.dehydration))

    }

    private fun externalCommand(command: String)
    {
        if (command.equals("show symptoms",true))
        {
            var allSymptomps:String = ""
            for (i in symptomArray)
            {
                allSymptomps += i
                allSymptomps += "\n"
            }
            receiveAutoResponse(allSymptomps)
        }
        if (command.equals("show disease",true))
        {
            var allDisease:String = ""
            for (i in diseaseArray)
            {
                allDisease += i
                allDisease += "\n"
            }
            receiveAutoResponse(allDisease)
        }
    }



    /**private fun startConversation()
    {
        var isRootFound : Boolean = false
        var reply : Int


        var count : Int = 0
        var root : Int = 0
        var ans : String = ""
        //Conversation initiated
        sendButtonIll.setOnClickListener{

            ans = editTextIll.text.toString()
            count++
            Log.d("Count value", count.toString())

            val message = Message(ans, "me")
            val sendMessageItem = SendMessageItem(message)
            messageAdapter.add(sendMessageItem)
            editTextIll.text.clear()

            //Looking for root tree
            //init root 1
            if(!isRootFound)
            {
                receiveAutoResponse("Do you have a Headache?")
                val hasHeadache = getResponse()
                if(hasHeadache)
                {
                    isRootFound = true
                    reply = -1
                    //TODO: Implement tree 1
                    receiveAutoResponse("Is the pain in one side of the head?")
                    if(reply == 1)
                    {
                        //one sided pain
                        receiveAutoResponse("Are you vomitting?")
                        reply
                    }
                    else if(reply == 0)
                    {

                    }
                    else if(reply == 2)
                    {

                    }


                }
            }


        }


        //init root 2
        /*if(!isRootFound)
        {

            if(reply == 1)
            {
                isRootFound = true
                //TODO: Implement tree 2
            }
        }

        //init root 3
        if(!isRootFound)
        {

            if(reply == 1)
            {
                isRootFound = true
                //TODO: Implement tree 3
            }
        }*/


    }

    private fun solution(solutionId : Int)
    {

    }


    private fun getResponse() : Boolean
    {
        var returnValue : Boolean = false
        var isValidResponse : Boolean = false

        var ans: String = ""
        sendButtonIll.isClickable = true;
        sendButtonIll.setOnClickListener {
            ans = editTextIll.text.toString()
            val message = Message(ans, "me")
            val sendMessageItem = SendMessageItem(message)
            messageAdapter.add(sendMessageItem)
            editTextIll.text.clear()

            if(ans.equals("yes",true))
            {
                returnValue = true
                isValidResponse = true
            }
            else if(ans.equals("no",true))
            {
                returnValue = false
                isValidResponse = true
            }
            else
            {
                getResponse()
            }

        }
        sendButtonIll.isClickable = false;

        if(isValidResponse)
            return returnValue
        return returnValue

    }**/

    private fun receiveAutoResponse(flag :Int)
    {
        var finalString : String = "Sorry. I didn't understand. Can you repeat?";
        if(flag == 0) //This is the start
        {
            finalString = getString(R.string.start_msg_illness)
        }
        GlobalScope.launch(Dispatchers.Main) {

            val receive = Message(
                msg = finalString, sendby = "me"
            )
            val receiveItem = ReceiveMessageItem(receive)
            messageAdapter.add(receiveItem)
        }
    }

    private fun receiveAutoResponse(temp:String, flag :Int)
    {
        var finalString : String = "Sorry. I didn't understand. Can you repeat?";
        if(endOfChat(temp))
        {
            finalString = getString(R.string.end_of_conversation)
        }
        else if(startOfChat(temp))
        {
            finalString = getString(R.string.start_msg_illness)
        }
        else if(flag == 2)
        {
            finalString = temp
        }

        GlobalScope.launch(Dispatchers.Main) {

            val receive = Message(
                msg = finalString, sendby = "me"
            )
            val receiveItem = ReceiveMessageItem(receive)
            messageAdapter.add(receiveItem)
        }
    }

    private fun receiveAutoResponse(temp:String)
    {
        var finalString : String = "Sorry. I didn't understand. Can you repeat?";
        if(endOfChat(temp))
        {
            finalString = getString(R.string.end_of_conversation)
        }
        else if(startOfChat(temp))
        {
            finalString = getString(R.string.start_msg_illness)
        }
        finalString = temp

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

    private fun invalidResponse()
    {
        val finalString = "Sorry! I didn't understand you. Please reply with 'Yes' or 'No'. "
        GlobalScope.launch(Dispatchers.Main) {

            val receive = Message(
                msg = finalString, sendby = "me"
            )
            val receiveItem = ReceiveMessageItem(receive)
            messageAdapter.add(receiveItem)
        }
    }
}
