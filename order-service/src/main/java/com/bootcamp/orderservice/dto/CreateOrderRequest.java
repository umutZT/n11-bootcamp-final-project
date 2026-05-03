package com.bootcamp.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "New order request including card data forwarded to Iyzico")
public class CreateOrderRequest {

    @Schema(description = "Order line items", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @Valid
    private List<CreateOrderItemRequest> items;

    @Schema(description = "Cardholder name as printed on the card", example = "Ahmet Yilmaz",
            minLength = 3, maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(min = 3, max = 100)
    private String cardHolderName;

    @Schema(description = "Card PAN (15-19 digits). Use Iyzico SUCCESS test card 5528790000000008.",
            example = "5528790000000008", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Pattern(regexp = "\\d{15,19}", message = "Card number must be 15-19 digits")
    private String cardNumber;

    @Schema(description = "Expiration month (01-12)", example = "12", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Pattern(regexp = "(0[1-9]|1[0-2])")
    private String expireMonth;

    @Schema(description = "Expiration year (4 digits)", example = "2030", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Pattern(regexp = "\\d{4}")
    private String expireYear;

    @Schema(description = "Card CVC/CVV (3-4 digits)", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Pattern(regexp = "\\d{3,4}")
    private String cvc;

    public CreateOrderRequest() {
    }

    public List<CreateOrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CreateOrderItemRequest> items) {
        this.items = items;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpireMonth() {
        return expireMonth;
    }

    public void setExpireMonth(String expireMonth) {
        this.expireMonth = expireMonth;
    }

    public String getExpireYear() {
        return expireYear;
    }

    public void setExpireYear(String expireYear) {
        this.expireYear = expireYear;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }
}
