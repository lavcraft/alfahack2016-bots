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
  String token;
  String name;
  Integer offset = 0;
  Integer limit = 10;
  Integer timeout = 30;
}
