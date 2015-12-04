package ru.hack2016.microbot.goods.bot

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author tolkv
 * @since 04/12/15
 */
@Component
@ConfigurationProperties("bot.goods")
class GoodsBotConfig {
  String token
  String name
  int offset = 0
  int limit = 10
  int timeout = 30
  long pollPeriod = 1000

  /**
   * in seconds
   */
  int connectionTimeout = 5

  /**
   * in seconds
   */
  int connectionReadTimeout = 7200

  /**
   * in seconds
   */
  int connectionWriteTimeout = 5
}
