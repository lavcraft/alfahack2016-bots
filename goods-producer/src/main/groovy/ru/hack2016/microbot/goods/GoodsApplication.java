package ru.hack2016.microbot.goods;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import ru.hack2016.microbot.goods.bot.GoodsBot;
import ru.hack2016.microbot.goods.bot.GoodsBotConfig;
import rx.Subscription;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static rx.Observable.empty;

/**
 * @author tolkv
 * @since 04/12/15
 */
@Slf4j
@SpringBootApplication
public class GoodsApplication {
  @Autowired
  GoodsBotConfig goodsBotConfig;

  @Autowired
  GoodsBot goodsBot;
  Subscription subscribe;

  public static void main(String[] args) {
    new SpringApplicationBuilder(GoodsApplication.class)
        .web(false)
        .run(args);
  }

  @Bean
  public CommandLineRunner commandLineRunner() {
    return args -> {
      subscribe = goodsBot.observe()
          .doOnNext(message -> {
            log.info("message {}", message);
          })
          .doOnError(Throwable::printStackTrace)
          .doOnCompleted(() -> log.info("COMPLETED"))
          .onErrorResumeNext(throwable -> {
            return empty();
          }).subscribe();
    };
  }

  @PostConstruct
  public void checkAndInit() {
    if (goodsBotConfig.getToken().length() < 5) {
      throw new Error("Invalid token: bot.goods.token property");
    }
  }

  @PreDestroy
  public void clean() {
    if (!subscribe.isUnsubscribed()) {
      subscribe.unsubscribe();
    }
  }
}
