package com.aut.isen_todo


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseBooleanArray
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.aut.isen_todo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var add: Button
    private lateinit var listView: ListView
    private lateinit var editText: EditText
    private lateinit var delete: Button
    private lateinit var clear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        add = findViewById(R.id.add)
        listView = findViewById(R.id.listView)
        editText = findViewById(R.id.editText)
        delete = findViewById(R.id.delete)
        clear = findViewById(R.id.clear)
        binding = ActivityMainBinding.inflate(layoutInflater)



        // Initializing the array lists and the adapter
        val itemlist = arrayListOf<String>()
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_multiple_choice, itemlist
        )
        // Adding the items to the list when the add button is pressed
        add.setOnClickListener {

            itemlist.add(editText.text.toString())
            listView.adapter = adapter
            adapter.notifyDataSetChanged()
            // This is because every time when you add the item the input space or the eidt text space will be cleared
            editText.text.clear()

        }
        // Clearing all the items in the list when the clear button is pressed
        clear.setOnClickListener {

            itemlist.clear()
            adapter.notifyDataSetChanged()
        }         // Adding the toast message to the list when an item on the list is pressed
        listView.setOnItemClickListener { _, _, i, _ ->
            android.widget.Toast.makeText(
                this,
                "You Selected the item --> " + itemlist[i],
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }        // Selecting and Deleting the items from the list when the delete button is pressed
        delete.setOnClickListener {
            val position: SparseBooleanArray = listView.checkedItemPositions
            val count = listView.count
            var item = count - 1
            while (item >= 0) {
                if (position.get(item)) {
                    adapter.remove(itemlist[item])
                }
                item--
            }
            position.clear()
            adapter.notifyDataSetChanged()
        }
    }


} 