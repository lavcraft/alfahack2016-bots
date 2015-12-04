package ru.hack2016.microbot.goods;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import ru.hack2016.microbot.goods.bot.Bot;
import ru.hack2016.microbot.goods.bot.GoodsBotConfig;
import ru.hack2016.microbot.goods.bot.SpeechBot;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

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
  Bot goodsBot;
  @Autowired
  TelegramBot goodsTelegramBot;

  Subscription subscribe;
  @Autowired
  SpeechBot speechBot;
  Observable<String> speechObservable;

  public static void main(String[] args) {
    new SpringApplicationBuilder(GoodsApplication.class)
        .web(false)
        .run(args);
  }

  @Bean
  public CommandLineRunner commandLineRunner() {

    return args -> subscribe = goodsBot.observe()
        .doOnNext(message -> log.info("user {} said {}", message.from().username(), message.text()))
        .doOnNext(message -> goodsTelegramBot.sendMessage(message.chat().id(), "Echo *" + message.text() + "*",
            ParseMode.Markdown, false, null, null))
        .doOnError(Throwable::printStackTrace)
        .doOnCompleted(() -> log.info("COMPLETED"))
        .onErrorResumeNext(throwable -> {
          return empty();
        })
        .subscribeOn(Schedulers.immediate())
        .subscribe();
  }

  @PostConstruct
  public void checkAndInit() {
    if (goodsBotConfig.getToken().length() < 5) {
      throw new Error("Invalid token: bot.goods.token property");
    }

    log.info("===============================1");
    speechBot.observe().doOnNext(s -> log.info("speech : {}", s))
        .subscribe();
    log.info("===============================2");
  }

  @PreDestroy
  public void clean() {
    if (!subscribe.isUnsubscribed()) {
      subscribe.unsubscribe();
    }
  }
}