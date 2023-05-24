package dto.general;

import lombok.Data;

import java.util.Date;

@Data
public class Monobank {
        private int currencyCodeA;
        private int currencyCodeB;
        private long date;
        private double rateBuy;
        private double rateCross;
        private double rateSell;

        public Date getDate() {
                return new Date(date*1000);
        }
}
