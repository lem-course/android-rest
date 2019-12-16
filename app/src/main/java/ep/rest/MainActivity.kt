package ep.rest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MainActivity : AppCompatActivity(), Callback<List<Book>> {

    private var adapter: BookAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = BookAdapter(this)
        items.adapter = adapter
        items.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            val book = adapter?.getItem(i)
            if (book != null) {
                val intent = Intent(this, BookDetailActivity::class.java)
                intent.putExtra("ep.rest.id", book.id)
                startActivity(intent)
            }
        }

        container.setOnRefreshListener { BookService.instance.getAll().enqueue(this) }

        btnSave.setOnClickListener {
            val intent = Intent(this, BookFormActivity::class.java)
            startActivity(intent)
        }

        BookService.instance.getAll().enqueue(this)
    }

    override fun onResponse(call: Call<List<Book>>, response: Response<List<Book>>) {
        val hits = response.body() ?: emptyList()

        if (response.isSuccessful) {
            Log.i(TAG, "Hits: " + hits.size)
            adapter?.clear()
            adapter?.addAll(hits)
        } else {
            val errorMessage = try {
                "An error occurred: ${response.errorBody()?.string()}"
            } catch (e: IOException) {
                "An error occurred: error while decoding the error message."
            }

            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            Log.e(TAG, errorMessage)
        }
        container.isRefreshing = false
    }

    override fun onFailure(call: Call<List<Book>>, t: Throwable) {
        Log.w(TAG, "Error: ${t.message}", t)
        container.isRefreshing = false
    }

    companion object {
        private val TAG = MainActivity::class.java.canonicalName
    }
}
