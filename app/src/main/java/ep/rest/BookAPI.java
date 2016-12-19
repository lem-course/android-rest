package ep.rest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BookAPI {
    @GET("api/books/")
    Call<List<Book>> getAll();
}
