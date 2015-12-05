package ru.hack2016.microbot.goods.bot
import rx.Observable

import java.util.function.Consumer

/**
 * @author tolkv
 * @since 05/12/15
 */
interface SensorBot {
  public Observable<Boolean> observe()

  public void setCallback(Consumer<Boolean> booleanConsumer)
}