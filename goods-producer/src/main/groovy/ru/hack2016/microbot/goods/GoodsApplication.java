package ru.hack2016.microbot.goods;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import ru.hack2016.microbot.goods.bot.Bot;
import ru.hack2016.microbot.goods.bot.GoodsBotConfig;
import ru.hack2016.microbot.goods.bot.SpeechBot;
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
  Bot goodsBot;
  @Autowired
  TelegramBot goodsTelegramBot;
  @Autowired
  SpeechBot speechBot;

  private long lastChatId = 0;
  private ArrayList<Long> chatIds = new ArrayList<>();
  @Autowired
  @Qualifier("bot.telegram.pool")
  private ExecutorService telegramPool;

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

    speechBot.observe()
        .subscribeOn(Schedulers.from(telegramPool))
        .observeOn(Schedulers.from(telegramPool))
        .filter(s -> s != null && !s.isEmpty() && !s.equals("no parse"))
        .doOnNext(s -> log.info("speech : {}", s))
        .doOnNext(msg -> {
          log.info("ids {}", chatIds.size());
          chatIds.forEach(aLong -> goodsTelegramBot.sendMessage(aLong, msg));
        })
        .subscribe();

    goodsBot.observe()
        .subscribeOn(Schedulers.from(telegramPool))
        .observeOn(Schedulers.from(telegramPool))
        .filter(message -> message.text() != null && !message.text().isEmpty())
        .doOnNext(message -> log.info("user {} said {} in chat {}", message.from().username(), message.text(), message.chat().title()))
        .doOnNext(message -> {
          log.info("msg : {}", message);
          goodsTelegramBot.sendMessage(message.chat().id(), "_" + message.text() + "_",
              ParseMode.Markdown, false, null, null);
        })
        .doOnNext(message -> {
          if (chatIds.contains(message.chat().id())) {
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