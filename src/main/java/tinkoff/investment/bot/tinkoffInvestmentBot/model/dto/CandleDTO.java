package tinkoff.investment.bot.tinkoffInvestmentBot.model.dto;

import lombok.*;
import tinkoff.investment.bot.tinkoffInvestmentBot.model.enums.CandleType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandleDTO {

    private String nameOfTheCompany;
    private Double openPrice;
    private Double closePrice;
    private Double highShadow;
    private Double lowShadow;
    private Double percentChange;
    private CandleType candleType;
}
