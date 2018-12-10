package ep.rest

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.content_book_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class BookDetailActivity : AppCompatActivity(), Callback<Book> {

    private var book: Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)
        setSupportActionBar(toolbar)

        fabEdit.setOnClickListener {
            val intent = Intent(this, BookFormActivity::class.java)
            intent.putExtra("ep.rest.book", book)
            startActivity(intent)
        }

        fabDelete.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Confirm deletion")
            dialog.setMessage("Are you sure?")
            dialog.setPositiveButton("Yes") { _, _ -> deleteBook() }
            dialog.setNegativeButton("Cancel", null)
            dialog.create().show()
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getIntExtra("ep.rest.id", 0)

        if (id > 0) {
            BookService.instance.get(id).enqueue(this)
        }
    }

    private fun deleteBook() {
        // todo
    }

    override fun onResponse(call: Call<Book>, response: Response<Book>) {
        book = response.body()
        Log.i(TAG, "Got result: $book")

        if (response.isSuccessful) {
            tvBookDetail.text = book?.description
            toolbarLayout.title = book?.title
        } else {
            val errorMessage = try {
                "An error occurred: ${response.errorBody().string()}"
            } catch (e: IOException) {
                "An error occurred: error while decoding the error message."
            }

            Log.e(TAG, errorMessage)
            tvBookDetail.text = errorMessage
        }
    }

    override fun onFailure(call: Call<Book>, t: Throwable) {
        Log.w(TAG, "Error: ${t.message}", t)
    }

    companion object {
        private val TAG = BookDetailActivity::class.java.canonicalName
    }
}
