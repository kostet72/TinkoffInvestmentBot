package tinkoff.investment.bot.tinkoffInvestmentBot.model.entity;

import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stock")
public class Stock {

    private String ticker;
    private String price;
}
