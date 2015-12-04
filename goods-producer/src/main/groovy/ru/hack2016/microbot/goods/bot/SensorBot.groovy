package ru.hack2016.microbot.goods.bot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import rx.Observable
import rx.Subscriber

import java.util.concurrent.ExecutorService

/**
 * @author tolkv
 * @since 04/12/15
 */
@Component
class SensorBot {
  @Autowired
  @Qualifier("bot.sensor.pool")
  private ExecutorService pool

  public Observable<Boolean> observe() {
    Observable.create { Subscriber<Boolean> subscriber ->
      subscriber.onNext(false)
      subscriber.onCompleted()
    }
  }
}
