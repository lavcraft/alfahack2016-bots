package ru.hack2016.microbot.goods.bot;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import rx.Observable;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author tolkv
 * @since 04/12/15
 */
@Data
@Component
@Profile("stub")
public class StubSensorBot implements SensorBot {
  @Autowired
  @Qualifier("bot.sensor.pool")
  private ExecutorService pool;
  private Observable<Boolean> observable;
  private Consumer<Boolean> callback;

  public Observable<Boolean> observe() {
    return Observable.create(subscriber -> {
      pool.execute(() -> {
        while (!Thread.currentThread().isInterrupted()) {
          boolean nextBoolean = new Random().nextBoolean();
          subscriber.onNext(nextBoolean);
          try {
            TimeUnit.MILLISECONDS.sleep(50);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          callback.accept(nextBoolean);
        }

        subscriber.onCompleted();
      });
    });
  }
}
