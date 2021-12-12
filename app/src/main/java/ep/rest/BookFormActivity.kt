package ep.rest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ep.rest.databinding.ActivityBookFormBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class BookFormActivity : AppCompatActivity(), Callback<Void> {

    private var book: Book? = null

    private val binding by lazy {
        ActivityBookFormBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            val author = binding.etAuthor.text.toString().trim()
            val title = binding.etTitle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val price = binding.etPrice.text.toString().trim().toDouble()
            val year = binding.etYear.text.toString().trim().toInt()

            if (book == null) { // dodajanje
                BookService.instance.insert(
                    author, title, price,
                    year, description
                ).enqueue(this)
            } else { // urejanje
                BookService.instance.update(
                    book!!.id, author, title, price,
                    year, description
                ).enqueue(this)
            }
        }

        val book = intent?.getSerializableExtra("ep.rest.book") as Book?
        if (book != null) {
            binding.etAuthor.setText(book.author)
            binding.etTitle.setText(book.title)
            binding.etPrice.setText(book.price.toString())
            binding.etYear.setText(book.year.toString())
            binding.etDescription.setText(book.description)
            this.book = book
        }
    }

    override fun onResponse(call: Call<Void>, response: Response<Void>) {
        val headers = response.headers()

        if (response.isSuccessful) {
            val id = if (book == null) {
                // Preberemo Location iz zaglavja
                Log.i(TAG, "Insertion completed.")
                val parts =
                    headers.get("Location")?.split("/".toRegex())?.dropLastWhile { it.isEmpty() }
                        ?.toTypedArray()
                // spremenljivka id dobi vrednost, ki jo vrne zadnji izraz v bloku
                parts?.get(parts.size - 1)?.toInt()
            } else {
                Log.i(TAG, "Editing saved.")
                // spremenljivka id dobi vrednost, ki jo vrne zadnji izraz v bloku
                book!!.id
            }

            val intent = Intent(this, BookDetailActivity::class.java)
            intent.putExtra("ep.rest.id", id)
            startActivity(intent)
        } else {
            val errorMessage = try {
                "An error occurred: ${response.errorBody()?.string()}"
            } catch (e: IOException) {
                "An error occurred: error while decoding the error message."
            }

            Log.e(TAG, errorMessage)
        }
    }

    override fun onFailure(call: Call<Void>, t: Throwable) {
        Log.w(TAG, "Error: ${t.message}", t)
    }

    companion object {
        private val TAG = BookFormActivity::class.java.canonicalName
    }
}
