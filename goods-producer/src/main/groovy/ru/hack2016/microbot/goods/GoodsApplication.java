package ru.hack2016.microbot.goods;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import ru.hack2016.microbot.goods.bot.GoodsBotConfig;

import javax.annotation.PostConstruct;

/**
 * @author tolkv
 * @since 04/12/15
 */
@Slf4j
@SpringBootApplication
public class GoodsApplication {
  @Autowired
  GoodsBotConfig goodsBotConfig;

  public static void main(String[] args) {
    new SpringApplicationBuilder(GoodsApplication.class)
        .web(false)
        .run(args);


  }

  @PostConstruct
  public void checkAndInit() {
    if (goodsBotConfig.getToken().length() < 5) {
      throw new Error("Invalid token: bot.goods.token property");
    }
  }
}
