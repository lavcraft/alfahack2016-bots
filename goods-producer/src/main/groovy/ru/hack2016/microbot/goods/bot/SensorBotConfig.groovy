package ru.hack2016.microbot.goods.bot

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author tolkv
 * @since 04/12/15
 */
@Component
@ConfigurationProperties("bot.sensor")
class SensorBotConfig {
  int thresoldLevel = 10
  int pin = 2
  int sleepTime = 1000
}
