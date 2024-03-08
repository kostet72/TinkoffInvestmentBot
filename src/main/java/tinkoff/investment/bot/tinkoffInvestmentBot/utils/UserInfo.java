package tinkoff.investment.bot.tinkoffInvestmentBot.utils;

import tinkoff.investment.bot.tinkoffInvestmentBot.model.entity.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import static org.springframework.data.relational.core.query.Query.query;
import static org.springframework.data.relational.core.query.Criteria.where;

@Service
@RequiredArgsConstructor
public class UserInfo {

    private final R2dbcEntityTemplate template;

    public Flux<Long> getChatIdFromUsers() {

        return template.select(query(where("is_subscribed").is(true)), User.class)
                .flatMap(user -> Mono.just(user.getChatId()))
                .switchIfEmpty(Mono.empty());
    }
}
