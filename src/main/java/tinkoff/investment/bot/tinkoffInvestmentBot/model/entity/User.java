package tinkoff.investment.bot.tinkoffInvestmentBot.model.entity;

import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    private String tag;
    private Long chatId;
}
