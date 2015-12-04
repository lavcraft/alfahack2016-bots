package ru.hack2016.microbot.goods.bot;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.hack2016.microbot.speechkit.SpeechRecognitor;
import rx.Observable;

import java.util.concurrent.ExecutorService;

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
  private ExecutorService pool;
  @Autowired
  private SpeechRecognitor speechRecognitor;

  public Observable<String> observe() {
    return Observable.create(subscriber -> pool.execute(() -> {
      while (!Thread.currentThread().isInterrupted()) {
        log.info("Start speech cycle");
        speechRecognitor
            .recognize()
            .doOnNext(subscriber::onNext)
            .doOnNext(s -> log.info("speech {} ", s))
            .doOnError(Throwable::printStackTrace)
            .toBlocking().single();
        log.info("Next speech cycle");
      }
      subscriber.onCompleted();
    }));
  }
}
