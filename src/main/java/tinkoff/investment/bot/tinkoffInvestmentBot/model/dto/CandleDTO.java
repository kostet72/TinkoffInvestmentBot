package tinkoff.investment.bot.tinkoffInvestmentBot.model.dto;

import lombok.*;
import tinkoff.investment.bot.tinkoffInvestmentBot.model.enums.CandleType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandleDTO {

    private String ticker;
    private Double openPrice;
    private Double closePrice;
    private Double highShadow;
    private Double lowShadow;
    private CandleType candleType;
}
