package ru.hack2016.microbot.goods.bot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import ru.hack2016.microbot.raspberry.RaspberryColorSensor
import rx.Observable
import rx.Subscriber

import java.util.concurrent.ExecutorService

/**
 * @author tolkv
 * @since 04/12/15
 */
@Component
@Profile('sensor')
class SensorBot {
  @Autowired
  @Qualifier("bot.sensor.pool")
  ExecutorService pool

  @Autowired
  SensorBotConfig botConfig

  public Observable<Boolean> observe() {
    Observable.create { Subscriber<Boolean> subscriber ->
      def sensor = new RaspberryColorSensor(botConfig.thresoldLevel, botConfig.pin)
      while (!Thread.currentThread().isInterrupted()) {
        subscriber.onNext(sensor.getState())
      }
      subscriber.onCompleted()
    }
  }
}
