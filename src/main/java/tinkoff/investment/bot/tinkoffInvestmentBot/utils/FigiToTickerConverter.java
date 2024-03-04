package tinkoff.investment.bot.tinkoffInvestmentBot.utils;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

@Service
public class FigiToTickerConverter {

    @Value("${file.path}")
    String filePath;

    public String findTickerByFigi(String figi) {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(": ");

                if (parts.length == 2 && parts[0].equals(figi))
                    return parts[1];
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
