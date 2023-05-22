package parsers;

import lombok.Data;

import java.util.Date;

@Data
public class ResponseMono {
        public int currencyCodeA;
        public int currencyCodeB;
        public long date;
        public double rateBuy;
        public double rateCross;
        public double rateSell;

        public Date getDate() {
                return new Date(date);
        }
}
