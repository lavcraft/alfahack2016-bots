package ru.hack2016.microbot.goods.bot;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.hack2016.microbot.goods.search.SentenceAnalyzer;
import ru.hack2016.microbot.raspberry.RaspberryLCDController;
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
  @Autowired(required = false)
  RaspberryLCDController raspberryLCDController;
  @Autowired
  @Qualifier("bot.speech.pool")
  private ExecutorService speechPool;
  @Autowired
  @Qualifier("bot.sensor.pool")
  private ExecutorService gpipool;
  @Autowired
  private SpeechRecognitor speechRecognitor;
  @Autowired
  private SentenceAnalyzer sentenceAnalyzer;
  @Autowired(required = false)
  private SensorBot sensorBot;

  private volatile boolean isRun = true;

  public Observable<String> observe() {
    if (sensorBot != null) {
      sensorBot.setCallback(aBoolean1 -> {
        isRun = aBoolean1;
        if (isRun != aBoolean1) {
          speechRecognitor.stop();
        }
      });

      log.info("Wait speech cycle");
      sensorBot.observe()
          .observeOn(Schedulers.from(gpipool))
          .subscribeOn(Schedulers.from(gpipool))
          .debounce(200, TimeUnit.MILLISECONDS)
          .doOnNext(aBoolean -> {
            log.info("aboolean: {}", aBoolean);
            isRun = aBoolean;
          });
    }

    return getObservable();
  }

  private Observable<String> getObservable() {
    speechRecognitor.start();
    return speechRecognitor.recognize()
        .observeOn(Schedulers.from(speechPool))
        .subscribeOn(Schedulers.from(speechPool))
        .doOnError(Throwable::printStackTrace)
        .flatMap(item -> {
          log.info("symbol : {}", item);
          return sentenceAnalyzer
              .parse(item.replace("<", "").replace(">", ""))
              .onErrorResumeNext(Observable.just(item));
        });
  }
}