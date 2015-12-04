package ru.hack2016.microbot.goods.bot;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.hack2016.microbot.speechkit.SpeechRecognitor;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author tolkv
 * @since 04/12/15
 */
@Slf4j
@Data
@Component
public class SpeechBot {
  @Autowired
  @Qualifier("bot.speech.pool")
  private ExecutorService speechPool;

  @Autowired
  @Qualifier("bot.sensor.pool")
  private ExecutorService gpipool;

  @Autowired
  private SpeechRecognitor speechRecognitor;

  @Autowired(required = false)
  private SensorBot sensorBot;

  private volatile boolean isRun;

  public Observable<String> observe() {
    if (sensorBot != null) {
      sensorBot.setCallback(aBoolean1 -> isRun = aBoolean1);
      log.info("Wait speech cycle");
      gpipool.execute(() -> {
        sensorBot.observe()
            .subscribeOn(Schedulers.from(gpipool))
            .observeOn(Schedulers.from(gpipool))
            .debounce(200, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .doOnNext(aBoolean -> {
              isRun = aBoolean;
              log.info("aboolean : {}", aBoolean);
            })
            .subscribe();
        log.info("Restart gpio");
      });
    }

    return Observable.create(subscriber -> {
      speechPool.execute(() -> {
        while (!Thread.currentThread().isInterrupted()) {
          log.info("Start speech cycle");
          speechRecognitor.recognize()
              .subscribeOn(Schedulers.from(speechPool))
              .observeOn(Schedulers.from(speechPool))
              .doOnNext((t) -> {
                log.info("isrun : {}", isRun);
                if (isRun) {
                  subscriber.onNext(t);
                }
              })
              .doOnError(Throwable::printStackTrace)
              .toBlocking().single();
          log.info("Next speech cycle");
        }
        subscriber.onCompleted();
      });
    });
  }
}