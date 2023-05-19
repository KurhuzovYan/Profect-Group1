package parsers;

import lombok.Data;

@Data
public class ResponseMono {
        public int currencyCodeA;
        public int currencyCodeB;
        public int date;
        public double rateBuy;
        public double rateCross;
        public double rateSell;
}
