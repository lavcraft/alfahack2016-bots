package ru.hack2016.microbot.goods.bot;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.hack2016.microbot.goods.search.SentenceAnalyzer;
import ru.hack2016.microbot.raspberry.RaspberryLCDController;
import ru.hack2016.microbot.raspberry.Transliterator;
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
      });

      log.info("Wait speech cycle");
      gpipool.execute(() -> {
        sensorBot.observe()
            .observeOn(Schedulers.from(gpipool))
            .debounce(200, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .subscribe(aBoolean -> {
              isRun = aBoolean;
              log.info("aboolean : {}", aBoolean);
              if (isRun) {
                raspberryLCDController.writeText(0, Transliterator.transliterate("Слушаю"));
              } else {
                raspberryLCDController.writeText(0, Transliterator.transliterate("Не слушаю"));
              }
            });
        log.info("Restart gpio");
      });
    }

    return Observable.create(subscriber -> speechPool.execute(() -> {
      log.info("Start speech cycle");
      speechRecognitor.recognize()
          .observeOn(Schedulers.from(speechPool))
          .doOnError(Throwable::printStackTrace)
          .flatMap(s1 -> {
            log.info("symbol : {}", s1);
            return sentenceAnalyzer.parse(s1.replace("<", "").replace(">", ""))
                .onErrorResumeNext(Observable.just(s1));
          })
          .subscribe(s -> {
            log.info("isrun : {}", isRun);
            if (isRun) {
              log.info("send on next {}", s);
              subscriber.onNext(s);
              raspberryLCDController.writeText(1, Transliterator.transliterate(s));
            }
          });
      log.info("Next speech cycle");
      subscriber.onCompleted();
    }));
  }
}