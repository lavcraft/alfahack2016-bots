package ru.hack2016.microbot.goods.bot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author tolkv
 * @since 04/12/15
 */
@Data
@Builder
@AllArgsConstructor
public class TelegramBotUpdateRequest {
  private final Integer offset;
  private final Integer limit;
  private final Integer timeout;
}