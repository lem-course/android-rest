package ep.rest;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Callback<List<Book>> {
    private static final String TAG = MainActivity.class.getCanonicalName();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://10.10.10.221/netbeans/mvc-rest/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private final BookAPI service = retrofit.create(BookAPI.class);

    private SwipeRefreshLayout container;
    private ListView list;
    private final List<Book> books = new ArrayList<>();
    private BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.items);

        adapter = new BookAdapter(this, books);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "clicked on: " + books.get(i));
            }
        });

        container = (SwipeRefreshLayout) findViewById(R.id.container);
        container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                service.getAll().enqueue(MainActivity.this);
            }
        });

        service.getAll().enqueue(MainActivity.this);
    }

    @Override
    public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
        final List<Book> hits = response.body();
        Log.i(TAG, "Hits: " + hits.size());
        adapter.clear();
        adapter.addAll(hits);
        container.setRefreshing(false);
    }

    @Override
    public void onFailure(Call<List<Book>> call, Throwable t) {
        Log.w(TAG, "Error: " + t.getMessage(), t);
        container.setRefreshing(false);
    }
}
