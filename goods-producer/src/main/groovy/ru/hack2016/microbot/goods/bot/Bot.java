package ru.hack2016.microbot.goods.bot;

import com.pengrad.telegrambot.model.Message;
import rx.Observable;

/**
 * @author tolkv
 * @since 04/12/15
 */
public interface Bot {
  Observable<Message> observe();
}
