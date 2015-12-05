package ru.hack2016.microbot.goods.bot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import rx.Observable
import rx.Subscriber

import java.util.concurrent.TimeUnit
import java.util.function.Consumer

import static rx.Observable.create
/**
 * @author tolkv
 * @since 04/12/15
 */
@Component
@Profile('stub')
class StubSensorBot extends SensorBot {

  @Autowired
  SensorBotConfig botConfig

  Consumer<Boolean> callback

  public Observable<Boolean> observe() {
    create { Subscriber<Boolean> subscriber ->
      while (!Thread.currentThread().isInterrupted()) {
        def state = true
        subscriber.onNext(state)
        TimeUnit.MILLISECONDS.sleep(botConfig.sleepTime)
        callback.accept(state)
      }
      subscriber.onCompleted()
    }
  }
}
