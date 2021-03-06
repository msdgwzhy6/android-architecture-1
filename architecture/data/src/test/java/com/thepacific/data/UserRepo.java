package com.thepacific.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thepacific.data.cache.DiskCache;
import com.thepacific.data.cache.MemoryCache;
import com.thepacific.data.http.Envelope;
import com.thepacific.data.http.OnAccessFailure;
import io.reactivex.Observable;
import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

public class UserRepo extends Repository<UserQuery, List<User>> {

  private UserRepo(Gson gson,
      DiskCache diskCache,
      MemoryCache memoryCache,
      OnAccessFailure onAccessFailure) {
    super(gson, diskCache, memoryCache, onAccessFailure);
  }

  public static UserRepo create() {
    File cacheDir = new File("http");
    return new UserRepo(new Gson(),
        new DiskCache(cacheDir),
        new MemoryCache(),
        e -> {
          throw new AssertionError(e);
        }
    );
  }

  @Override
  protected boolean isIrrelevant(List<User> data) {
    return data == null;
  }

  @Override
  protected Observable<Envelope<List<User>>> dispatchNetwork() {
    return Observable.just(EnvelopeImpl.create());
  }

  @Override
  protected String getKey(UserQuery query) {
    return "get-user";
  }

  @Override
  protected Type dataType() {
    return new TypeToken<List<User>>() {
    }.getType();
  }
}
