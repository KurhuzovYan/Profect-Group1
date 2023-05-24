package dto;

import constants.Currencies;
import lombok.Data;

import java.util.List;
@Data
public class UsersSettings {
    private int numberOfDecimal;
    private String bankMame;
    private List<Currencies> currencies;
    private int reminder;
}
