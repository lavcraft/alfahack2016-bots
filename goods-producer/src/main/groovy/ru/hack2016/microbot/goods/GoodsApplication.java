package ru.hack2016.microbot.goods;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import ru.hack2016.microbot.goods.bot.*;
import ru.hack2016.microbot.goods.client.ItemsUtils;
import ru.hack2016.microbot.raspberry.LCDController;
import rx.schedulers.Schedulers;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

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

  @Autowired
  RawBotConfig rawBotConfig;
  @Autowired
  RawBot rawBot;

  @Autowired
  @Qualifier("bot.goods.telegram")
  TelegramBot goodsTelegramBot;

  @Autowired
  @Qualifier("bot.raw.telegram")
  TelegramBot rawTelegramBot;

  @Autowired
  SpeechBot speechBot;

  private long lastChatId = 0;
  private ArrayList<Long> chatIds = new ArrayList<>();
  @Autowired
  @Qualifier("bot.goods.telegram.pool")
  private ExecutorService goodsTelegramPool;

  @Autowired
  @Qualifier("bot.raw.telegram.pool")
  private ExecutorService rawTelegramPool;

  public static void main(String[] args) {
    new SpringApplicationBuilder(GoodsApplication.class)
        .web(false)
        .run(args);
  }

  @Autowired
  LCDController lcdController;

  @SneakyThrows
  @PostConstruct
  public void checkAndInit() {
    if (goodsBotConfig.getToken().length() < 5) {
      throw new Error("Invalid token: bot.goods.token property");
    }

    speechBot.observe()
        .subscribeOn(Schedulers.from(goodsTelegramPool))
        .observeOn(Schedulers.from(goodsTelegramPool))
        .filter(s -> s != null && !s.isEmpty() && !s.equals("no parse"))
        .doOnNext(msg -> {
          log.info("ids {}", chatIds.size());
          chatIds.forEach(aLong -> goodsTelegramBot.sendMessage(aLong, msg));
          try {
            ItemsUtils.send(msg);
          } catch (Exception e) {
            e.printStackTrace();
          }
        })
        .subscribe();

    goodsBot.observe()
        .subscribeOn(Schedulers.from(goodsTelegramPool))
        .filter(message -> message.text() != null && !message.text().isEmpty())
        .doOnNext(message -> log.info("user {} said {} in chat {}", message.from().username(), message.text(), message.chat().title()))
        .doOnNext(message -> {
          log.info("msg : {}", message);
          goodsTelegramBot.sendMessage(message.chat().id(), "_" + message.text() + "_",
                  ParseMode.Markdown, false, null, null);

        })
        .doOnNext(message -> {
          if (!chatIds.contains(message.chat().id())) {
            chatIds.add(message.chat().id());
          }
        })
        .doOnError(Throwable::printStackTrace)
        .doOnCompleted(() -> log.info("COMPLETED"))
        .onErrorResumeNext(throwable -> {
          return empty();
        })
        .subscribe();

    rawBot.observe()
            .subscribeOn(Schedulers.from(rawTelegramPool))
            .filter(message -> message.text() != null && !message.text().isEmpty())
            .doOnNext(message -> log.info("user {} said {} in chat {}", message.from().username(), message.text(), message.chat().title()))
            .doOnNext(message -> {
              log.info("msg : {}", message);
              rawTelegramBot.sendMessage(message.chat().id(), "_" + message.text() + "_",
                      ParseMode.Markdown, false, null, null);

            })
            .doOnNext(message -> {
              if (!chatIds.contains(message.chat().id())) {
                chatIds.add(message.chat().id());
              }
            })
            .doOnError(Throwable::printStackTrace)
            .doOnCompleted(() -> log.info("COMPLETED"))
            .onErrorResumeNext(throwable -> {
              return empty();
            })
            .subscribe();

    log.info("Post constructed");
  }
}