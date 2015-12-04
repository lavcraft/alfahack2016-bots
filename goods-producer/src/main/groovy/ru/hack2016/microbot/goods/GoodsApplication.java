package ru.hack2016.microbot.goods;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import ru.hack2016.microbot.goods.bot.Bot;
import ru.hack2016.microbot.goods.bot.GoodsBotConfig;
import ru.hack2016.microbot.goods.bot.SpeechBot;
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
  Bot goodsBot;
  @Autowired
  TelegramBot goodsTelegramBot;
  @Autowired
  SpeechBot speechBot;
//  @Autowired
//  SensorBot sensorBot;

  Subscription subscribe;
  private long lastChatId = 0;

  public static void main(String[] args) {
    new SpringApplicationBuilder(GoodsApplication.class)
        .web(false)
        .run(args);
  }

  @Bean
  public CommandLineRunner commandLineRunner() {
    String[] buttonSet1 = {"пива", "молока"};
    String[] buttonSet2 = {"огурцов", "рыбы"};

    return args -> subscribe = goodsBot.observe()
        .filter(message -> message.text() != null && !message.text().isEmpty())
        .doOnNext(message -> log.info("user {} said {} in chat {}", message.from().username(), message.text(), message.chat().title()))
        .doOnNext(message -> {
          ReplyKeyboardMarkup selective = new ReplyKeyboardMarkup(buttonSet1, buttonSet2)
              .oneTimeKeyboard(true)
              .selective(true);
          goodsTelegramBot.sendMessage(message.chat().id(), "_" + message.text() + "_",
              ParseMode.Markdown, false, null, null);
        })
        .doOnNext(message -> lastChatId = message.chat().id())
        .doOnError(Throwable::printStackTrace)
        .doOnCompleted(() -> log.info("COMPLETED"))
        .onErrorResumeNext(throwable -> {
          return empty();
        })
        .subscribe();
  }

  @PostConstruct
  public void checkAndInit() {
    if (goodsBotConfig.getToken().length() < 5) {
      throw new Error("Invalid token: bot.goods.token property");
    }

    speechBot.observe()
        .filter(s -> s != null && !s.isEmpty() && !s.equals("no parse"))
        .doOnNext(s -> log.info("speech : {}", s))
        .subscribe(msg -> {
          if (lastChatId != 0) {
            goodsTelegramBot.sendMessage(lastChatId, msg);

          }
        });
  }

  @PreDestroy
  public void clean() {
    if (!subscribe.isUnsubscribed()) {
      subscribe.unsubscribe();
    }
  }
}