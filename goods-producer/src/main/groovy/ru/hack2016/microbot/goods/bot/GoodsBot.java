package ru.hack2016.microbot.goods.bot;

import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import rx.Observable;

/**
 * @author tolkv
 * @since 04/12/15
 */
@Component
public class GoodsBot {
  public Observable<Message> observe() {
    return Observable.empty();
  }
}
