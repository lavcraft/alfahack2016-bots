package ru.hack2016.microbot.goods.bot;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.hack2016.microbot.goods.search.SentenceAnalyzer;
import ru.hack2016.microbot.raspberry.LCDController;
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
  LCDController lcdController;
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

  @Autowired
  private SentenceAnalyzer sentenceAnalyzer;

  @Autowired
  private RawBot rawBot;

  private volatile boolean isRun = true;

  public Observable<String> observe() {
    if (sensorBot != null) {
      sensorBot.setCallback(aBoolean1 -> {
        if (isRun != aBoolean1) {
          lcdController.writeText(0, "Sleeping...");
        }
        isRun = aBoolean1;
      });
      log.info("Wait speech cycle");
      gpipool.execute(() -> {
        sensorBot.observe()
            .subscribeOn(Schedulers.from(gpipool))
            .observeOn(Schedulers.from(speechPool))
            .debounce(200, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .subscribe(aBoolean -> {
              isRun = aBoolean;
              log.info("aboolean : {}", aBoolean);
            });
        log.info("Restart gpio");
      });
    }

    return Observable.create(subscriber -> speechPool.execute(() -> {
      log.info("Start speech cycle");
      speechRecognitor.recognize()
          .observeOn(Schedulers.from(speechPool))
          .doOnError(Throwable::printStackTrace)
          .map(s2 -> {
            rawBot.getBot().sendMessage((long) -11280563, s2);
            return s2;
          })
          .flatMap(s1 -> {
            log.info("symbol : {}", s1);
            lcdController.writeText(0, Transliterator.transliterate("Listening..."));
            return sentenceAnalyzer.parse(s1.replace("<", "").replace(">", ""))
                    .onErrorResumeNext(Observable.just(s1));
          })
          .subscribe(s -> {
            log.info("isrun : {}", isRun);
            if (isRun) {
              log.info("send on next {}", s);
              lcdController.writeText(1, Transliterator.transliterate(s));
              subscriber.onNext(s);
            }
          });
      log.info("Next speech cycle");
      subscriber.onCompleted();
    }));
  }
}