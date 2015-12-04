package ru.hack2016.microbot.goods.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import rx.Observable;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author tolkv
 * @since 04/12/15
 */
@Data
@Component
public class GoodsBot implements Bot {
  @Autowired
  private TelegramBot bot;
  @Autowired
  private GoodsBotConfig goodsBotConfig;
  @Autowired
  @Qualifier("bot.goods.pool")
  private ExecutorService pool;
  private Observable<Message> observable;
  private volatile boolean isRun = true;

  @PostConstruct
  public void init() {
    observable = Observable.create(subscriber -> pool.execute(() -> {
      int lastUpdatedId = goodsBotConfig.getOffset();
      while (true) {
        if (!isRun || Thread.currentThread().isInterrupted()) {
          subscriber.onCompleted();
          break;
        }

        GetUpdatesResponse updates = getUpdates(UpdateRequest.builder()
            .offset(lastUpdatedId + 1)
            .timeout(goodsBotConfig.getTimeout())
            .limit(goodsBotConfig.getLimit())
            .build());

        if (updates.isOk() && updates.updates().size() > 0) {
          List<Update> collect = updates.updates().stream()
              .sorted((o1, o2) -> o1.updateId().compareTo(o2.updateId()))
              .collect(Collectors.toList());

          if (collect.get(0).updateId() > lastUpdatedId) {
            lastUpdatedId = collect.get(0).updateId();
          }

          collect.forEach(update -> subscriber.onNext(update.message()));
        }
      }
    }));
  }

  private GetUpdatesResponse getUpdates(UpdateRequest updateRequest) {
    return bot.getUpdates(updateRequest.getOffset(), updateRequest.getLimit(), updateRequest.getTimeout());
  }

  @Override
  public Observable<Message> observe() {
    return observable;
  }
}
